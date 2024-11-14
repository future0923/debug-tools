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
package io.github.future0923.debug.tools.hotswap.core.plugin.hotswapper;

import io.github.future0923.debug.tools.hotswap.core.HotswapAgent;
import io.github.future0923.debug.tools.hotswap.core.annotation.FileEvent;
import io.github.future0923.debug.tools.hotswap.core.annotation.Init;
import io.github.future0923.debug.tools.hotswap.core.annotation.OnClassFileEvent;
import io.github.future0923.debug.tools.hotswap.core.annotation.Plugin;
import io.github.future0923.debug.tools.hotswap.core.command.Command;
import io.github.future0923.debug.tools.hotswap.core.command.ReflectionCommand;
import io.github.future0923.debug.tools.hotswap.core.command.Scheduler;
import io.github.future0923.debug.tools.hotswap.core.config.PluginConfiguration;
import io.github.future0923.debug.tools.hotswap.core.config.PluginManager;
import io.github.future0923.debug.tools.hotswap.core.javassist.CannotCompileException;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.util.PluginManagerInvoker;
import io.github.future0923.debug.tools.hotswap.core.util.classloader.ClassLoaderHelper;
import io.github.future0923.debug.tools.base.logging.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Hotswap class changes directly via JPDA API.
 * <p/>
 * This plugin creates an instance for each classloader with autoHotswap agent property set. Then it listens
 * for .class file change and executes hotswap via JPDA API.
 *
 * @author Jiri Bubnik
 * @see HotSwapperJpda
 */
@Plugin(name = "HotSwapper", description = "Watch for any class file change and reload (hotswap) it on the fly.",
        testedVersions = {"JDK 1.7.0_45"}, expectedVersions = {"JDK 1.6+"})
public class HotSwapperPlugin {
    private static final Logger LOGGER = Logger.getLogger(HotSwapperPlugin.class);

    @Init
    Scheduler scheduler;

    @Init
    PluginManager pluginManager;

    // synchronize on this map to wait for previous processing
    final Map<Class<?>, byte[]> reloadMap = new HashMap<>();

    // command to do actual hotswap. Single command to merge possible multiple reload actions.
    Command hotswapCommand;

    /**
     * For each changed class create a reload command.
     */
    @OnClassFileEvent(classNameRegexp = ".*", events = {FileEvent.MODIFY, FileEvent.CREATE})
    public void watchReload(CtClass ctClass, ClassLoader appClassLoader, URL url) throws IOException, CannotCompileException {
        if (!ClassLoaderHelper.isClassLoaded(appClassLoader, ctClass.getName())) {
            LOGGER.trace("Class {} not loaded yet, no need for autoHotswap, skipped URL {}", ctClass.getName(), url);
            return;
        }

        LOGGER.debug("Class {} will be reloaded from URL {}", ctClass.getName(), url);

        // search for a class to reload
        Class clazz;
        try {
            clazz  = appClassLoader.loadClass(ctClass.getName());
        } catch (ClassNotFoundException e) {
            LOGGER.warning("HotSwapper tries to reload class {}, which is not known to application classLoader {}.",
                    ctClass.getName(), appClassLoader);
            return;
        }

        synchronized (reloadMap) {
            reloadMap.put(clazz, ctClass.toBytecode());
        }
        scheduler.scheduleCommand(hotswapCommand, 100, Scheduler.DuplicateSheduleBehaviour.SKIP);
    }

    /**
     * Create a hotswap command using hotSwapper.
     *
     * @param appClassLoader it can be run in any classloader with tools.jar on classpath. AppClassLoader can
     *                       be setup by maven dependency (jetty plugin), use this classloader.
     * @param port           attach the hotSwapper
     */
    public void initHotswapCommand(ClassLoader appClassLoader, String port) {
        if (port != null && !port.isEmpty()) {
            hotswapCommand = new ReflectionCommand(this, HotSwapperCommand.class.getName(), "hotswap", appClassLoader,
                    port, reloadMap);
        } else {
            hotswapCommand = new Command() {
                @Override
                public void executeCommand() {
                    pluginManager.hotswap(reloadMap);
                }

                @Override
                public String toString() {
                    return "pluginManager.hotswap(" + Arrays.toString(reloadMap.keySet().toArray()) + ")";
                }
            };
        }
    }

    /**
     * For each classloader check for autoHotswap configuration instance with hot swapper.
     */
    @Init
    public static void init(PluginConfiguration pluginConfiguration, ClassLoader appClassLoader) {

        if (appClassLoader == null) {
            LOGGER.debug("Bootstrap class loader is null, hot swapper skipped.");
            return;
        }

        LOGGER.debug("Init plugin at classLoader {}", appClassLoader);

        // init only if the classloader contains directly the property file (not in parent classloader)
        if (!HotswapAgent.isAutoHotswap() && !pluginConfiguration.containsPropertyFile()) {
            LOGGER.debug("ClassLoader {} does not contain hotswap-agent.properties file, hot swapper skipped.", appClassLoader);
            return;
        }

        // and autoHotswap enabled
        if (!HotswapAgent.isAutoHotswap() && !pluginConfiguration.getPropertyBoolean("autoHotswap")) {
            LOGGER.debug("ClassLoader {} has autoHotswap disabled, hot swapper skipped.", appClassLoader);
            return;
        }


        String port = pluginConfiguration.getProperty("autoHotswap.port");

        HotSwapperPlugin plugin = PluginManagerInvoker.callInitializePlugin(HotSwapperPlugin.class, appClassLoader);
        if (plugin != null) {
            plugin.initHotswapCommand(appClassLoader, port);
        } else {
            LOGGER.debug("Hot swapper is disabled in {}", appClassLoader);
        }
    }
}
