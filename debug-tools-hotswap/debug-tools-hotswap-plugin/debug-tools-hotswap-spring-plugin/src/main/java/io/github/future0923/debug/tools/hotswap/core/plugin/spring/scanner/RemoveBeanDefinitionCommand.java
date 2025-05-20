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
