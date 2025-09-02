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
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import io.github.future0923.debug.tools.common.protocal.http.AllClassLoaderRes;
import io.github.future0923.debug.tools.common.protocal.packet.request.ResourceHotDeployRequestPacket;
import io.github.future0923.debug.tools.idea.bundle.DebugToolsBundle;
import io.github.future0923.debug.tools.idea.client.socket.utils.SocketSendUtils;
import io.github.future0923.debug.tools.idea.tool.DebugToolsToolWindowFactory;
import io.github.future0923.debug.tools.idea.utils.DebugToolsIcons;
import io.github.future0923.debug.tools.idea.utils.StateUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * @author future0923
 */
public class DeployXmlAction extends AnAction {

    public DeployXmlAction() {
        getTemplatePresentation().setIcon(DebugToolsIcons.Hotswap.Publish);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
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
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (editor == null) {
            return;
        }
        // 保存当前文件
        FileDocumentManager.getInstance().saveDocument(editor.getDocument());
        PsiFile file = e.getData(CommonDataKeys.PSI_FILE);
        if (file == null) {
            return;
        }
        VirtualFile virtualFile = file.getVirtualFile();
        ResourceHotDeployRequestPacket hotSwapRequestPacket = new ResourceHotDeployRequestPacket();
        VirtualFile sourceRootForFile = ProjectFileIndex.getInstance(project).getSourceRootForFile(virtualFile);
        if (sourceRootForFile != null) {
            String filePath = VfsUtilCore.getRelativePath(virtualFile, sourceRootForFile, '/');
            try {
                byte[] bytes = virtualFile.contentsToByteArray();
                hotSwapRequestPacket.add(filePath, bytes);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            hotSwapRequestPacket.setIdentity(projectDefaultClassLoader.getIdentity());
            SocketSendUtils.send(project, hotSwapRequestPacket);
        }
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
        Module moduleForFile = ModuleUtilCore.findModuleForFile(file);
        if (moduleForFile == null) {
            presentation.setEnabledAndVisible(false);
            return;
        }
        if ("XML".equalsIgnoreCase(file.getFileType().getName())) {
            presentation.setText(DebugToolsBundle.message("action.deploy.xml.file.text") + " '" + file.getName() + "' " + DebugToolsBundle.message("action.deploy.xml.file.description"));
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
