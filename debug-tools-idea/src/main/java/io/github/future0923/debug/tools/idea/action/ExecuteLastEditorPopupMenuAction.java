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
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.io.FileUtil;
import io.github.future0923.debug.tools.common.dto.RunDTO;
import io.github.future0923.debug.tools.common.protocal.packet.request.RunTargetMethodRequestPacket;
import io.github.future0923.debug.tools.common.utils.DebugToolsJsonUtils;
import io.github.future0923.debug.tools.idea.client.socket.utils.SocketSendUtils;
import io.github.future0923.debug.tools.idea.constant.IdeaPluginProjectConstants;
import io.github.future0923.debug.tools.idea.utils.DebugToolsIcons;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author future0923
 */
public class ExecuteLastEditorPopupMenuAction extends AnAction {

    private static final Logger log = Logger.getInstance(ExecuteLastEditorPopupMenuAction.class);

    public ExecuteLastEditorPopupMenuAction() {
        getTemplatePresentation().setText("Execute Last");
        getTemplatePresentation().setIcon(DebugToolsIcons.Last);
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
        RunTargetMethodRequestPacket packet = new RunTargetMethodRequestPacket(runDTO);
        SocketSendUtils.send(project, packet);
    }
}
