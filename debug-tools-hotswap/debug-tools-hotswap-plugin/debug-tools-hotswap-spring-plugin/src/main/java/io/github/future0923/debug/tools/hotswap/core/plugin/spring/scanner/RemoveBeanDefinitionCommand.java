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
package io.github.future0923.debug.tools.hotswap.core.plugin.spring.scanner;

import io.github.future0923.debug.tools.hotswap.core.annotation.FileEvent;
import io.github.future0923.debug.tools.hotswap.core.command.EventMergeableCommand;
import io.github.future0923.debug.tools.hotswap.core.watch.WatchFileEvent;

/**
 * @author future0923
 */
public class RemoveBeanDefinitionCommand extends EventMergeableCommand<RemoveBeanDefinitionCommand> {

    private final WatchFileEvent event;

    public RemoveBeanDefinitionCommand(WatchFileEvent event) {
        this.event = event;
    }

    @Override
    public void executeCommand() {
        if (isDeleteEvent()) {
            if (event.isFile() && event.getURI().toString().endsWith(".class")) {
                // 删除了class文件，卸载bean
                if (FileEvent.DELETE.equals(event.getEventType())) {
                    ClassPathBeanDefinitionScannerAgent.removeBeanDefinitionByFilePath(event.getURI().getPath());
                }
            }
        }
    }

    @Override
    protected WatchFileEvent event() {
        return event;
    }
}
