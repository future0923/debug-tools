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

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.command.ReflectionCommand;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 热重载命令，依赖tools.jar
 */
public class HotSwapperCommand {
    private static final Logger LOGGER = Logger.getLogger(HotSwapperCommand.class);

    /**
     * 通过Jpda热重载类
     */
    private static HotSwapperJpda hotSwapper = null;

    /**
     * {@link HotSwapperPlugin#initHotswapCommand}创建{@link ReflectionCommand}会调用运行这里
     */
    public static synchronized void hotswap(String port, final HashMap<Class<?>, byte[]> reloadMap) {
        synchronized (reloadMap) {
            if (hotSwapper == null) {
                LOGGER.debug("Starting HotSwapperJpda agent on JPDA transport socket - port {}, classloader {}", port, HotSwapperCommand.class.getClassLoader());
                try {
                    hotSwapper = new HotSwapperJpda(port);
                } catch (IOException e) {
                    LOGGER.error("Unable to connect to debug session. Did you start the application with debug enabled " +
                            "(i.e. java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000)", e);
                } catch (Exception e) {
                    LOGGER.error("Unable to connect to debug session. Please check port property setting '{}'.", e, port);
                }
            }
            if (hotSwapper != null) {
                LOGGER.debug("Reloading classes {}", Arrays.toString(reloadMap.keySet().toArray()));
                Map<String, byte[]> reloadMapClassNames = new HashMap<>();
                for (Map.Entry<Class<?>, byte[]> entry : reloadMap.entrySet()) {
                    reloadMapClassNames.put(entry.getKey().getName(), entry.getValue());
                }
                hotSwapper.reload(reloadMapClassNames);
                reloadMap.clear();
                LOGGER.debug("HotSwapperJpda agent reload complete.");
            }
        }
    }
}
