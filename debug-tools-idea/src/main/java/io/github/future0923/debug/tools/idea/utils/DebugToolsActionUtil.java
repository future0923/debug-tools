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
package io.github.future0923.debug.tools.idea.utils;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiParameterList;
import io.github.future0923.debug.tools.common.dto.RunDTO;
import io.github.future0923.debug.tools.common.protocal.http.AllClassLoaderRes;
import io.github.future0923.debug.tools.common.protocal.packet.request.RunTargetMethodRequestPacket;
import io.github.future0923.debug.tools.common.utils.DebugToolsJsonUtils;
import io.github.future0923.debug.tools.idea.bundle.DebugToolsBundle;
import io.github.future0923.debug.tools.idea.client.ApplicationProjectHolder;
import io.github.future0923.debug.tools.idea.client.socket.utils.SocketSendUtils;
import io.github.future0923.debug.tools.idea.tool.DebugToolsToolWindowFactory;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DebugToolsActionUtil {

    public static String genCacheKey(PsiClass psiClass, PsiMethod psiMethod) {
        return genCacheKey(psiClass.getQualifiedName(), psiMethod.getName(), DebugToolsActionUtil.toParamTypeNameList(psiMethod.getParameterList()));
    }

    public static String genCacheKey(String className, String methodName, List<String> paramTypeNameList) {
        return className + "#" + methodName + "#" + String.join(",", paramTypeNameList);
    }

    public static List<String> toParamTypeNameList(PsiParameterList parameterList) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < parameterList.getParametersCount(); i++) {
            PsiParameter parameter = Objects.requireNonNull(parameterList.getParameter(i));
            String canonicalText = parameter.getType().getCanonicalText();
            list.add(StringUtils.substringBefore(canonicalText, "<"));
        }
        return list;
    }

    public static boolean checkAttachSocketError(Project project) {
        ApplicationProjectHolder.Info info = ApplicationProjectHolder.getInfo(project);
        if (info == null) {
            Messages.showErrorDialog(DebugToolsBundle.message("error.run.attach.first"), DebugToolsBundle.message("dialog.title.execution.failed"));
            DebugToolsToolWindowFactory.showWindow(project, null);
            return true;
        }
        if (info.getClient().isClosed()) {
            Messages.showErrorDialog(DebugToolsBundle.message("error.attach.socket.status"), DebugToolsBundle.message("dialog.title.execution.failed"));
            DebugToolsToolWindowFactory.showWindow(project, null);
            return true;
        }
        return false;
    }

    public static void executeLast(Project project, String json) {
        executeLast(project, DebugToolsJsonUtils.toBean(json, RunDTO.class));
    }

    public static void executeLast(Project project, RunDTO runDTO) {
        RunTargetMethodRequestPacket packet = new RunTargetMethodRequestPacket(runDTO);
        SocketSendUtils.send(project, packet);
    }

    public static void executeLastWithDefaultClassLoader(Project project, String json) {
        executeLastWithDefaultClassLoader(project, DebugToolsJsonUtils.toBean(json, RunDTO.class));
    }

    public static void executeLastWithDefaultClassLoader(Project project, RunDTO runDTO) {
        AllClassLoaderRes.Item projectDefaultClassLoader = StateUtils.getProjectDefaultClassLoader(project);
        if (projectDefaultClassLoader == null) {
            Messages.showErrorDialog(DebugToolsBundle.message("error.select.default.classloader"), DebugToolsBundle.message("dialog.title.execution.failed"));
            DebugToolsToolWindowFactory.showWindow(project, null);
            return;
        }
        runDTO.setClassLoader(projectDefaultClassLoader);
        executeLast(project, runDTO);
    }
}
