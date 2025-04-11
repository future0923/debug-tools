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
import io.github.future0923.debug.tools.idea.client.http.HttpClientUtils;
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
        try {
            runDTO.setClassLoader(HttpClientUtils.defaultClassLoader(project));
        } catch (Exception ex) {
            log.error("execute last with default class loader request error", ex);
            Messages.showErrorDialog(ex.getMessage(), "执行失败");
            return;
        }
        RunTargetMethodRequestPacket packet = new RunTargetMethodRequestPacket(runDTO);
        SocketSendUtils.send(project, packet);
    }
}
