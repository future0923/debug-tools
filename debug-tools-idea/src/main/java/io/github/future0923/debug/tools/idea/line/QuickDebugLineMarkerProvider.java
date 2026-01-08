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
package io.github.future0923.debug.tools.idea.line;

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethod;
import io.github.future0923.debug.tools.base.hutool.core.collection.CollUtil;
import io.github.future0923.debug.tools.base.utils.DebugToolsClassUtils;
import io.github.future0923.debug.tools.common.protocal.packet.request.ChangeTraceMethodRequestPacket;
import io.github.future0923.debug.tools.idea.client.socket.utils.SocketSendUtils;
import io.github.future0923.debug.tools.idea.utils.DebugToolsIcons;
import io.github.future0923.debug.tools.idea.utils.DebugToolsIdeaClassUtil;
import io.github.future0923.debug.tools.idea.utils.StateUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * @author future0923
 */
public class QuickDebugLineMarkerProvider implements LineMarkerProvider {

    @Override
    public @Nullable LineMarkerInfo<?> getLineMarkerInfo(@NotNull PsiElement psiElement) {
        if (psiElement instanceof PsiIdentifier
                && psiElement.getParent() instanceof PsiMethod method) {
            if (method.getContainingClass() != null) {
                String qualifierMethod = DebugToolsClassUtils.getQualifierMethod(
                        method.getContainingClass().getQualifiedName(),
                        method.getName(),
                        DebugToolsIdeaClassUtil.getMethodDescriptor(method)
                );
                Set<String> traceMethodSet = StateUtils.getTraceMethod(psiElement.getProject());
                if (CollUtil.isNotEmpty(traceMethodSet) && traceMethodSet.contains(qualifierMethod)) {
                    return new LineMarkerInfo<>(
                            psiElement,
                            psiElement.getTextRange(),
                            DebugToolsIcons.Trace.Trace,
                            e -> "Remove method from trace",
                            (mouseEvent, elt) -> {
                                ChangeTraceMethodRequestPacket packet = new ChangeTraceMethodRequestPacket();
                                packet.setTrace(false);
                                packet.setClassName(method.getContainingClass().getQualifiedName());
                                packet.setMethodName(method.getName());
                                packet.setMethodDescription(DebugToolsIdeaClassUtil.getMethodDescriptor(method));
                                SocketSendUtils.send(psiElement.getProject(), packet, () -> {
                                    traceMethodSet.remove(qualifierMethod);
                                    DaemonCodeAnalyzer.getInstance(psiElement.getProject()).restart(psiElement.getContainingFile());
                                });
                            },
                            GutterIconRenderer.Alignment.RIGHT,
                            () -> "Remove method from trace"
                    );
                }
            }
        }
        return null;
    }
}
