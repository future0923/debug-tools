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
