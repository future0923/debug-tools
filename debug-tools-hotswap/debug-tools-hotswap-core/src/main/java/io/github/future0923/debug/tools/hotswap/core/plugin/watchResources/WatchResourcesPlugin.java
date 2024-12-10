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
package io.github.future0923.debug.tools.hotswap.core.plugin.watchResources;

import io.github.future0923.debug.tools.hotswap.core.annotation.Init;
import io.github.future0923.debug.tools.hotswap.core.annotation.Plugin;
import io.github.future0923.debug.tools.hotswap.core.config.PluginConfiguration;
import io.github.future0923.debug.tools.hotswap.core.config.PluginManager;
import io.github.future0923.debug.tools.hotswap.core.util.classloader.HotswapAgentClassLoaderExt;
import io.github.future0923.debug.tools.hotswap.core.util.classloader.URLClassPathHelper;
import io.github.future0923.debug.tools.hotswap.core.util.classloader.WatchResourcesClassLoader;
import io.github.future0923.debug.tools.hotswap.core.watch.Watcher;
import io.github.future0923.debug.tools.base.logging.Logger;

import java.net.URL;

/**
 * Support for watchResources configuration property.
 *
 * This plugin creates special WatchResourcesClassLoader witch returns only modified resources on watchResources
 * path. It then modifies application classloader to look for resources first in WatchResourcesClassLoader and
 * only if the resource is not found, standard execution proceeds.
 *
 * Works for any java.net.URLClassLoader which delegates to URLClassPath property to findResource() (typical
 * scenario).
 */
@Plugin(name = "WatchResources", description = "Support for watchResources configuration property.",
        testedVersions = {"JDK 1.7.0_45"}, expectedVersions = {"JDK 1.6+"})
public class WatchResourcesPlugin {
    private static final Logger LOGGER = Logger.getLogger(WatchResourcesPlugin.class);

    @Init
    Watcher watcher;

    @Init
    ClassLoader appClassLoader;


    // Classloader to return only modified resources on watchResources path.
    WatchResourcesClassLoader watchResourcesClassLoader = new WatchResourcesClassLoader(false);

    /**
     * For each classloader check for watchResources configuration instance with hotswapper.
     */
    @Init
    public static void init(PluginManager pluginManager, PluginConfiguration pluginConfiguration, ClassLoader appClassLoader) {
        LOGGER.debug("Init plugin at classLoader {}", appClassLoader);

        // synthetic classloader, skip
        if (appClassLoader instanceof WatchResourcesClassLoader.UrlOnlyClassLoader)
            return;

        // init only if the classloader contains directly the property file (not in parent classloader)
        if (!pluginConfiguration.containsPropertyFile()) {
            LOGGER.debug("ClassLoader {} does not contain hotswap-agent.properties file, WatchResources skipped.", appClassLoader);
            return;
        }

        // and watch resources are set
        URL[] watchResources = pluginConfiguration.getWatchResources();
        if (watchResources.length == 0) {
            LOGGER.debug("ClassLoader {} has hotswap-agent.properties watchResources empty.", appClassLoader);
            return;
        }

        if (!URLClassPathHelper.isApplicable(appClassLoader) &&
                !(appClassLoader instanceof HotswapAgentClassLoaderExt)) {
            LOGGER.warning("Unable to modify application classloader. Classloader '{}' is of type '{}'," +
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
        } else if (URLClassPathHelper.isApplicable(appClassLoader)) {
            URLClassPathHelper.setWatchResourceLoader(appClassLoader, watchResourcesClassLoader);
        }
    }
}
