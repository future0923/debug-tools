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
package io.github.future0923.debug.tools.hotswap.core.command;

import io.github.future0923.debug.tools.hotswap.core.annotation.FileEvent;
import io.github.future0923.debug.tools.hotswap.core.watch.WatchFileEvent;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 可以合并为单个执行的命令
 * <p>
 * 当{@link Scheduler#scheduleCommand}时调用{@code Command#equals(Object)}方法如果相等则调用{@link #merge(Command)}合并
 * <p>
 * 如多个文件修改，如删除+创建合并为修改等
 */
@Getter
@SuppressWarnings("unchecked")
public abstract class EventMergeableCommand<T extends EventMergeableCommand<T>> extends MergeableCommand {

    protected abstract WatchFileEvent event();

    /**
     * 检查所有合并的事件，查看是否有删除和创建事件。如果发现只有删除而没有创建，那么就假设文件被删除了。
     */
    public boolean isDeleteEvent() {
        List<T> mergedCommands = new ArrayList<>();
        for (Command command : getMergedCommands()) {
            mergedCommands.add((T) command);
        }
        mergedCommands.add((T) this);
        boolean createFound = false;
        boolean deleteFound = false;
        for (T command : mergedCommands) {
            if (command.event() != null) {
                if (command.event().getEventType().equals(FileEvent.DELETE)) {
                    deleteFound = true;
                }
                if (command.event().getEventType().equals(FileEvent.CREATE)) {
                    createFound = true;
                }
            }
        }
        return !createFound && deleteFound;
    }
}
