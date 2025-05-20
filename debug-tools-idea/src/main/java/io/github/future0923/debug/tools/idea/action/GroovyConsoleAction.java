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

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.io.FileUtilRt;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ThrowableRunnable;
import io.github.future0923.debug.tools.idea.constant.IdeaPluginProjectConstants;
import io.github.future0923.debug.tools.idea.utils.DebugToolsIcons;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author future0923
 */
public class GroovyConsoleAction extends AnAction {

    private static final Logger log = Logger.getInstance(GroovyConsoleAction.class);

    public GroovyConsoleAction() {
        getTemplatePresentation().setText("Groovy Console");
        getTemplatePresentation().setIcon(DebugToolsIcons.Groovy);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }
        String relativeFilePath = IdeaPluginProjectConstants.GROOVY_CONSOLE_FILE;
        String text = "";
        try {
            InputStream inputStream = GroovyConsoleAction.class.getClassLoader().getResourceAsStream(relativeFilePath);
            if (inputStream != null) {
                text = FileUtil.loadTextAndClose(inputStream);
            }
        } catch (IOException ex) {
            log.error("读取{}失败", ex, relativeFilePath);
        }
        final String fileContent = text;
        String filePath = FileUtilRt.toSystemIndependentName(PathManager.getScratchPath()) + IdeaPluginProjectConstants.SCRATCH_PATH + "/" + relativeFilePath;
        int index = filePath.lastIndexOf("/");
        String parentPath = (index == -1) ? "" : filePath.substring(0, index);
        String fileName = (index == -1) ? filePath : filePath.substring(index + 1);
        try {
            WriteAction.run((ThrowableRunnable<Throwable>) () -> {
                VirtualFile parentFolder = VfsUtil.createDirectoryIfMissing(parentPath);
                if (parentFolder == null) {
                    return;
                }
                VirtualFile child = parentFolder.findChild(fileName);
                if (child == null) {
                    VirtualFile virtualFile = parentFolder.createChildData(IdeaPluginProjectConstants.ROOT_TYPE_ID, fileName);
                    VfsUtil.saveText(virtualFile, fileContent);
                    FileEditorManager.getInstance(project).openFile(virtualFile, true);
                } else {
                    FileEditorManager.getInstance(project).openFile(child, true);
                }
            });
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }
}
