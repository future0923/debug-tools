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
import io.github.future0923.debug.tools.hotswap.core.annotation.FileEvent;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.MyBatisPlugin;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.reload.MyBatisSpringXmlReload;

import java.net.URL;

/**
 * 重载 MyBatis Spring xml 文件命令
 *
 * @author future0923
 */
public class MyBatisSpringXmlReloadCommand {

    private static final Logger logger = Logger.getLogger(MyBatisSpringXmlReloadCommand.class);

    /**
     * 当xml文件变化时，通过{@link MyBatisPlugin#watchResource(URL, FileEvent)}创建MyBatisXmlResourceRefreshCommands后调用这里
     */
    public static void reloadConfiguration(URL url) {
        try {
            MyBatisSpringXmlReload.INSTANCE.reload(url);
        } catch (Exception e) {
            logger.error("reload MyBatis spring xml error", e);
        }

    }
}
