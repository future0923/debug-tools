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
package io.github.future0923.debug.tools.hotswap.core.config;

import io.github.future0923.debug.tools.hotswap.core.command.Command;
import io.github.future0923.debug.tools.hotswap.core.command.MergeableCommand;

import java.util.HashMap;
import java.util.Map;

/**
 * 通过调度器延迟执行热重载
 */
public class ScheduledHotswapCommand extends MergeableCommand {

    /**
     * 要热重载的类
     */
    private final Map<Class<?>, byte[]> reloadMap;

    public ScheduledHotswapCommand(Map<Class<?>, byte[]> reloadMap) {
        this.reloadMap = new HashMap<>();
        for (Class<?> key: reloadMap.keySet()) {
            this.reloadMap.put(key, reloadMap.get(key));
        }
    }

    public Command merge(Command other) {
        if (other instanceof ScheduledHotswapCommand) {
            ScheduledHotswapCommand scheduledHotswapCommand = (ScheduledHotswapCommand) other;
            for (Class<?> key: scheduledHotswapCommand.reloadMap.keySet()) {
                this.reloadMap.put(key, scheduledHotswapCommand.reloadMap.get(key));
            }
        }
        return this;
    }

    @Override
    public void executeCommand() {
        PluginManager.getInstance().hotswap(reloadMap);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o || getClass() == o.getClass()) return true;
        return false;
    }

    @Override
    public int hashCode() {
        return 31;
    }

}
