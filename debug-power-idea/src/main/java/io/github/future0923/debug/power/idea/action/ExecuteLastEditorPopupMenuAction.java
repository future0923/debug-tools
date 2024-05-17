package io.github.future0923.debug.power.idea.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import io.github.future0923.debug.power.idea.model.ServerDisplayValue;
import io.github.future0923.debug.power.idea.setting.DebugPowerSettingState;
import io.github.future0923.debug.power.idea.utils.DebugPowerAttachUtils;
import org.jetbrains.annotations.NotNull;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

/**
 * @author future0923
 */
public class ExecuteLastEditorPopupMenuAction extends AnAction {

    private static final Logger log = Logger.getInstance(ExecuteLastEditorPopupMenuAction.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }
        DebugPowerSettingState settingState = DebugPowerSettingState.getInstance(project);
        ServerDisplayValue attach = settingState.getAttach();
        CompletableFuture.runAsync(() -> {
            String pathname = project.getBasePath() + "/.idea/DebugPower/agent.json";
            String agentParam = "file://" + URLEncoder.encode(pathname, StandardCharsets.UTF_8);
            DebugPowerAttachUtils.attach(project, attach.getKey(), settingState.getAgentPath(), agentParam);
        });
    }
}
