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

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import io.github.future0923.debug.tools.common.protocal.http.AllClassLoaderRes;
import io.github.future0923.debug.tools.idea.tool.DebugToolsToolWindowFactory;
import io.github.future0923.debug.tools.idea.ui.hotswap.HotDeployDialog;
import io.github.future0923.debug.tools.idea.utils.DebugToolsIcons;
import io.github.future0923.debug.tools.idea.utils.StateUtils;
import org.jetbrains.annotations.NotNull;

/**
 * @author future0923
 */
public class HotDeploymentAction extends DumbAwareAction {

    public HotDeploymentAction() {
        getTemplatePresentation().setIcon(DebugToolsIcons.Hotswap.Deploy);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }
        AllClassLoaderRes.Item projectDefaultClassLoader = StateUtils.getProjectDefaultClassLoader(project);
        if (projectDefaultClassLoader == null) {
            Messages.showErrorDialog("Please select a DefaultClassLoader first.", "执行失败");
            DebugToolsToolWindowFactory.showWindow(project, null);
            return;
        }
        FileDocumentManager.getInstance().saveAllDocuments();
        HotDeployDialog hotDeployDialog = new HotDeployDialog(project, projectDefaultClassLoader);
        hotDeployDialog.show();
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }
}
