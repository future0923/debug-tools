package io.github.future0923.debug.power.idea.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.io.FileUtil;
import io.github.future0923.debug.power.common.dto.RunDTO;
import io.github.future0923.debug.power.common.protocal.packet.request.RunTargetMethodRequestPacket;
import io.github.future0923.debug.power.common.utils.DebugPowerJsonUtils;
import io.github.future0923.debug.power.idea.client.ApplicationProjectHolder;
import io.github.future0923.debug.power.idea.constant.IdeaPluginProjectConstants;
import io.github.future0923.debug.power.idea.utils.DebugPowerIcons;
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
        getTemplatePresentation().setIcon(DebugPowerIcons.last_icon);
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
        RunDTO runDTO = DebugPowerJsonUtils.toBean(json, RunDTO.class);
        RunTargetMethodRequestPacket packet = new RunTargetMethodRequestPacket(runDTO);
        ApplicationProjectHolder.Info info = ApplicationProjectHolder.getInfo(project);
        if (info == null) {
            Messages.showErrorDialog("Run attach first", "执行失败");
            return;
        }
        try {
            info.getClient().getHolder().send(packet);
        } catch (Exception ex) {
            log.error("execute last request error", ex);
            Messages.showErrorDialog(ex.getMessage(), "执行失败");
        }
    }
}
