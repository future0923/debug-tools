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
package io.github.future0923.debug.tools.idea.tool.action;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import io.github.future0923.debug.tools.base.constants.ProjectConstants;
import io.github.future0923.debug.tools.idea.bundle.DebugToolsBundle;
import io.github.future0923.debug.tools.idea.tool.DebugToolsToolWindow;
import io.github.future0923.debug.tools.idea.utils.DebugToolsIcons;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * SQL历史记录Action
 *
 * @author future0923
 */
public class SqlHistoryAction extends BaseToolAction {

    public SqlHistoryAction() {
        super();
        getTemplatePresentation().setText(DebugToolsBundle.message("action.tool.sql.history.text"));
        getTemplatePresentation().setIcon(DebugToolsIcons.SqlHistory);
        getTemplatePresentation().setDescription(DebugToolsBundle.message("action.tool.sql.history.description"));
    }

    @Override
    protected void doActionPerformed(@NotNull Project project, @NotNull DebugToolsToolWindow toolWindow) {
        try {
            // 获取当前日期
            LocalDate today = LocalDate.now();
            String dateStr = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            
            // 构建SQL文件路径
            String sqlFilePath = String.format("./.idea/%s/sql/%s.sql",ProjectConstants.NAME,dateStr);
            
            // 检查文件是否存在
            File sqlFile = new File(project.getBasePath(), sqlFilePath);
            if (!sqlFile.exists()) {
                // 如果文件不存在，创建一个空文件
                sqlFile.getParentFile().mkdirs();
                sqlFile.createNewFile();
            }
            
            // 刷新文件系统
            LocalFileSystem.getInstance().refreshAndFindFileByPath(sqlFile.getAbsolutePath());
            
            // 打开文件
            VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(sqlFile.getAbsolutePath());
            if (virtualFile != null) {
                virtualFile.refresh(false, false);
                com.intellij.openapi.fileEditor.FileEditorManager.getInstance(project).openFile(virtualFile, true);
            }
            
        } catch (Exception e) {
            com.intellij.openapi.ui.Messages.showErrorDialog(project, 
                "Failed to open SQL history file: " + e.getMessage(), DebugToolsBundle.message("dialog.title.execution.failed"));
        }
    }
} 