/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.future0923.debug.tools.hotswap.core.util.classloader;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.config.PluginManager;
import io.github.future0923.debug.tools.hotswap.core.javassist.util.proxy.MethodHandler;
import io.github.future0923.debug.tools.hotswap.core.javassist.util.proxy.Proxy;
import io.github.future0923.debug.tools.hotswap.core.javassist.util.proxy.ProxyFactory;
import io.github.future0923.debug.tools.hotswap.core.javassist.util.proxy.ProxyObject;
import lombok.Getter;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessControlContext;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

/**
 * 对UrlClassLoader进行字节码增强，使其可以加载外部的class或jar
 */
public class URLClassLoaderPathHelper {

    private static final Logger logger = Logger.getLogger(URLClassLoaderPathHelper.class);

    /**
     * Javassist创建的UrlClassPath代理类
     */
    private static Class<?> urlClassPathProxyClass = null;

    static {
        Class<?> urlClassPathClass = null;
        ClassLoader classLoader = URLClassLoaderPathHelper.class.getClassLoader();
        try {
            urlClassPathClass = classLoader.loadClass("sun.misc.URLClassPath");
        } catch (ClassNotFoundException e) {
            try {
                // java9
                urlClassPathClass = classLoader.loadClass("jdk.internal.loader.URLClassPath");
            } catch (ClassNotFoundException e1) {
                logger.error("Unable to loadClass URLClassPath!");
            }
        }
        if (urlClassPathClass != null) {
            ProxyFactory f = new ProxyFactory();
            f.setSuperclass(urlClassPathClass);
            f.setFilter(m -> !m.getName().equals("finalize"));
            urlClassPathProxyClass = f.createClass();
        }
    }

    /**
     * 给指定的类加载器插入扩展的类加载路径。
     * 通过重新定义 UCP 字段。现有的类加载器都会被重新创建
     *
     * @param classLoader    要添加的类加载器
     */
    public static void prependClassPath(final ClassLoader classLoader) {
        prependClassPath(classLoader, PluginManager.getInstance().getPluginConfiguration(classLoader).getExtraClasspath());
    }

    /**
     * 给指定的类加载器插入扩展的类加载路径。
     * 通过重新定义 UCP 字段。现有的类加载器都会被重新创建
     *
     * @param classLoader    要添加的类加载器
     * @param extraClassPath 要添加的路径
     */
    public static void prependClassPath(final ClassLoader classLoader, URL[] extraClassPath) {
        synchronized (classLoader) {
            try {
                 Field ucpField = getUcpField(classLoader);
                if (ucpField == null) {
                    logger.debug("Unable to find ucp field in classLoader {}", classLoader);
                    return;
                }
                ucpField.setAccessible(true);
                URL[] origClassPath = getOriginalClassPath(classLoader, ucpField);
                URL[] modifiedClassPath = new URL[origClassPath.length + extraClassPath.length];
                System.arraycopy(extraClassPath, 0, modifiedClassPath, 0, extraClassPath.length);
                System.arraycopy(origClassPath, 0, modifiedClassPath, extraClassPath.length, origClassPath.length);
                Object urlClassPath = createClassPathInstance(modifiedClassPath);
                // 创建方法拦截处理器
                ExtraURLClassPathMethodHandler methodHandler = new ExtraURLClassPathMethodHandler(modifiedClassPath);
                ((Proxy) urlClassPath).setHandler(methodHandler);
                // 处理当前类和父类的ucp字段
                setUcpFieldOfAllClassLoader(classLoader, ucpField, urlClassPath);
                logger.info("Added extraClassPath URLs {} to classLoader {}", Arrays.toString(extraClassPath), classLoader);
            } catch (Exception e) {
                logger.error("Unable to add extraClassPath URLs {} to classLoader {}", e, Arrays.toString(extraClassPath), classLoader);
            }
        }
    }

    /**
     * 通过 WatchResourceLoader 的 ucp 更改 classLoader 的 ucp 字段
     */
    public static void setWatchResourceLoader(ClassLoader classLoader, final ClassLoader watchResourceLoader) {
        synchronized (classLoader) {
            try {
                Field ucpField = getUcpField(classLoader);
                if (ucpField == null) {
                    logger.debug("Unable to find ucp field in classLoader {}", classLoader);
                    return;
                }
                ucpField.setAccessible(true);
                URL[] origClassPath = getOriginalClassPath(classLoader, ucpField);
                Object urlClassPath = createClassPathInstance(origClassPath);
                ExtraURLClassPathMethodHandler methodHandler = new ExtraURLClassPathMethodHandler(origClassPath, watchResourceLoader);
                ((Proxy) urlClassPath).setHandler(methodHandler);
                setUcpFieldOfAllClassLoader(classLoader, ucpField, urlClassPath);
                logger.debug("WatchResourceLoader registered to classLoader {}", classLoader);
            } catch (Exception e) {
                logger.debug("Unable to register WatchResourceLoader to classLoader {}", e, classLoader);
            }
        }
    }

    /**
     * 创建ClassPath实例
     */
    private static Object createClassPathInstance(URL[] urls) throws Exception {
        try {
            // java8
            Constructor<?> constr = urlClassPathProxyClass.getConstructor(URL[].class);
            return constr.newInstance(new Object[]{urls});
        } catch (NoSuchMethodException e) {
            // java9
            Constructor<?> constr = urlClassPathProxyClass.getConstructor(URL[].class, AccessControlContext.class);
            return constr.newInstance(urls, null);
        }
    }

    /**
     * 获取ClassLoader原始的ClassPath
     */
    @SuppressWarnings("unchecked")
    private static URL[] getOriginalClassPath(ClassLoader classLoader, Field ucpField) throws IllegalAccessException, NoSuchFieldException {
        URL[] origClassPath = null;
        Object urlClassPath = ucpField.get(classLoader);
        if (urlClassPath instanceof ProxyObject) {
            ProxyObject p = (ProxyObject) urlClassPath;
            MethodHandler handler = p.getHandler();
            if (handler instanceof ExtraURLClassPathMethodHandler) {
                origClassPath = ((ExtraURLClassPathMethodHandler) handler).getOriginalClassPath();
            }
        } else {
            if (classLoader instanceof URLClassLoader) {
                origClassPath = ((URLClassLoader) classLoader).getURLs();
            } else {
                Field pathField = ucpField.getType().getDeclaredField("path");
                pathField.setAccessible(true);
                List<URL> urls = (List<URL>) pathField.get(urlClassPath);
                origClassPath = urls.toArray(new URL[0]);
            }
        }
        return origClassPath;
    }

    /**
     * 获取类加载器的ucp字段
     * <li>
     *     <ul>JDK 8: sun.misc.Launcher$AppClassLoaders</ul>
     *     <ul>JDK 9+: jdk.internal.loader.ClassLoaders$AppClassLoader</ul>
     * </li>
     * 9以上需要添加以下参数
     * <pre>
     *     --add-opens java.base/jdk.internal.loader=ALL-UNNAMED
     * </pre>
     */
    private static Field getUcpField(ClassLoader classLoader) throws NoSuchFieldException {
        if (classLoader instanceof URLClassLoader) {
            return URLClassLoader.class.getDeclaredField("ucp");
        }
        Class<?> ucpOwner = classLoader.getClass();
        if (ucpOwner.getName().startsWith("jdk.internal.loader.ClassLoaders$")) {
            try {
                return ucpOwner.getDeclaredField("ucp");
            } catch (NoSuchFieldException e) {
                return ucpOwner.getSuperclass().getDeclaredField("ucp");
            }
        }
        return null;
    }

    /**
     *  jdk.internal.loader.ClassLoaders.AppClassLoader 的父类 'jdk.internal.loader.BuiltinClassLoader' 也有一个 ucp 字段。
     *  这个字段是final和private，如果需要修改，它需要在所有super classes和当前类中进行修改。
     */
    private static void setUcpFieldOfAllClassLoader(ClassLoader classLoader, Field ucpField, Object urlClassPath) throws IllegalAccessException {
        // 1. 设置当前类的 ucp 字段
        ucpField.set(classLoader, urlClassPath);
        // 2. 设置所有父类的 ucp 字段
        Class<?> currentClass = classLoader.getClass();
        while ((currentClass = currentClass.getSuperclass()) != null) {
            try {
                Field field = currentClass.getDeclaredField("ucp");
                field.setAccessible(true);
                field.set(classLoader, urlClassPath);
            } catch (NoSuchFieldException e) {
                break;
            }
        }
    }

    public static boolean isApplicable(ClassLoader classLoader) {
        if (classLoader == null) {
            return false;
        }
        if (classLoader instanceof URLClassLoader) {
            return true;
        }
        Class<?> ucpOwner = classLoader.getClass();
        return ucpOwner.getName().startsWith("jdk.internal.loader.ClassLoaders$");
    }

    /**
     * 拦截 findResource 和 findResources 方法，当 watchResourceLoader 能加载到时，使用 watchResourceLoader 加载。
     */
    public static class ExtraURLClassPathMethodHandler implements MethodHandler {

        private ClassLoader watchResourceLoader;

        /**
         * 原始的ClassPath
         */
        @Getter
        private final URL[] originalClassPath;

        public ExtraURLClassPathMethodHandler(URL[] originalClassPath) {
            this.originalClassPath = originalClassPath;
        }

        public ExtraURLClassPathMethodHandler(URL[] originalClassPath, ClassLoader watchResourceLoader) {
            this.originalClassPath = originalClassPath;
            this.watchResourceLoader = watchResourceLoader;
        }

        /**
         * 当 watchResourceLoader 能加载到时，使用 watchResourceLoader 加载。
         */
        public Object invoke(Object self, Method method, Method proceed, Object[] args) throws Throwable {
            String methodName = method.getName();
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (watchResourceLoader != null) {
                if ("findResource".equals(methodName)
                        && parameterTypes.length == 2
                        && parameterTypes[0] == String.class
                        && (parameterTypes[1] == Boolean.TYPE || parameterTypes[1] == Boolean.class)) {
                    URL resource = watchResourceLoader.getResource((String) args[0]);
                    if (resource != null) {
                        return resource;
                    }
                } else if ("findResources".equals(methodName)
                        && parameterTypes.length == 2
                        && parameterTypes[0] == String.class
                        && (parameterTypes[1] == Boolean.TYPE || parameterTypes[1] == Boolean.class)) {
                    try {
                        Enumeration<URL> resources = watchResourceLoader.getResources((String) args[0]);
                        if (resources != null && resources.hasMoreElements()) {
                            return resources;
                        }
                    } catch (IOException e) {
                        logger.debug("Unable to load resource {}", e, args[0]);
                    }
                }
            }
            return proceed.invoke(self, args);
        }

    }

}
