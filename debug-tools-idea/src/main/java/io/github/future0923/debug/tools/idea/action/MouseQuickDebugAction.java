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
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import io.github.future0923.debug.tools.idea.client.ApplicationProjectHolder;
import io.github.future0923.debug.tools.idea.context.ClassDataContext;
import io.github.future0923.debug.tools.idea.context.DataContext;
import io.github.future0923.debug.tools.idea.context.MethodDataContext;
import io.github.future0923.debug.tools.idea.setting.DebugToolsSettingState;
import io.github.future0923.debug.tools.idea.tool.DebugToolsToolWindowFactory;
import io.github.future0923.debug.tools.idea.ui.main.MainDialog;
import io.github.future0923.debug.tools.idea.utils.DebugToolsIcons;
import io.github.future0923.debug.tools.idea.utils.DebugToolsNotifierUtil;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

/**
 * 鼠标快速调试
 *
 * @author future0923
 */
@Setter
public class MouseQuickDebugAction extends AnAction {

    private static final Logger log = Logger.getInstance(MouseQuickDebugAction.class);

    private PsiMethod psiMethod;

    public MouseQuickDebugAction() {
        getTemplatePresentation().setText("Quick Debug");
        getTemplatePresentation().setIcon(DebugToolsIcons.Request_Full);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        if (psiMethod == null) {
            return;
        }
        final Project project = e.getProject();
        final Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (null == project || editor == null) {
            throw new IllegalArgumentException("idea arg error (project or editor is null)");
        }

        ApplicationProjectHolder.Info info = ApplicationProjectHolder.getInfo(project);
        if (info == null) {
            Messages.showErrorDialog("Run attach first", "执行失败");
            DebugToolsToolWindowFactory.showWindow(project, null);
            return;
        }
        if (info.getClient().isClosed()) {
            Messages.showErrorDialog("Attach socket status error", "执行失败");
            DebugToolsToolWindowFactory.showWindow(project, null);
            return;
        }
        try {
            DebugToolsSettingState settingState = DebugToolsSettingState.getInstance(project);
            if (settingState == null) {
                DebugToolsNotifierUtil.notifyError(project, "state not exists");
                return;
            }
            PsiClass psiClass = (PsiClass) psiMethod.getParent();
            ClassDataContext classDataContext = DataContext.instance(project).getClassDataContext(psiClass);
            MethodDataContext methodDataContext = new MethodDataContext(classDataContext, psiMethod, project);
            MainDialog dialog = new MainDialog(methodDataContext, project);
            dialog.show();
        } catch (Exception exception) {
            log.error("debug tools invoke exception", exception);
            DebugToolsNotifierUtil.notifyError(project, "invoke exception " + exception.getMessage());
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        if (e.getProject() == null || DumbService.isDumb(e.getProject())) {
            e.getPresentation().setEnabled(false);
        }
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

}
