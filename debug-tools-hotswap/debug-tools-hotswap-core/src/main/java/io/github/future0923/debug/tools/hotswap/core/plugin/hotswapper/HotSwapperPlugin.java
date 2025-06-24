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
package io.github.future0923.debug.tools.hotswap.core.plugin.hotswapper;

import io.github.future0923.debug.tools.base.logging.Logger;
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

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * HotSwapperPlugin如过设置了自动热重载配置，那么插件为每个类加载器创建一个插件实例，监听到.class文件的变化后进行重载
 * <p>有port通过直接连接JVM调用JPDA的API进行重载
 * <p>没有port通过{@link PluginManager#hotswap}调用{@link Instrumentation#redefineClasses}进行重载
 *
 * @see HotSwapperJpda
 */
@Plugin(name = "HotSwapper", description = "如果配置了自动热重载会Watch到.class文件修改进行热重载",
        testedVersions = {"JDK 1.7.0_45"}, expectedVersions = {"JDK 1.6+"})
public class HotSwapperPlugin {
    private static final Logger LOGGER = Logger.getLogger(HotSwapperPlugin.class);

    @Init
    Scheduler scheduler;

    @Init
    PluginManager pluginManager;

    /**
     * 要重载的Class
     */
    private final Map<Class<?>, byte[]> reloadMap = new HashMap<>();

    /**
     * 创建热重载命令
     */
    private Command hotswapCommand;

    /**
     * 如果自动热重载，那么为每个类加载器创建一个HotSwapperPlugin实例
     */
    @Init
    public static void init(PluginConfiguration pluginConfiguration, ClassLoader appClassLoader) {
        if (appClassLoader == null) {
            LOGGER.debug("Bootstrap class loader is null, hot swapper skipped.");
            return;
        }
        LOGGER.debug("Init plugin at classLoader {}", appClassLoader);
        if (!HotswapAgent.isAutoHotswap() && !pluginConfiguration.containsPropertyFile()) {
            LOGGER.debug("ClassLoader {} does not contain debug-tools-agent.properties file, hot swapper skipped.", appClassLoader);
            return;
        }
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

    /**
     * 对变动的class创建一个重新加载命令。
     * <p>{@link #init}初始化了插件之后，OnClassFileEvent才会生效监听到class文件变化
     */
    @OnClassFileEvent(classNameRegexp = ".*", events = {FileEvent.MODIFY, FileEvent.CREATE})
    public void watchReload(CtClass ctClass, ClassLoader appClassLoader, URL url) throws IOException, CannotCompileException {
        if (!ClassLoaderHelper.isClassLoaded(appClassLoader, ctClass.getName())) {
            LOGGER.trace("Class {} not loaded yet, no need for autoHotswap, skipped URL {}", ctClass.getName(), url);
            return;
        }
        LOGGER.debug("Class {} will be reloaded from URL {}", ctClass.getName(), url);
        Class<?> clazz;
        try {
            clazz = appClassLoader.loadClass(ctClass.getName());
        } catch (ClassNotFoundException e) {
            LOGGER.warning("HotSwapper tries to reload class {}, which is not known to application classLoader {}.", ctClass.getName(), appClassLoader);
            return;
        }
        synchronized (reloadMap) {
            reloadMap.put(clazz, ctClass.toBytecode());
        }
        scheduler.scheduleCommand(hotswapCommand, 100, Scheduler.DuplicateSheduleBehaviour.SKIP);
    }

    /**
     * 创建热重载命令
     *
     * @param appClassLoader classPath上有tools.jar的类加载器都可以运行
     * @param port           连接jvm的端口
     */
    public void initHotswapCommand(ClassLoader appClassLoader, String port) {
        if (port != null && !port.isEmpty()) {
            hotswapCommand = new ReflectionCommand(appClassLoader, this, HotSwapperCommand.class.getName(), "hotswap", Arrays.asList(String.class, HashMap.class), port, reloadMap);
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
}
