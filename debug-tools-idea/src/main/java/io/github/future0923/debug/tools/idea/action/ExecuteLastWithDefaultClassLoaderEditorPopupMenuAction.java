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

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.io.FileUtil;
import io.github.future0923.debug.tools.common.dto.RunDTO;
import io.github.future0923.debug.tools.common.protocal.http.AllClassLoaderRes;
import io.github.future0923.debug.tools.common.protocal.packet.request.RunTargetMethodRequestPacket;
import io.github.future0923.debug.tools.common.utils.DebugToolsJsonUtils;
import io.github.future0923.debug.tools.idea.client.socket.utils.SocketSendUtils;
import io.github.future0923.debug.tools.idea.constant.IdeaPluginProjectConstants;
import io.github.future0923.debug.tools.idea.tool.DebugToolsToolWindowFactory;
import io.github.future0923.debug.tools.idea.utils.DebugToolsIcons;
import io.github.future0923.debug.tools.idea.utils.StateUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author future0923
 */
public class ExecuteLastWithDefaultClassLoaderEditorPopupMenuAction extends AnAction {

    private static final Logger log = Logger.getInstance(ExecuteLastWithDefaultClassLoaderEditorPopupMenuAction.class);

    public ExecuteLastWithDefaultClassLoaderEditorPopupMenuAction() {
        getTemplatePresentation().setText("Execute Last With Default ClassLoader");
        getTemplatePresentation().setIcon(DebugToolsIcons.Last_ClassLoader);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }
        String pathname = project.getBasePath() + IdeaPluginProjectConstants.PARAM_FILE;
        String json;
        try {
            json = FileUtil.loadFile(new File(pathname), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            Messages.showErrorDialog("Load file error", "执行失败");
            return;
        }
        RunDTO runDTO = DebugToolsJsonUtils.toBean(json, RunDTO.class);
        AllClassLoaderRes.Item projectDefaultClassLoader = StateUtils.getProjectDefaultClassLoader(project);
        if (projectDefaultClassLoader == null) {
            Messages.showErrorDialog("Please select a DefaultClassLoader first.", "执行失败");
            DebugToolsToolWindowFactory.showWindow(project, null);
            return;
        }
        runDTO.setClassLoader(projectDefaultClassLoader);
        RunTargetMethodRequestPacket packet = new RunTargetMethodRequestPacket(runDTO);
        SocketSendUtils.send(project, packet);
    }
}
