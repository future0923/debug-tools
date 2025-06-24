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
package io.github.future0923.debug.tools.hotswap.core.plugin.watchResources;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.annotation.Init;
import io.github.future0923.debug.tools.hotswap.core.annotation.Plugin;
import io.github.future0923.debug.tools.hotswap.core.config.PluginConfiguration;
import io.github.future0923.debug.tools.hotswap.core.config.PluginManager;
import io.github.future0923.debug.tools.hotswap.core.util.classloader.HotswapAgentClassLoaderExt;
import io.github.future0923.debug.tools.hotswap.core.util.classloader.URLClassLoaderPathHelper;
import io.github.future0923.debug.tools.hotswap.core.util.classloader.WatchResourcesClassLoader;
import io.github.future0923.debug.tools.hotswap.core.watch.Watcher;

import java.net.URL;

/**
 * 支持watchResources配置加载外部资源。
 * 1. 通过{@link WatchResourcesClassLoader}加载指定路径上被修改的外部资源。
 * 2. 修改应用程序类加载器（使用继承UrlClassLoader的），现在 WatchResourcesClassLoader 中加载，加载不到再从应用类加载器找。通过{@link URLClassLoaderPathHelper}实现。
 */
@Plugin(name = "WatchResources", description = "Support for watchResources configuration property.",
        testedVersions = {"JDK 1.7.0_45"}, expectedVersions = {"JDK 1.6+"})
public class WatchResourcesPlugin {

    private static final Logger logger = Logger.getLogger(WatchResourcesPlugin.class);

    @Init
    Watcher watcher;

    @Init
    ClassLoader appClassLoader;

    /**
     * WatchResourcesClassLoader加载 watchResources 路径修改的资源
     */
    WatchResourcesClassLoader watchResourcesClassLoader = new WatchResourcesClassLoader(false);

    /**
     * For each classloader check for watchResources configuration instance with hotswapper.
     */
    //@Init
    public static void init(PluginManager pluginManager, PluginConfiguration pluginConfiguration, ClassLoader appClassLoader) {
        logger.debug("Init plugin at classLoader {}", appClassLoader);

        // 跳过合成类加载器
        if (appClassLoader instanceof WatchResourcesClassLoader.UrlOnlyClassLoader)
            return;

        if (!pluginConfiguration.containsPropertyFile()) {
            logger.debug("ClassLoader {} does not contain debug-tools-agent.properties file, WatchResources skipped.", appClassLoader);
            return;
        }

        // and watch resources are set
        URL[] watchResources = pluginConfiguration.getWatchResources();
        if (watchResources.length == 0) {
            logger.debug("ClassLoader {} has debug-tools-agent.properties watchResources empty.", appClassLoader);
            return;
        }

        if (!URLClassLoaderPathHelper.isApplicable(appClassLoader) &&
                !(appClassLoader instanceof HotswapAgentClassLoaderExt)) {
            logger.warning("Unable to modify application classloader. Classloader '{}' is of type '{}'," +
                            "unknown classloader type.\n" +
                            "*** watchResources configuration property will not be handled on JVM level ***",
                    appClassLoader, appClassLoader.getClass());
            return;
        }

        WatchResourcesPlugin plugin = (WatchResourcesPlugin) pluginManager.getPluginRegistry().initializePlugin(WatchResourcesPlugin.class.getName(), appClassLoader);
        plugin.init(watchResources);
    }

    private void init(URL[] watchResources) {
        watchResourcesClassLoader.initWatchResources(watchResources, watcher);
        if (appClassLoader instanceof HotswapAgentClassLoaderExt) {
            ((HotswapAgentClassLoaderExt) appClassLoader).$$ha$setWatchResourceLoader(watchResourcesClassLoader);
        } else if (URLClassLoaderPathHelper.isApplicable(appClassLoader)) {
            URLClassLoaderPathHelper.setWatchResourceLoader(appClassLoader, watchResourcesClassLoader);
        }
    }
}
