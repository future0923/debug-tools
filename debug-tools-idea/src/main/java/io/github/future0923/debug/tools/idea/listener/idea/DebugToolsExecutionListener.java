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
package io.github.future0923.debug.tools.idea.listener.idea;

import com.intellij.execution.ExecutionListener;
import com.intellij.execution.ShortenCommandLine;
import com.intellij.execution.application.ApplicationConfiguration;
import com.intellij.execution.process.KillableColoredProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import io.github.future0923.debug.tools.base.hutool.core.io.FileUtil;
import io.github.future0923.debug.tools.base.hutool.core.thread.ThreadUtil;
import io.github.future0923.debug.tools.base.utils.DebugToolsFileUtils;
import io.github.future0923.debug.tools.idea.setting.DebugToolsSettingState;
import io.github.future0923.debug.tools.idea.utils.DebugToolsAttachUtils;
import io.github.future0923.debug.tools.idea.utils.StateUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * @author future0923
 */
public class DebugToolsExecutionListener implements ExecutionListener {

    private static final Logger log = Logger.getInstance(DebugToolsExecutionListener.class);

    @Override
    public void processStarted(@NotNull String executorId, @NotNull ExecutionEnvironment env, @NotNull ProcessHandler handler) {
        Project project = env.getProject();
        String runClassName = env.getRunProfile().getClass().getName();
        if (!StringUtils.endsWith(runClassName, "SpringBootApplicationRunConfiguration") && !StringUtils.endsWithIgnoreCase(runClassName, "ApplicationConfiguration")) {
            return;
        }
        if (handler instanceof KillableColoredProcessHandler.Silent) {
            String pid = String.valueOf(((KillableColoredProcessHandler.Silent) handler).getProcess().pid());
            // 处理 shorten command line
            ApplicationConfiguration runProfile = (ApplicationConfiguration) env.getRunProfile();
            ShortenCommandLine shortenCommandLine = runProfile.getShortenCommandLine();
            if (ShortenCommandLine.CLASSPATH_FILE.equals(shortenCommandLine)) {
                StateUtils.setShortenCommandLineMap(pid, runProfile.getMainClassName());
            }
            // 自动附着
            DebugToolsSettingState settingState = DebugToolsSettingState.getInstance(project);
            if (!settingState.getAutoAttach()) {
                return;
            }
            String agentPath = settingState.getAgentPath();
            ThreadUtil.createThreadFactory("Auto-Attach").newThread(() -> {
                File file = DebugToolsFileUtils.getAutoAttachFile();
                int index = 0;
                for (; ; ) {
                    if (index++ > 30) {
                        break;
                    }
                    ThreadUtil.sleep(1000);
                    String data = FileUtil.readUtf8String(file);
                    if ("1".equals(data)) {
                        DebugToolsAttachUtils.attachLocal(
                                project,
                                pid,
                                runProfile.getMainClassName(),
                                agentPath,
                                () -> {
                                    StateUtils.getClassLoaderComboBox(project).refreshClassLoaderLater(true);
                                    settingState.setLocal(true);
                                }
                        );
                        break;
                    }
                }
            }).start();
        }
    }

    @Override
    public void processTerminated(@NotNull String executorId, @NotNull ExecutionEnvironment env, @NotNull ProcessHandler handler, int exitCode) {
        String runClassName = env.getRunProfile().getClass().getName();
        if (!StringUtils.endsWith(runClassName, "SpringBootApplicationRunConfiguration") && !StringUtils.endsWithIgnoreCase(runClassName, "ApplicationConfiguration")) {
            return;
        }
        if (handler instanceof KillableColoredProcessHandler.Silent) {
            ApplicationConfiguration runProfile = (ApplicationConfiguration) env.getRunProfile();
            ShortenCommandLine shortenCommandLine = runProfile.getShortenCommandLine();
            if (ShortenCommandLine.CLASSPATH_FILE.equals(shortenCommandLine)) {
                String pid = String.valueOf(((KillableColoredProcessHandler.Silent) handler).getProcess().pid());
                StateUtils.removeShortenCommandLineMap(pid);
            }
        }
    }
}
