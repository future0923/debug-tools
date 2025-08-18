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
package io.github.future0923.debug.tools.idea.action;

import com.intellij.execution.Executor;
import com.intellij.execution.dashboard.actions.ExecutorAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import io.github.future0923.debug.tools.idea.bundle.DebugToolsBundle;
import io.github.future0923.debug.tools.idea.runner.HotswapDebugExecutor;
import io.github.future0923.debug.tools.idea.utils.DebugToolsIcons;
import org.jetbrains.annotations.NotNull;

/**
 * Hotswap with DebugTools
 */
public class HotswapDebugAction extends ExecutorAction {


    @Override
    protected Executor getExecutor() {
        return HotswapDebugExecutor.getRunExecutorInstance();
    }

    @Override
    protected void update(@NotNull AnActionEvent e, boolean running) {
        Presentation presentation = e.getPresentation();
        if (running) {
            presentation.setText(DebugToolsBundle.message("action.hotswap.debug.rerun.text"));
            presentation.setDescription(DebugToolsBundle.message("action.hotswap.debug.rerun.description"));
            presentation.setIcon(DebugToolsIcons.Hotswap.Off);
        }
        else {
            presentation.setText(DebugToolsBundle.message("action.hotswap.debug.text"));
            presentation.setDescription(DebugToolsBundle.message("action.hotswap.debug.description"));
            presentation.setIcon(DebugToolsIcons.Hotswap.Off);
        }
    }
}
