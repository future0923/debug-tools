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
