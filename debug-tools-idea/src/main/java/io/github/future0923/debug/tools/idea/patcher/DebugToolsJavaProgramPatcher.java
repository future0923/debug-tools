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
import io.github.future0923.debug.tools.base.utils.DebugToolsExecUtils;
import io.github.future0923.debug.tools.base.utils.DebugToolsFileUtils;
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
        applyForConfiguration(configuration, javaParameters, project);
    }

    private boolean isMavenAndGrade(RunProfile configuration, JavaParameters javaParameters) {
        if (configuration.getClass().getName().equals("org.jetbrains.idea.maven.execution.MavenRunConfiguration")) {
            return true;
        }
        if (configuration.getClass().getName().equals("org.jetbrains.plugins.gradle.service.execution.GradleRunConfiguration")) {
            return true;
        }
        if (javaParameters.getMainClass().equals("org.codehaus.classworlds.Launcher")) {
            return true;
        }
        if (javaParameters.getMainClass().equals("org.codehaus.plexus.classworlds.launcher.Launcher")) {
            return true;
        }
        return false;
    }

    private void applyForConfiguration(RunProfile configuration, JavaParameters javaParameters, Project project) {
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
        }
        DebugToolsSettingState settingState = DebugToolsSettingState.getInstance(project);
        String agentPath = settingState.loadAgentPath(project);
        if (!PrintSqlType.NO.equals(settingState.getPrintSql()) || settingState.getHotswap()) {
            AgentArgs agentArgs = new AgentArgs();
            agentArgs.setServer(Boolean.FALSE.toString());
            if (settingState.getHotswap()) {
                //ProjectRootManager rootManager = ProjectRootManager.getInstance(project);
                //rootManager.getProjectSdk();
                if (jdkVersion.startsWith("17") || jdkVersion.startsWith("21")) {
                    agentArgs.setHotswap(Boolean.TRUE.toString());
                    javaParameters.getVMParametersList().add("-XX:+AllowEnhancedClassRedefinition");
                    addVm(javaParameters);
                } else if (jdkVersion.startsWith("11")) {
                    agentArgs.setHotswap(Boolean.TRUE.toString());
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
