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
package io.github.future0923.debug.tools.hotswap.core.command.impl;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.command.Command;

/**
 * 命令的执行器，创建线程执行
 */
public class CommandExecutor extends Thread {
    private static final Logger LOGGER = Logger.getLogger(CommandExecutor.class);

    final Command command;

    public CommandExecutor(Command command) {
        this.command = command;
        setDaemon(true);
    }

    @Override
    public void run() {
        try {
            LOGGER.trace("Executing command {}", command);
            command.executeCommand();
        } finally {
            finished();
        }
    }

    /**
     * 执行完成后的处理（一定会调用）
     */
    public void finished() {
    }

}
