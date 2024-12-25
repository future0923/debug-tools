package io.github.future0923.debug.tools.idea.patcher;

import com.intellij.execution.Executor;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.runners.JavaProgramPatcher;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import io.github.future0923.debug.tools.base.config.AgentArgs;
import io.github.future0923.debug.tools.idea.setting.DebugToolsSettingState;
import io.github.future0923.debug.tools.idea.utils.DcevmUtils;
import io.github.future0923.debug.tools.idea.utils.DebugToolsNotifierUtil;

/**
 * java参数Patcher
 *
 * @author future0923
 */
public class DebugToolsJavaProgramPatcher extends JavaProgramPatcher {

    private static final Logger log = Logger.getInstance(DebugToolsJavaProgramPatcher.class);

    @Override
    public void patchJavaParameters(Executor executor, RunProfile configuration, JavaParameters javaParameters) {
        Project project = (configuration instanceof RunConfiguration) ? ((RunConfiguration) configuration).getProject() : null;
        if (project == null) {
            return;
        }
        applyForConfiguration(configuration, javaParameters, project);
    }

    private void applyForConfiguration(RunProfile configuration, JavaParameters javaParameters, Project project) {
        log.debug("Applying HotSwapAgent to configuration " + (configuration != null ? configuration.getName() : ""));
        DebugToolsSettingState settingState = DebugToolsSettingState.getInstance(project);
        String agentPath = settingState.loadAgentPath(project);
        if (settingState.getPrintSql() || settingState.getHotswap()) {
            AgentArgs agentArgs = new AgentArgs();
            if (settingState.getHotswap()) {
                ProjectRootManager rootManager = ProjectRootManager.getInstance(project);
                if (rootManager.getProjectSdk() != null) {
                    if (DcevmUtils.isDcevmInstalledLikeAltJvm(rootManager.getProjectSdk())) {
                        javaParameters.getVMParametersList().add("-XXaltjvm=dcevm");
                        agentArgs.setHotswap(Boolean.TRUE.toString());
                    }
                    if (!DcevmUtils.isDCEVMPresent(rootManager.getProjectSdk())) {
                        DebugToolsNotifierUtil.notifyError(project, "DCEVM is not installed");
                    }
                }
            }
            agentArgs.setPrintSql(settingState.getPrintSql().toString());
            javaParameters.getVMParametersList().add("-javaagent:" + agentPath + "=" + agentArgs.format());
        }
    }
}
