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
package io.github.future0923.debug.tools.idea.tool.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import io.github.future0923.debug.tools.idea.tool.DebugToolsToolWindow;
import io.github.future0923.debug.tools.idea.tool.DebugToolsToolWindowFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author future0923
 */
public abstract class BaseToolAction extends DumbAwareAction {

    private DebugToolsToolWindow toolWindow;

    public BaseToolAction() {
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }
        if (getToolWindow(project) == null) {
            return;
        }
        doActionPerformed(project, toolWindow);
    }

    protected abstract void doActionPerformed(Project project, DebugToolsToolWindow toolWindow);

    private DebugToolsToolWindow getToolWindow(@Nullable Project project) {
        if (toolWindow != null) {
            return toolWindow;
        }
        return (toolWindow = DebugToolsToolWindowFactory.getToolWindow(project));
    }
}
