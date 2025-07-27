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

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.psi.PsiMethod;
import io.github.future0923.debug.tools.base.utils.DebugToolsClassUtils;
import io.github.future0923.debug.tools.common.protocal.packet.request.ChangeTraceMethodRequestPacket;
import io.github.future0923.debug.tools.idea.client.socket.utils.SocketSendUtils;
import io.github.future0923.debug.tools.idea.utils.DebugToolsIdeaClassUtil;
import io.github.future0923.debug.tools.idea.utils.StateUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author future0923
 */
public abstract class AbstractTraceMethodAction extends AnAction {

    protected abstract boolean isAddTraceMethod();

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        if (e.getProject() == null) {
            return;
        }
        PsiMethod psiMethod = DebugToolsIdeaClassUtil.getCaretPsiMethod(e);
        if (psiMethod == null) {
            return;
        }
        if (psiMethod.getContainingClass() == null) {
            return;
        }
        ChangeTraceMethodRequestPacket packet = new ChangeTraceMethodRequestPacket();
        packet.setTrace(isAddTraceMethod());
        packet.setClassName(psiMethod.getContainingClass().getQualifiedName());
        packet.setMethodName(psiMethod.getName());
        packet.setMethodDescription(DebugToolsIdeaClassUtil.getMethodDescriptor(psiMethod));
        SocketSendUtils.send(e.getProject(), packet, () -> {
            if (isAddTraceMethod()) {
                StateUtils.setTraceMethod(e.getProject(), DebugToolsClassUtils.getQualifierMethod(packet.getClassName(), packet.getMethodName(), packet.getMethodDescription()));
            } else {
                StateUtils.removeTraceMethod(e.getProject(), DebugToolsClassUtils.getQualifierMethod(packet.getClassName(), packet.getMethodName(), packet.getMethodDescription()));
            }
            DaemonCodeAnalyzer.getInstance(e.getProject()).restart(psiMethod.getContainingFile());
        });
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabledAndVisible(Objects.nonNull(DebugToolsIdeaClassUtil.getCaretPsiMethod(e)));
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }
}
