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
import io.github.future0923.debug.tools.hotswap.core.command.MergeableCommand;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.dto.MyBatisPlusEntityReloadDTO;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.reload.MyBatisPlusEntityReload;

/**
 * 重载 MyBatis Plus Entity 命令 AbstractSqlInjector.inspectInject
 *
 * @author future0923
 */
public class MyBatisPlusEntityReloadCommand extends MergeableCommand {

    private static final Logger logger = Logger.getLogger(MyBatisPlusEntityReloadCommand.class);

    private final ClassLoader classLoader;

    private final Class<?> clazz;

    public MyBatisPlusEntityReloadCommand(ClassLoader classLoader, Class<?> clazz) {
        this.classLoader = classLoader;
        this.clazz = clazz;
    }

    @Override
    public void executeCommand() {
        try {
            MyBatisPlusEntityReload.INSTANCE.reload(new MyBatisPlusEntityReloadDTO(classLoader, clazz));
        } catch (Exception e) {
            logger.error("refresh mybatis error", e);
        }
    }
}
