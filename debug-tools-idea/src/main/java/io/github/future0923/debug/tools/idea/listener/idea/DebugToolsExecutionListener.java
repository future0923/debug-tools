package io.github.future0923.debug.tools.idea.listener.idea;

import com.intellij.execution.ExecutionListener;
import com.intellij.execution.application.ApplicationConfiguration;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.project.Project;
import io.github.future0923.debug.tools.idea.constant.IdeaPluginProjectConstants;
import io.github.future0923.debug.tools.idea.setting.DebugToolsSettingState;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * @author future0923
 */
public class DebugToolsExecutionListener implements ExecutionListener {

    @Override
    public void processStartScheduled(@NotNull String executorId, @NotNull ExecutionEnvironment env) {
        RunProfile runProfile = env.getRunProfile();
        Project project = env.getProject();
        DebugToolsSettingState settingState = DebugToolsSettingState.getInstance(project);
        // 检查是否是一个 Java 运行配置
        if (runProfile instanceof ApplicationConfiguration config) {
            String jvmArg = "-javaagent:" + settingState.loadAgentPath(project);
            // 获取当前的 VM options，并添加 -javaagent 参数
            String existingOptions = config.getVMParameters();
            if (StringUtils.isNotBlank(existingOptions)) {
                existingOptions = IdeaPluginProjectConstants.AGENT_TMP_REGEX.matcher(existingOptions).replaceAll("");
                if (settingState.getPrintSql()) {
                    if (StringUtils.isNotBlank(existingOptions)) {
                        config.setVMParameters(existingOptions + " " + jvmArg);
                    } else {
                        config.setVMParameters(jvmArg);
                    }
                } else {
                    config.setVMParameters(existingOptions);
                }
            } else {
                if (settingState.getPrintSql()) {
                    config.setVMParameters(jvmArg);
                } else {
                    config.setVMParameters("");
                }
            }
        }
    }

    //@Override
    //public void processStarted(@NotNull String executorId, @NotNull ExecutionEnvironment env, @NotNull ProcessHandler handler) {
    //    String runClassName = env.getRunProfile().getClass().getName();
    //    if (!StringUtils.endsWith(runClassName, "SpringBootApplicationRunConfiguration") && !StringUtils.endsWithIgnoreCase(runClassName, "ApplicationConfiguration")) {
    //        return;
    //    }
    //    if (handler instanceof KillableColoredProcessHandler.Silent) {
    //        String pid = String.valueOf(((KillableColoredProcessHandler.Silent) handler).getProcess().pid());
    //    }
    //}
}