package io.github.future0923.debug.power.idea.listener.idea;

import com.intellij.execution.ExecutionListener;
import com.intellij.execution.process.KillableColoredProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import io.github.future0923.debug.power.idea.model.ServerDisplayValue;
import io.github.future0923.debug.power.idea.setting.DebugPowerSettingState;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * @author future0923
 */
public class DebugPowerExecutionListener implements ExecutionListener {

    @Override
    public void processStarted(@NotNull String executorId, @NotNull ExecutionEnvironment env, @NotNull ProcessHandler handler) {
        DebugPowerSettingState settingState = DebugPowerSettingState.getInstance(env.getProject());
        if (!settingState.getRunApplicationAttach()) {
            return;
        }
        String runClassName = env.getRunProfile().getClass().getName();
        if (!StringUtils.endsWith(runClassName, "SpringBootApplicationRunConfiguration") && !StringUtils.endsWithIgnoreCase(runClassName, "ApplicationConfiguration")) {
            return;
        }
        if (handler instanceof KillableColoredProcessHandler.Silent) {
            String pid = String.valueOf(((KillableColoredProcessHandler.Silent) handler).getProcess().pid());
            settingState.setAttach(new ServerDisplayValue(pid, ""));
        }
    }
}
