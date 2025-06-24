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
package io.github.future0923.debug.tools.hotswap.core.config;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.HotswapAgent;
import io.github.future0923.debug.tools.hotswap.core.annotation.Plugin;
import io.github.future0923.debug.tools.hotswap.core.annotation.handler.AnnotationProcessor;
import io.github.future0923.debug.tools.hotswap.core.util.classloader.ClassLoaderDefineClassPatcher;
import io.github.future0923.debug.tools.hotswap.core.util.scanner.ClassPathAnnotationScanner;
import io.github.future0923.debug.tools.hotswap.core.util.scanner.ClassPathScanner;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 插件注册表
 */
public class PluginRegistry {

    private static final Logger LOGGER = Logger.getLogger(PluginRegistry.class);

    /**
     * plugin class -> Map (ClassLoader -> Plugin instance)
     */
    @Getter
    protected final Map<Class<?>, Map<ClassLoader, Object>> registeredPlugins = Collections.synchronizedMap(new HashMap<>());

    /**
     * 插件管理器
     */
    private final PluginManager pluginManager;

    /**
     * ClassPath注解扫描器，用于扫描插件
     */
    @Setter
    private ClassPathAnnotationScanner annotationScanner;

    /**
     * 解析插件类上的注解
     */
    @Setter
    protected AnnotationProcessor annotationProcessor;

    /**
     * 如果插件加载类不是 agentClassLoader，则需要通过ClassLoaderDefineClassPatcher将插件的class从classLoader复制到agentClassLoader
     */
    @Setter
    private ClassLoaderDefineClassPatcher classLoaderPatcher;

    public PluginRegistry(PluginManager pluginManager, ClassLoaderDefineClassPatcher classLoaderPatcher) {
        this.pluginManager = pluginManager;
        this.classLoaderPatcher = classLoaderPatcher;
        annotationScanner = new ClassPathAnnotationScanner(Plugin.class.getName(), new ClassPathScanner());
        annotationProcessor = new AnnotationProcessor(pluginManager);
    }

    /**
     * 扫描指定路径下带有{@link Plugin}注解的类，并处理注解功能
     *
     * @param classLoader   用哪个classloader解析插件路径
     * @param pluginPackage 要扫描的包（带.全路径）
     */
    public void scanPlugins(ClassLoader classLoader, String pluginPackage) {
        String pluginPath = pluginPackage.replace(".", "/");
        ClassLoader agentClassLoader = getClass().getClassLoader();

        try {
            List<String> discoveredPlugins = annotationScanner.scanPlugins(classLoader, pluginPath);
            List<String> discoveredPluginNames = new ArrayList<>();

            // 插件的类必须要要AgentClassLoader中加载，否则将无法用在instrumentation进程
            // 如果传入解析路径的classloader与agentClassloader不一致，通过ClassLoaderDefineClassPatcher将插件的class从classLoader复制到agentClassLoader
            if (!discoveredPlugins.isEmpty() && agentClassLoader != classLoader) {
                classLoaderPatcher.patch(classLoader, pluginPath, agentClassLoader, null);
            }

            for (String discoveredPlugin : discoveredPlugins) {
                // 通过classLoaderPatcher已经保证agentClassLoader中有插件的class。
                Class<?> pluginClass = Class.forName(discoveredPlugin, true, agentClassLoader);
                Plugin pluginAnnotation = pluginClass.getAnnotation(Plugin.class);

                if (pluginAnnotation == null) {
                    LOGGER.error("Scanner discovered plugin class {} which does not contain @Plugin annotation.", pluginClass);
                    continue;
                }
                String pluginName = pluginAnnotation.name();

                if (HotswapAgent.isPluginDisabled(pluginName)) {
                    LOGGER.debug("Plugin {} is disabled, skipping...", pluginName);
                    continue;
                }

                if (registeredPlugins.containsKey(pluginClass))
                    continue;

                registeredPlugins.put(pluginClass, Collections.synchronizedMap(new HashMap<>()));

                if (annotationProcessor.processAnnotations(pluginClass, pluginClass)) {
                    LOGGER.debug("Plugin registered {}.", pluginClass);
                } else {
                    LOGGER.error("Error processing annotations for plugin {}. Plugin was unregistered.", pluginClass);
                    registeredPlugins.remove(pluginClass);
                }

                discoveredPluginNames.add(pluginName);
            }

            LOGGER.info("Discovered plugins: " + Arrays.toString(discoveredPluginNames.toArray()));

        } catch (Exception e) {
            LOGGER.error("Error in plugin initial processing for plugin package '{}'", e, pluginPackage);
        }
    }

    /**
     * 在指定的ClassLoader中初始化创建插件实例，一个ClassLoader中只有一个插件实例
     *
     * @return 新创建的插件实例（可能null）
     */
    public Object initializePlugin(String pluginClass, ClassLoader appClassLoader) {
        if (appClassLoader == null)
            throw new IllegalArgumentException("Cannot initialize plugin '" + pluginClass + "', appClassLoader is null.");

        // 初始化类加载器
        pluginManager.initClassLoader(appClassLoader);

        Class<Object> clazz = getPluginClass(pluginClass);

        // 是否被禁用
        if (pluginManager.getPluginConfiguration(appClassLoader).isDisabledPlugin(clazz)) {
            LOGGER.debug("Plugin {} disabled in classloader {}.", clazz, appClassLoader);
            return null;
        }

        // 已经在当前类加载器或父类加载器中初始化
        if (doHasPlugin(clazz, appClassLoader, false, true)) {
            LOGGER.debug("Plugin {} already initialized in parent classloader of {}.", clazz, appClassLoader);
            return getPlugin(clazz, appClassLoader);
        }

        Object pluginInstance = registeredPlugins.get(clazz).get(appClassLoader);

        if (annotationProcessor.processAnnotations(pluginInstance)) {
            LOGGER.info("Plugin '{}' initialized in ClassLoader '{}'.", pluginClass, appClassLoader);
        } else {
            LOGGER.error("Plugin '{}' NOT initialized in ClassLoader '{}', error while processing annotations.", pluginClass, appClassLoader);
            registeredPlugins.get(clazz).remove(appClassLoader);
        }

        return pluginInstance;
    }

    public void initializePluginInstance(Object pluginInstance) {
        registeredPlugins.put(pluginInstance.getClass(),
                Collections.singletonMap(pluginInstance.getClass().getClassLoader(), pluginInstance));
        if (!annotationProcessor.processAnnotations(pluginInstance)) {
            throw new IllegalStateException("Unable to initialize plugin");
        }

    }

    /**
     * 通过插件Class和类加载器获取插件实例
     *
     * @return 插件实例
     * @throws IllegalArgumentException ClassLoader未初始化或没找到插件
     */
    @SuppressWarnings("unchecked")
    public <T> T getPlugin(Class<T> pluginClass, ClassLoader classLoader) {
        if (registeredPlugins.isEmpty()) {
            throw new IllegalStateException("No plugin initialized. " +
                    "The Hotswap Agent JAR must NOT be in app classloader (only registered as --javaagent: startup parameter). " +
                    "Please check your mapPreviousState.");
        }

        if (!registeredPlugins.containsKey(pluginClass))
            throw new IllegalArgumentException(String.format("Plugin %s is not known to the registry.", pluginClass));

        Map<ClassLoader, Object> pluginInstances = registeredPlugins.get(pluginClass);
        synchronized (pluginInstances) {
            for (Map.Entry<ClassLoader, Object> registeredClassLoaderEntry : pluginInstances.entrySet()) {
                if (isParentClassLoader(registeredClassLoaderEntry.getKey(), classLoader)) {
                    return (T) registeredClassLoaderEntry.getValue();
                }
            }
        }
        throw new IllegalArgumentException(String.format("Plugin %s is not initialized in classloader %s.", pluginClass, classLoader));
    }

    /**
     * 插件是否已经在指定的ClassLoader中初始化
     *
     * @param checkParent 是否检查父类加载器
     */
    public boolean hasPlugin(Class<?> pluginClass, ClassLoader classLoader, boolean checkParent) {
        return doHasPlugin(pluginClass, classLoader, checkParent, false);
    }

    /**
     * 插件是否已经在指定的ClassLoader中初始化
     *
     * @param checkParent     是否检查父类加载器
     * @param createIfMissing 不存在是否创建
     */
    public boolean doHasPlugin(Class<?> pluginClass, ClassLoader classLoader, boolean checkParent, boolean createIfMissing) {
        if (!registeredPlugins.containsKey(pluginClass))
            return false;

        Map<ClassLoader, Object> pluginInstances = registeredPlugins.get(pluginClass);
        synchronized (pluginInstances) {
            for (Map.Entry<ClassLoader, Object> registeredClassLoaderEntry : pluginInstances.entrySet()) {
                if (checkParent && isParentClassLoader(registeredClassLoaderEntry.getKey(), classLoader)) {
                    return true;
                } else if (registeredClassLoaderEntry.getKey().equals(classLoader)) {
                    return true;
                }
            }
            if (createIfMissing) {
                Object pluginInstance = instantiate(pluginClass);
                pluginInstances.put(classLoader, pluginInstance);
            }
        }
        return false;
    }

    /**
     * 获取插件的类加载器
     */
    public ClassLoader getAppClassLoader(Object plugin) {
        Class<Object> clazz = getPluginClass(plugin.getClass().getName());
        Map<ClassLoader, Object> pluginInstances = registeredPlugins.get(clazz);
        if (pluginInstances != null) {
            synchronized (pluginInstances) {
                for (Map.Entry<ClassLoader, Object> entry : pluginInstances.entrySet()) {
                    if (entry.getValue().equals(plugin))
                        return entry.getKey();
                }
            }
        }
        throw new IllegalArgumentException("Plugin not found in the registry " + plugin);
    }


    /**
     * 获取插件类Class信息，插件类文件应该与PluginRegistry的Class在一个类加载器中
     */
    @SuppressWarnings("unchecked")
    protected Class<Object> getPluginClass(String pluginClass) {
        try {
            if (getClass().getClassLoader() == null) {
                return (Class<Object>) Class.forName(pluginClass, true, null);
            }
            return (Class<Object>) getClass().getClassLoader().loadClass(pluginClass);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Plugin class not found " + pluginClass, e);
        }
    }

    /**
     * parentClassLoader是否是classLoader的父类加载器
     */
    private boolean isParentClassLoader(ClassLoader parentClassLoader, ClassLoader classLoader) {
        if (parentClassLoader.equals(classLoader)) {
            return true;
        } else if (classLoader.getParent() != null) {
            return isParentClassLoader(parentClassLoader, classLoader.getParent());
        } else {
            return false;
        }
    }

    /**
     * 创建插件实例
     */
    protected Object instantiate(Class<?> plugin) {
        try {
            return plugin.newInstance();
        } catch (InstantiationException e) {
            LOGGER.error("Error instantiating plugin: " + plugin.getName(), e);
        } catch (IllegalAccessException e) {
            LOGGER.error("Plugin: " + plugin.getName()
                    + " does not contain public no param constructor", e);
        }
        return null;
    }

    /**
     * 移除ClassLoader中的所有插件
     */
    public void closeClassLoader(ClassLoader classLoader) {
        LOGGER.debug("Closing classloader {}.", classLoader);
        synchronized (registeredPlugins) {
            for (Map<ClassLoader, Object> plugins : registeredPlugins.values()) {
                plugins.remove(classLoader);
            }
        }
    }
}
