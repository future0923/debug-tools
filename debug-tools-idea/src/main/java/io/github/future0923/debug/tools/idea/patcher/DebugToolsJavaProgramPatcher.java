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
package io.github.future0923.debug.tools.idea.patcher;

import com.intellij.execution.CantRunException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.runners.JavaProgramPatcher;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import io.github.future0923.debug.tools.base.config.AgentArgs;
import io.github.future0923.debug.tools.base.enums.PrintSqlType;
import io.github.future0923.debug.tools.base.hutool.core.io.FileUtil;
import io.github.future0923.debug.tools.base.hutool.core.util.BooleanUtil;
import io.github.future0923.debug.tools.base.utils.DebugToolsExecUtils;
import io.github.future0923.debug.tools.base.utils.DebugToolsFileUtils;
import io.github.future0923.debug.tools.idea.runner.HotswapDebugExecutor;
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
        if (isMavenAndGrade(configuration, javaParameters)) {
            return;
        }
        Project project = (configuration instanceof RunConfiguration) ? ((RunConfiguration) configuration).getProject() : null;
        if (project == null) {
            return;
        }
        applyForConfiguration(executor, configuration, javaParameters, project);
    }

    private boolean isMavenAndGrade(RunProfile configuration, JavaParameters javaParameters) {
        if ("org.jetbrains.idea.maven.execution.MavenRunConfiguration".equals(configuration.getClass().getName())) {
            return true;
        }
        if ("org.jetbrains.plugins.gradle.service.execution.GradleRunConfiguration".equals(configuration.getClass().getName())) {
            return true;
        }
        if ("org.codehaus.classworlds.Launcher".equals(javaParameters.getMainClass())) {
            return true;
        }
        if ("org.codehaus.plexus.classworlds.launcher.Launcher".equals(javaParameters.getMainClass())) {
            return true;
        }
        return false;
    }

    private void applyForConfiguration(Executor executor, RunProfile configuration, JavaParameters javaParameters, Project project) {
        log.debug("Applying HotSwapAgent to configuration " + (configuration != null ? configuration.getName() : ""));
        String jdkPath;
        try {
            jdkPath = javaParameters.getJdkPath();
        } catch (CantRunException e) {
            DebugToolsNotifierUtil.notifyError(project, e.getMessage());
            return;
        }
        String jdkVersion = DcevmUtils.getJdkVersion(jdkPath);
        if (jdkVersion == null) {
            DebugToolsNotifierUtil.notifyError(project, "Failed to obtain the running version of jdk.");
            return;
        }
        if (jdkVersion.startsWith("17") || jdkVersion.startsWith("21")) {
            javaParameters.getVMParametersList().add("-XX:+EnableDynamicAgentLoading");
        } else if (jdkVersion.startsWith("25")) {
            javaParameters.getVMParametersList().add("-XX:+EnableDynamicAgentLoading");
            javaParameters.getVMParametersList().add("--enable-native-access=ALL-UNNAMED");
        }
        DebugToolsSettingState settingState = DebugToolsSettingState.getInstance(project);
        String agentPath = settingState.loadAgentPath(project);
        boolean traceSql = settingState.getTraceMethodDTO() != null && BooleanUtil.isTrue(settingState.getTraceMethodDTO().getTraceMethod()) && BooleanUtil.isTrue(settingState.getTraceMethodDTO().getTraceSQL());
        // 根据执行器判断是否使用hotswap
        boolean hotswap = HotswapDebugExecutor.EXECUTOR_ID.equals(executor.getId());
        if (!PrintSqlType.NO.equals(settingState.getPrintSql())
                || hotswap
                || traceSql) {
            AgentArgs agentArgs = new AgentArgs();
            agentArgs.setServer(Boolean.FALSE.toString());
            if (hotswap) {
                //ProjectRootManager rootManager = ProjectRootManager.getInstance(project);
                //rootManager.getProjectSdk();
                if (jdkVersion.startsWith("11")
                        || jdkVersion.startsWith("17")
                        || jdkVersion.startsWith("21")
                        || jdkVersion.startsWith("25")) {
                    agentArgs.setHotswap(Boolean.TRUE.toString());
                    javaParameters.getVMParametersList().add("-XX:+AllowEnhancedClassRedefinition");
                    addVm(javaParameters);
                } else if (jdkVersion.startsWith("1.8")) {
                    if (DcevmUtils.isDcevmInstalledLikeAltJvm(jdkPath)) {
                        agentArgs.setHotswap(Boolean.TRUE.toString());
                        javaParameters.getVMParametersList().add("-XXaltjvm=dcevm");
                    }
                    if (!DcevmUtils.isDCEVMPresent(jdkPath)) {
                        DebugToolsNotifierUtil.notifyError(project, "DCEVM is not installed");
                    }
                    try {
                        DebugToolsExecUtils.findToolsJarNoCheckVersion(jdkPath);
                    } catch (Exception e) {
                        DebugToolsNotifierUtil.notifyError(project, "Can't find tools.jar. Please run it in the JDK environment.");
                    }
                } else {
                    DebugToolsNotifierUtil.notifyError(project, "hotswap not support " + jdkVersion + " version");
                }
            }
            agentArgs.setPrintSql(settingState.getPrintSql().getType());
            agentArgs.setLogLevel(settingState.getLogLevel());
            agentArgs.setTraceSql(Boolean.toString(traceSql));
            agentArgs.setAutoAttach(settingState.getAutoAttach().toString());
            agentArgs.setAutoSaveSql(settingState.getAutoSaveSql().toString());
            agentArgs.setSqlRetentionDays(settingState.getSqlRetentionDays());
            if (settingState.getAutoAttach()) {
                FileUtil.writeUtf8String("0", DebugToolsFileUtils.getAutoAttachFile());
            }
            javaParameters.getVMParametersList().add("-javaagent:" + agentPath + "=" + agentArgs.format());
        } else {
            if (settingState.getAutoAttach()) {
                FileUtil.writeUtf8String("1", DebugToolsFileUtils.getAutoAttachFile());
            }
        }
    }

    private static void addVm(JavaParameters javaParameters) {
        javaParameters.getVMParametersList().add("--add-opens");
        javaParameters.getVMParametersList().add("java.base/java.lang=ALL-UNNAMED");
        javaParameters.getVMParametersList().add("--add-opens");
        javaParameters.getVMParametersList().add("java.base/jdk.internal.loader=ALL-UNNAMED");
        javaParameters.getVMParametersList().add("--add-opens");
        javaParameters.getVMParametersList().add("java.base/java.io=ALL-UNNAMED");
        javaParameters.getVMParametersList().add("--add-opens");
        javaParameters.getVMParametersList().add("java.desktop/java.beans=ALL-UNNAMED");
        javaParameters.getVMParametersList().add("--add-opens");
        javaParameters.getVMParametersList().add("java.desktop/com.sun.beans=ALL-UNNAMED");
        javaParameters.getVMParametersList().add("--add-opens");
        javaParameters.getVMParametersList().add("java.desktop/com.sun.beans.introspect=ALL-UNNAMED");
        javaParameters.getVMParametersList().add("--add-opens");
        javaParameters.getVMParametersList().add("java.desktop/com.sun.beans.util=ALL-UNNAMED");
        javaParameters.getVMParametersList().add("--add-opens");
        javaParameters.getVMParametersList().add("java.base/sun.security.action=ALL-UNNAMED");
        javaParameters.getVMParametersList().add("--add-opens");
        javaParameters.getVMParametersList().add("java.base/java.lang.reflect=ALL-UNNAMED");
        javaParameters.getVMParametersList().add("--add-opens");
        javaParameters.getVMParametersList().add("java.base/java.net=ALL-UNNAMED");
        javaParameters.getVMParametersList().add("--add-opens");
        javaParameters.getVMParametersList().add("java.base/sun.nio.ch=ALL-UNNAMED");
    }
}
