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
package io.github.future0923.debug.tools.hotswap.core.plugin.proxy;

import io.github.future0923.debug.tools.base.constants.ProjectConstants;
import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.annotation.LoadEvent;
import io.github.future0923.debug.tools.hotswap.core.annotation.OnClassLoadEvent;
import io.github.future0923.debug.tools.hotswap.core.annotation.Plugin;
import io.github.future0923.debug.tools.hotswap.core.config.PluginManager;
import io.github.future0923.debug.tools.hotswap.core.javassist.ClassPool;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.plugin.proxy.command.RedefinitionScheduler;
import io.github.future0923.debug.tools.hotswap.core.plugin.proxy.command.ReloadJavaProxyCommand;
import io.github.future0923.debug.tools.hotswap.core.plugin.proxy.hscglib.CglibEnhancerProxyTransformer;
import io.github.future0923.debug.tools.hotswap.core.plugin.proxy.hscglib.CglibProxyTransformer;
import io.github.future0923.debug.tools.hotswap.core.plugin.proxy.hscglib.GeneratorParametersTransformer;
import io.github.future0923.debug.tools.hotswap.core.plugin.proxy.hscglib.GeneratorParams;
import io.github.future0923.debug.tools.hotswap.core.plugin.proxy.utils.ProxyClassSignatureHelper;
import io.github.future0923.debug.tools.hotswap.core.util.ReflectionHelper;
import io.github.future0923.debug.tools.hotswap.core.util.classloader.ClassLoaderHelper;
import io.github.future0923.debug.tools.hotswap.core.watch.WatcherFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author future0923
 */
@Plugin(
        name = "Proxy",
        description = "Redefines proxies",
        testedVersions = { "" },
        expectedVersions = { "all" },
        supportClass = RedefinitionScheduler.class
)
public class ProxyPlugin {

    private static final Logger LOGGER = Logger.getLogger(ProxyPlugin.class);

    private static final boolean isJava8OrNewer = WatcherFactory.JAVA_VERSION >= 18;

    /**
     * 重载状态的标志。
     */
    public static boolean reloadFlag = false;

    private static final Set<String> proxyRedefiningMap = new HashSet<>();

    @OnClassLoadEvent(classNameRegexp = "(jdk.proxy\\d+.\\$Proxy.*)|(com.sun.proxy.\\$Proxy.*)", events = LoadEvent.REDEFINE, skipSynthetic = false)
    public static void transformJavaProxy(final Class<?> classBeingRedefined, final ClassLoader classLoader) {
        if (ProjectConstants.DEBUG) {
            LOGGER.info("redefine class {}", classBeingRedefined.getName());
        }
        // 在这个方法中无法直接重新定义代理（并返回新的代理类字节码），因为类加载器中包含了代理接口的旧定义。因此，在DCEVM中重新定义代理接口后，代理是在延迟命令中定义的（经过一些延迟）。
        Object proxyCache = ReflectionHelper.getNoException(null, java.lang.reflect.Proxy.class, "proxyCache");
        if (proxyCache != null) {
            try {
                ReflectionHelper.invoke(proxyCache, proxyCache.getClass().getSuperclass(), "removeAll", new Class[] { ClassLoader.class }, classLoader);
            } catch (IllegalArgumentException e) {
                LOGGER.error("Reflection proxy cache flush failed. {}", e.getMessage());
            }
        }

        if (!ClassLoaderHelper.isClassLoaderStarted(classLoader)) {
            return;
        }

        final String className = classBeingRedefined.getName();

        if (proxyRedefiningMap.contains(className)) {
            proxyRedefiningMap.remove(className);
            return;
        }

        proxyRedefiningMap.add(className);

        final Map<String, String> signatureMapOrig = ProxyClassSignatureHelper.getNonSyntheticSignatureMap(classBeingRedefined);

        reloadFlag = true;

        PluginManager.getInstance().getScheduler().scheduleCommand(new ReloadJavaProxyCommand(classLoader, className, signatureMapOrig), 50);
    }

    @OnClassLoadEvent(classNameRegexp = ".*", events = LoadEvent.REDEFINE, skipSynthetic = false)
    public static byte[] transformCglibProxy(final Class<?> classBeingRedefined,
                                             final byte[] classfileBuffer,
                                             final ClassLoader loader,
                                             final ClassPool cp) throws Exception {
        if (ProjectConstants.DEBUG) {
            LOGGER.info("redefine class {}", classBeingRedefined.getName());
        }
        GeneratorParams generatorParams = GeneratorParametersTransformer.getGeneratorParams(loader, classBeingRedefined.getName());

        if (generatorParams == null) {
            return classfileBuffer;
        }

        if (!ClassLoaderHelper.isClassLoaderStarted(loader)) {
            return classfileBuffer;
        }

        loader.loadClass("java.beans.Introspector").getMethod("flushCaches").invoke(null);
        if (generatorParams.getParam().getClass().getName().endsWith(".Enhancer")) {
            try {
                return CglibEnhancerProxyTransformer.transform(classBeingRedefined, cp, classfileBuffer, loader, generatorParams);
            } catch (Exception e) {
                LOGGER.error("Error redefining Cglib Enhancer proxy {}", e, classBeingRedefined.getName());
            }
        }

        // Multistep transformation crashed jvm in java8 u05
        if (!isJava8OrNewer) {
            try {
                return CglibProxyTransformer.transform(classBeingRedefined, cp, classfileBuffer, generatorParams);
            }
            catch (Exception e) {
                LOGGER.error("Error redefining Cglib proxy {}", e, classBeingRedefined.getName());
            }
        }

        return classfileBuffer;
    }

    /**
     * 修改 Cglib 字节码生成器以存储此插件的参数。
     *
     * @throws Exception
     */
    @OnClassLoadEvent(classNameRegexp = ".*/cglib/.*", skipSynthetic = false)
    public static CtClass transformDefinitions(CtClass cc) throws Exception {
        try {
            return GeneratorParametersTransformer.transform(cc);
        } catch (Exception e) {
            LOGGER.error("Error modifying class for cglib proxy creation parameter recording", e);
        }
        return cc;
    }
}
