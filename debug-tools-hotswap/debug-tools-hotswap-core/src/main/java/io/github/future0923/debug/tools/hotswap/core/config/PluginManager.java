/*
 * Copyright 2013-2024 the HotswapAgent authors.
 *
 * This file is part of HotswapAgent.
 *
 * HotswapAgent is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 2 of the License, or (at your
 * option) any later version.
 *
 * HotswapAgent is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with HotswapAgent. If not, see http://www.gnu.org/licenses/.
 */
package io.github.future0923.debug.tools.hotswap.core.config;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.command.Scheduler;
import io.github.future0923.debug.tools.hotswap.core.command.impl.SchedulerImpl;
import io.github.future0923.debug.tools.hotswap.core.util.HotswapTransformer;
import io.github.future0923.debug.tools.hotswap.core.util.classloader.ClassLoaderDefineClassPatcher;
import io.github.future0923.debug.tools.hotswap.core.watch.Watcher;
import io.github.future0923.debug.tools.hotswap.core.watch.WatcherFactory;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 插件管理器
 */
public class PluginManager {

    private static final Logger logger = Logger.getLogger(PluginManager.class);

    /**
     * 扫描的插件包路径
     */
    public static final String PLUGIN_PACKAGE = "io.github.future0923.debug.tools.hotswap.core.plugin";

    //////////////////////////   MANAGER SINGLETON /////////////////////////////////////

    // 单例
    private static final PluginManager INSTANCE = new PluginManager();

    /**
     * 获取插件管理器的单例实例
     */
    public static PluginManager getInstance() {
        return INSTANCE;
    }

    private PluginManager() {
        hotswapTransformer = new HotswapTransformer();
        pluginRegistry = new PluginRegistry(this, classLoaderPatcher);
    }

    @Getter
    private Instrumentation instrumentation;

    private final Object hotswapLock = new Object();

    //////////////////////////   PLUGINS /////////////////////////////////////

    /**
     * Returns a plugin instance by its type and classLoader.
     *
     * @param clazz       type name of the plugin (IllegalArgumentException class is not known to the classLoader)
     * @param classLoader classloader of the plugin
     * @return plugin instance or null if not found
     */
    public Object getPlugin(String clazz, ClassLoader classLoader) {
        try {
            return getPlugin(Class.forName(clazz), classLoader);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Plugin class not found " + clazz, e);
        }
    }

    /**
     * Returns a plugin instance by its type and classLoader.
     *
     * @param clazz       type of the plugin
     * @param classLoader classloader of the plugin
     * @param <T>         type of the plugin to return correct instance.
     * @return the plugin or null if not found.
     */
    public <T> T getPlugin(Class<T> clazz, ClassLoader classLoader) {
        return pluginRegistry.getPlugin(clazz, classLoader);
    }

    /**
     * Check if plugin is initialized in classLoader.
     *
     * @param pluginClassName type of the plugin
     * @param classLoader classloader of the plugin
     * @return true/false
     */
    public boolean isPluginInitialized(String pluginClassName, ClassLoader classLoader) {
        Class<Object> pluginClass = pluginRegistry.getPluginClass(pluginClassName);
        return pluginClass != null && pluginRegistry.hasPlugin(pluginClass, classLoader, false);
    }

    /**
     * Initialize the singleton plugin manager.
     * <ul>
     * <li>Create new resource watcher using WatcherFactory and start it in separate thread.</li>
     * <li>Create new scheduler and start it in separate thread.</li>
     * <li>Scan for plugins</li>
     * <li>Register HotswapTransformer with the javaagent instrumentation class</li>
     * </ul>
     *
     * @param instrumentation javaagent instrumentation.
     */
    public void init(Instrumentation instrumentation) {
        this.instrumentation = instrumentation;

        if (watcher == null) {
            try {
                watcher = new WatcherFactory().getWatcher();
            } catch (IOException e) {
                logger.debug("Unable to create default watcher.", e);
            }
        }
        watcher.run();

        if (scheduler == null) {
            scheduler = new SchedulerImpl();
        }
        scheduler.run();

        // create default configuration from this classloader
        ClassLoader classLoader = getClass().getClassLoader();

        classLoaderConfigurations.put(classLoader, new PluginConfiguration(classLoader));

        // 扫描插件
        pluginRegistry.scanPlugins(getClass().getClassLoader(), PLUGIN_PACKAGE);

        // 注册转换器
        instrumentation.addTransformer(hotswapTransformer);
    }

    ClassLoaderDefineClassPatcher classLoaderPatcher = new ClassLoaderDefineClassPatcher();
    Map<ClassLoader, PluginConfiguration> classLoaderConfigurations = new HashMap<>();
    Set<ClassLoaderInitListener> classLoaderInitListeners = new HashSet<>();

    public void registerClassLoaderInitListener(ClassLoaderInitListener classLoaderInitListener) {
        classLoaderInitListeners.add(classLoaderInitListener);

        // call init on this classloader immediately, because it is already initialized
        classLoaderInitListener.onInit(getClass().getClassLoader());
    }

    public void initClassLoader(ClassLoader classLoader) {
        // use default protection domain
        initClassLoader(classLoader, classLoader.getClass().getProtectionDomain());
    }

    public void initClassLoader(ClassLoader classLoader, ProtectionDomain protectionDomain) {
        // 存在说明ClassLoader中已经初始化过了，直接退出
        if (classLoaderConfigurations.containsKey(classLoader))
            return;

        // parent of current classloader (system/bootstrap)
        if (getClass().getClassLoader() != null &&
            classLoader != null &&
            classLoader.equals(getClass().getClassLoader().getParent()))
            return;

        // synchronize ClassLoader patching - multiple classloaders may be patched at the same time
        // and they may synchronize loading for security reasons and introduce deadlocks
        synchronized (this) {
            if (classLoaderConfigurations.containsKey(classLoader))
                return;

            // transformation
            if (classLoader != null && classLoaderPatcher.isPatchAvailable(classLoader)) {
                classLoaderPatcher.patch(getClass().getClassLoader(), PLUGIN_PACKAGE.replace(".", "/"),
                        classLoader, protectionDomain);
            }

            // create new configuration for the classloader
            PluginConfiguration pluginConfiguration = new PluginConfiguration(getPluginConfiguration(getClass().getClassLoader()), classLoader, false);
            classLoaderConfigurations.put(classLoader, pluginConfiguration);
            pluginConfiguration.init();
        }

        // call listeners
        for (ClassLoaderInitListener classLoaderInitListener : classLoaderInitListeners)
            classLoaderInitListener.onInit(classLoader);
    }

    /**
     * Remove any classloader reference and close all plugin instances associated with classloader.
     * This method is called typically after webapp undeploy.
     *
     * @param classLoader the classloader to cleanup
     */
    public void closeClassLoader(ClassLoader classLoader) {
        pluginRegistry.closeClassLoader(classLoader);
        classLoaderConfigurations.remove(classLoader);
        hotswapTransformer.closeClassLoader(classLoader);
    }


    public PluginConfiguration getPluginConfiguration(ClassLoader classLoader) {
        // if needed, iterate to first parent loader with a known configuration
        ClassLoader loader = classLoader;
        while (loader != null && !classLoaderConfigurations.containsKey(loader))
            loader = loader.getParent();

        return classLoaderConfigurations.get(loader);
    }

    //////////////////////////   AGENT SERVICES /////////////////////////////////////

    @Setter
    @Getter
    private PluginRegistry pluginRegistry;

    /**
     * 热重载转换器
     */
    @Getter
    protected HotswapTransformer hotswapTransformer;

    @Getter
    protected Watcher watcher;

    @Getter
    protected Scheduler scheduler;

    /**
     * Redefine the supplied set of classes using the supplied bytecode.
     *
     * This method operates on a set in order to allow interdependent changes to more than one class at the same time
     * (a redefinition of class A can require a redefinition of class B).
     *
     * @param reloadMap class -> new bytecode
     * @see Instrumentation#redefineClasses(ClassDefinition...)
     */
    public void hotswap(Map<Class<?>, byte[]> reloadMap) {
        if (instrumentation == null) {
            throw new IllegalStateException("Plugin manager is not correctly initialized - no instrumentation available.");
        }

        synchronized (reloadMap) {
            ClassDefinition[] definitions = new ClassDefinition[reloadMap.size()];
            String[] classNames = new String[reloadMap.size()];
            int i = 0;
            for (Map.Entry<Class<?>, byte[]> entry : reloadMap.entrySet()) {
                classNames[i] = entry.getKey().getName();
                definitions[i++] = new ClassDefinition(entry.getKey(), entry.getValue());
            }
            try {
                logger.reload("Reloading classes {} (autoHotswap)", Arrays.toString(classNames));
                synchronized (hotswapLock) {
                    instrumentation.redefineClasses(definitions);
                }
                logger.debug("... reloaded classes {} (autoHotswap)", Arrays.toString(classNames));
            } catch (Exception e) {
                logger.debug("... Fail to reload classes {} (autoHotswap), msg is {}", Arrays.toString(classNames), e);
                throw new IllegalStateException("Unable to redefine classes", e);
            }
            reloadMap.clear();
        }
    }

    /**
     * Redefine the supplied set of classes using the supplied bytecode in scheduled command. Actual hotswap is postponed by timeout
     *
     * This method operates on a set in order to allow interdependent changes to more than one class at the same time
     * (a redefinition of class A can require a redefinition of class B).
     *
     * @param reloadMap class -> new bytecode
     * @see Instrumentation#redefineClasses(ClassDefinition...)
     */
    public void scheduleHotswap(Map<Class<?>, byte[]> reloadMap, int timeout) {
        if (instrumentation == null) {
            throw new IllegalStateException("Plugin manager is not correctly initialized - no instrumentation available.");
        }
        getScheduler().scheduleCommand(new ScheduledHotswapCommand(reloadMap), timeout);
    }

}
