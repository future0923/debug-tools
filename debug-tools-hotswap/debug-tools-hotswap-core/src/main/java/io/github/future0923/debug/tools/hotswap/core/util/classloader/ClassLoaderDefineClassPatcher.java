/*
 * Copyright (C) 2024-2025 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package io.github.future0923.debug.tools.hotswap.core.util.classloader;

import io.github.future0923.debug.tools.base.logging.Logger;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.LoaderClassPath;
import javassist.NotFoundException;
import io.github.future0923.debug.tools.hotswap.core.util.scanner.ClassPathScanner;
import io.github.future0923.debug.tools.hotswap.core.util.scanner.Scanner;
import io.github.future0923.debug.tools.hotswap.core.util.scanner.ScannerVisitor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 插件的类必须要要AgentClassLoader中加载，否则将无法用在instrumentation进程
 * <p>
 * 如果传入解析路径的classloader与agentClassloader不一致，通过ClassLoaderDefineClassPatcher将插件的class从classLoader复制到agentClassLoader
 */
public class ClassLoaderDefineClassPatcher {

    private static final Logger LOGGER = Logger.getLogger(ClassLoaderDefineClassPatcher.class);

    private static final Map<String, List<byte[]>> pluginClassCache = new HashMap<>();

    /**
     * 将插件路径中的class从classLoaderFrom类加载器复制到classLoaderTo类加载器中
     *
     * @param classLoaderFrom  从哪个类加载器中加载
     * @param pluginPath       要复制的插件路径
     * @param classLoaderTo    复制到哪个类加载器中
     * @param protectionDomain 目标类加载器的保护域
     */
    public void patch(final ClassLoader classLoaderFrom, final String pluginPath,
                      final ClassLoader classLoaderTo, final ProtectionDomain protectionDomain) {

        List<byte[]> cache = getPluginCache(classLoaderFrom, pluginPath);

        if (cache != null) {

            final ClassPool cp = new ClassPool();
            cp.appendClassPath(new LoaderClassPath(getClass().getClassLoader()));
            Set<String> loadedClasses = new HashSet<>();
            String packagePrefix = pluginPath.replace('/', '.');

            for (byte[] pluginBytes : cache) {
                CtClass pluginClass = null;
                try {
                    // force to load class in classLoaderFrom (it may not yet be loaded) and if the classLoaderTo
                    // is parent of classLoaderFrom, after definition in classLoaderTo will classLoaderFrom return
                    // class from parent classloader instead own definition (hence change of behaviour).
                    InputStream is = new ByteArrayInputStream(pluginBytes);
                    pluginClass = cp.makeClass(is);
                    try {
                        classLoaderFrom.loadClass(pluginClass.getName());
                    } catch (NoClassDefFoundError e) {
                        LOGGER.trace("Skipping class loading {} in classloader {} - " +
                                "class has probably unresolvable dependency.", pluginClass.getName(), classLoaderTo);
                    }
                    // and load the class in classLoaderTo as well. Now the class is defined in BOTH classloaders.
                    transferTo(pluginClass, packagePrefix, classLoaderTo, protectionDomain, loadedClasses);
                } catch (CannotCompileException e) {
                    LOGGER.trace("Skipping class definition {} in app classloader {} - " +
                            "class is probably already defined.", pluginClass.getName(), classLoaderTo);
                } catch (NoClassDefFoundError e) {
                    LOGGER.trace("Skipping class definition {} in app classloader {} - " +
                            "class has probably unresolvable dependency.", pluginClass.getName(), classLoaderTo);
                } catch (Throwable e) {
                    LOGGER.trace("Skipping class definition app classloader {} - " +
                            "unknown error.", e, classLoaderTo);
                }
            }
        }

        LOGGER.debug("Classloader {} patched with plugin classes from agent classloader {}.", classLoaderTo, classLoaderFrom);

    }

    private void transferTo(CtClass pluginClass, String pluginPath, ClassLoader classLoaderTo,
                            ProtectionDomain protectionDomain, Set<String> loadedClasses) throws CannotCompileException {
        // if the class is already loaded, skip it
        if (loadedClasses.contains(pluginClass.getName()) || pluginClass.isFrozen() ||
                !pluginClass.getName().startsWith(pluginPath)) {
            return;
        }
        // 1. interface
        try {
            if (!pluginClass.isInterface()) {
                CtClass[] ctClasses = pluginClass.getInterfaces();
                if (ctClasses != null) {
                    for (CtClass ctClass : ctClasses) {
                        try {
                            transferTo(ctClass, pluginPath, classLoaderTo, protectionDomain, loadedClasses);
                        } catch (Throwable e) {
                            LOGGER.trace("Skipping class loading {} in classloader {} - " +
                                    "class has probably unresolvable dependency.", ctClass.getName(), classLoaderTo);
                        }
                    }
                }
            }
        } catch (NotFoundException e) {
        }
        // 2. superClass
        try {
            CtClass ctClass = pluginClass.getSuperclass();
            if (ctClass != null) {
                try {
                    transferTo(ctClass, pluginPath, classLoaderTo, protectionDomain, loadedClasses);
                } catch (Throwable e) {
                    LOGGER.trace("Skipping class loading {} in classloader {} - " +
                            "class has probably unresolvable dependency.", ctClass.getName(), classLoaderTo);
                }
            }
        } catch (NotFoundException e) {
        }
        pluginClass.toClass(classLoaderTo, protectionDomain);
        loadedClasses.add(pluginClass.getName());
    }

    private List<byte[]> getPluginCache(final ClassLoader classLoaderFrom, final String pluginPath) {
        List<byte[]> ret = null;
        synchronized (pluginClassCache) {
            ret = pluginClassCache.get(pluginPath);
            if (ret == null) {
                final List<byte[]> retList = new ArrayList<>();
                Scanner scanner = new ClassPathScanner();
                try {
                    scanner.scan(classLoaderFrom, pluginPath, new ScannerVisitor() {
                        @Override
                        public void visit(InputStream file) throws IOException {

                            // skip plugin classes
                            // TODO this should be skipped only in patching application classloader. To copy
                            // classes into agent classloader, Plugin class must be copied as well
                            //                        if (patchClass.hasAnnotation(Plugin.class)) {
                            //                            LOGGER.trace("Skipping plugin class: " + patchClass.getName());
                            //                            return;
                            //                        }

                            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

                            int readBytes;
                            byte[] data = new byte[16384];

                            while ((readBytes = file.read(data, 0, data.length)) != -1) {
                                buffer.write(data, 0, readBytes);
                            }

                            buffer.flush();
                            retList.add(buffer.toByteArray());
                        }

                    });
                } catch (IOException e) {
                    LOGGER.error("Exception while scanning 'org/hotswap/agent/plugin'", e);
                }
                ret = retList;
                pluginClassCache.put(pluginPath, ret);
            }
        }
        return ret;
    }

    /**
     * Check if the classloader can be patched.
     * Typically skip synthetic classloaders.
     *
     * @param classLoader classloader to check
     * @return if true, call patch()
     */
    public boolean isPatchAvailable(ClassLoader classLoader) {
        // we can define class in any class loader
        // exclude synthetic classloader where it does not make any sense

        // sun.reflect.DelegatingClassLoader - created automatically by JVM to optimize reflection calls
        return classLoader != null &&
                !classLoader.getClass().getName().equals("sun.reflect.DelegatingClassLoader") &&
                !classLoader.getClass().getName().equals("jdk.internal.reflect.DelegatingClassLoader");
    }
}
