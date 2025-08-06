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
package io.github.future0923.debug.tools.idea.runner;

import com.intellij.debugger.impl.GenericDebuggerRunner;
import com.intellij.execution.configurations.*;
import org.jetbrains.annotations.NotNull;

public class HotswapDebugRunner extends GenericDebuggerRunner {
    public static final String RUNNER_ID = "DEBUG_TOOLS_DEBUG_RUNNER";

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        return executorId.equals(HotswapDebugExecutor.EXECUTOR_ID) && profile instanceof ModuleRunProfile
                && !(profile instanceof RunConfigurationWithSuppressedDefaultDebugAction);
    }


    @Override
    public @NotNull String getRunnerId() {
        return RUNNER_ID;
    }
}
