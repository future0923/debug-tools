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
