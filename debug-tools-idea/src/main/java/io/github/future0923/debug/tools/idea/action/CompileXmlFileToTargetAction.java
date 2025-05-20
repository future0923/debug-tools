/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
