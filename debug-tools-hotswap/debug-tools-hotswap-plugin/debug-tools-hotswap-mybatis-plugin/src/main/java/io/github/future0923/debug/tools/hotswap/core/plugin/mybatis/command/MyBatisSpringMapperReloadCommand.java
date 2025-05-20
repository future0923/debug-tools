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
package io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.command;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.dto.MyBatisSpringMapperReloadDTO;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.patch.MyBatisSpringPatcher;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.reload.MyBatisSpringMapperReload;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.watcher.MyBatisSpringMapperWatchEventListener;
import io.github.future0923.debug.tools.hotswap.core.watch.WatchFileEvent;

/**
 * 重载 MyBatis Spring mapper 类命令
 *
 * @author future0923
 */
public class MyBatisSpringMapperReloadCommand {

    private static final Logger logger = Logger.getLogger(MyBatisSpringMapperReloadCommand.class);

    /**
     * 当class重新定义时，通过{@link MyBatisSpringPatcher#redefineMyBatisSpringMapper}创建命令后调用这
     * 当class新增时，通过{@link MyBatisSpringMapperWatchEventListener#onEvent(WatchFileEvent)}创建命令后调用这
     */
    public static void reloadConfiguration(String className, byte[] bytes, String path) {
        try {
            MyBatisSpringMapperReload.INSTANCE.reload(new MyBatisSpringMapperReloadDTO(className, bytes, path));
        } catch (Exception e) {
            logger.error("reloadConfiguration error", e);
        }
    }
}
