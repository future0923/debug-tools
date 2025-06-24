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
