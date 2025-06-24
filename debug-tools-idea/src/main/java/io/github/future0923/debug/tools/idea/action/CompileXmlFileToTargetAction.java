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
import com.intellij.openapi.compiler.CompilerPaths;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import io.github.future0923.debug.tools.idea.utils.DebugToolsIcons;
import io.github.future0923.debug.tools.idea.utils.DebugToolsNotifierUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

/**
 * @author future0923
 */
public class CompileXmlFileToTargetAction extends AnAction {

    private static final Logger logger = Logger.getInstance(CompileXmlFileToTargetAction.class);

    public CompileXmlFileToTargetAction() {
        getTemplatePresentation().setIcon(DebugToolsIcons.Hotswap.Compile);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }
        // 获取当前编辑的文件
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (editor == null) {
            return;
        }
        // 保存当前文件
        FileDocumentManager.getInstance().saveDocument(editor.getDocument());
        String fileContent = editor.getDocument().getText();
        PsiFile file = e.getData(CommonDataKeys.PSI_FILE);
        if (file == null) {
            return;
        }
        Module moduleForFile = ModuleUtilCore.findModuleForFile(file);
        if (moduleForFile == null) {
            return;
        }
        VirtualFile virtualFile = file.getVirtualFile();
        String path = virtualFile.getPath();
        VirtualFile sourceRootForFile = ProjectRootManager.getInstance(project).getFileIndex().getSourceRootForFile(virtualFile);
        if (sourceRootForFile == null) {
            return;
        }
        // mapper/UserMapper.xml
        String pathFromSourceRoot = path.substring(sourceRootForFile.getPath().length() + 1);
        VirtualFile moduleOutputDirectory = CompilerPaths.getModuleOutputDirectory(moduleForFile, false);
        if (moduleOutputDirectory == null) {
            return;
        }
        String targetPath = moduleOutputDirectory.getPath() + "/" + pathFromSourceRoot;
        try {
            FileUtil.writeToFile(new File(targetPath), fileContent);
            DebugToolsNotifierUtil.notifyInfo(project, "Compile" + file.getName() + " to target success");
        } catch (IOException ex) {
            logger.error("Compile" + file.getName() + " to target error", ex);
            DebugToolsNotifierUtil.notifyError(project, "Compile" + file.getName() + " to target error. " + ex.getMessage());
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
            presentation.setText("Compile '" + file.getName() + "' to Target");
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
