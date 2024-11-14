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
package io.github.future0923.debug.tools.hotswap.core.command;

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
public abstract class MergeableCommand implements Command {

    List<Command> mergedCommands = new ArrayList<>();

    /**
     * 合并命令
     *
     * @param other 其他要合并的命令
     * @return 合并之后的命令
     */
    public Command merge(Command other) {
        mergedCommands.add(other);
        return this;
    }

    /**
     * 返回合并命令并清除列表
     *
     * @return 合并后的命令
     */
    public List<Command> popMergedCommands() {
        List<Command> result = new ArrayList<>(mergedCommands);
        mergedCommands.clear();
        return result;
    }
}
