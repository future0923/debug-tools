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
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.util.concurrency.AppExecutorUtil;
import io.github.future0923.debug.tools.idea.client.ApplicationProjectHolder;
import io.github.future0923.debug.tools.idea.context.ClassDataContext;
import io.github.future0923.debug.tools.idea.context.DataContext;
import io.github.future0923.debug.tools.idea.context.MethodDataContext;
import io.github.future0923.debug.tools.idea.setting.DebugToolsSettingState;
import io.github.future0923.debug.tools.idea.tool.DebugToolsToolWindowFactory;
import io.github.future0923.debug.tools.idea.ui.main.MainDialog;
import io.github.future0923.debug.tools.idea.utils.DebugToolsActionUtil;
import io.github.future0923.debug.tools.idea.utils.DebugToolsIcons;
import io.github.future0923.debug.tools.idea.bundle.DebugToolsBundle;
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
        getTemplatePresentation().setText(DebugToolsBundle.message("action.mouse.quick.debug.text"));
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
        if (DebugToolsActionUtil.checkAttachSocketError(project)) {
            return;
        }
        DebugToolsSettingState settingState = DebugToolsSettingState.getInstance(project);
        if (settingState == null) {
            DebugToolsNotifierUtil.notifyError(project, "state not exists");
            return;
        }
        ReadAction.nonBlocking(() -> {
                    PsiClass psiClass = (PsiClass) psiMethod.getParent();
                    ClassDataContext classDataContext = DataContext.instance(project).getClassDataContext(psiClass);
                    return new MethodDataContext(classDataContext, psiMethod, project);
                })
                .withDocumentsCommitted(project)
                .finishOnUiThread(ModalityState.any(), methodDataContext -> {
                    if (methodDataContext != null) {
                        MainDialog dialog = new MainDialog(methodDataContext, project);
                        dialog.show();
                    }
                })
                .submit(AppExecutorUtil.getAppExecutorService())
                .onError(throwable -> {
                    log.error("debug tools invoke exception", throwable);
                    DebugToolsNotifierUtil.notifyError(project, "invoke exception " + throwable.getMessage());
                });
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
