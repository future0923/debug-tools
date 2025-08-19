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
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import io.github.future0923.debug.tools.common.protocal.http.AllClassLoaderRes;
import io.github.future0923.debug.tools.common.protocal.packet.request.RemoteCompilerHotDeployRequestPacket;
import io.github.future0923.debug.tools.idea.client.socket.utils.SocketSendUtils;
import io.github.future0923.debug.tools.idea.tool.DebugToolsToolWindowFactory;
import io.github.future0923.debug.tools.idea.utils.DebugToolsIcons;
import io.github.future0923.debug.tools.idea.bundle.DebugToolsBundle;
import io.github.future0923.debug.tools.idea.utils.DebugToolsIdeaClassUtil;
import io.github.future0923.debug.tools.idea.utils.StateUtils;
import org.jetbrains.annotations.NotNull;

/**
 * @author future0923
 */
public class RemoteCompilerAction extends AnAction {

    public RemoteCompilerAction() {
        getTemplatePresentation().setIcon(DebugToolsIcons.Hotswap.RemoteCompiler);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        if (project == null) {
            return;
        }
        AllClassLoaderRes.Item projectDefaultClassLoader = StateUtils.getProjectDefaultClassLoader(project);
        if (projectDefaultClassLoader == null) {
            Messages.showErrorDialog(DebugToolsBundle.message("error.select.default.classloader"), DebugToolsBundle.message("dialog.title.execution.failed"));
            DebugToolsToolWindowFactory.showWindow(project, null);
            return;
        }
        // 获取当前编辑的文件
        Editor editor = event.getData(CommonDataKeys.EDITOR);
        if (editor == null) {
            return;
        }
        // 保存当前文件
        FileDocumentManager.getInstance().saveDocument(editor.getDocument());
        VirtualFile virtualFile = editor.getVirtualFile();
        String content = editor.getDocument().getText();
        // 获取源文件路径和内容
        String packageName = DebugToolsIdeaClassUtil.getPackageName(content);
        if (packageName == null) {
            return;
        }
        String packetAllName = packageName + "." + virtualFile.getName().replace(".java", "");
        RemoteCompilerHotDeployRequestPacket packet = new RemoteCompilerHotDeployRequestPacket();
        packet.add(packetAllName, content);
        packet.setIdentity(projectDefaultClassLoader.getIdentity());
        SocketSendUtils.send(project, packet);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Presentation presentation = e.getPresentation();
        if (project == null) {
            presentation.setEnabledAndVisible(false);
            return;
        }
        // 当前文件
        PsiFile file = e.getData(CommonDataKeys.PSI_FILE);
        if (file == null) {
            presentation.setEnabledAndVisible(false);
            return;
        }
        if ("JAVA".equalsIgnoreCase(file.getFileType().getName())) {
            presentation.setText(DebugToolsBundle.message("action.remote.compiler.text") + " '" + file.getName() + "'");
            presentation.setEnabledAndVisible(true);
        } else {
            presentation.setEnabledAndVisible(false);
        }
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }
}
