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
package io.github.future0923.debug.tools.idea.listener.idea;

import com.intellij.execution.ExecutionListener;
import com.intellij.execution.application.ApplicationConfiguration;
import com.intellij.execution.process.KillableColoredProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.project.Project;
import io.github.future0923.debug.tools.idea.setting.DebugToolsSettingState;
import io.github.future0923.debug.tools.idea.utils.DebugToolsAttachUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * @author future0923
 */
public class DebugToolsExecutionListener implements ExecutionListener {

    @Override
    public void processStarted(@NotNull String executorId, @NotNull ExecutionEnvironment env, @NotNull ProcessHandler handler) {
        Project project = env.getProject();
        DebugToolsSettingState settingState = DebugToolsSettingState.getInstance(project);
        if (!settingState.getAutoAttach()) {
            return;
        }
        String runClassName = env.getRunProfile().getClass().getName();
        if (!StringUtils.endsWith(runClassName, "SpringBootApplicationRunConfiguration") && !StringUtils.endsWithIgnoreCase(runClassName, "ApplicationConfiguration")) {
            return;
        }
        if (handler instanceof KillableColoredProcessHandler.Silent) {
            String pid = String.valueOf(((KillableColoredProcessHandler.Silent) handler).getProcess().pid());
            String agentPath = settingState.loadAgentPath();
            DebugToolsAttachUtils.attachLocal(project, pid, ((ApplicationConfiguration)env.getRunProfile()).getMainClassName(), agentPath);
        }
    }
}
