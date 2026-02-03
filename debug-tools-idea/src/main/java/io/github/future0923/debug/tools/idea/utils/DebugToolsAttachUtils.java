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
package io.github.future0923.debug.tools.idea.utils;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import io.github.future0923.debug.tools.base.config.AgentArgs;
import io.github.future0923.debug.tools.idea.client.ApplicationProjectHolder;
import io.github.future0923.debug.tools.idea.client.http.HttpClientUtils;
import io.github.future0923.debug.tools.idea.client.socket.DebugToolsNettyTcpClient;
import io.github.future0923.debug.tools.idea.model.VirtualMachineDescriptorDTO;
import io.github.future0923.debug.tools.idea.setting.DebugToolsSettingState;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author future0923
 */
public class DebugToolsAttachUtils {

    private static final Logger log = Logger.getInstance(DebugToolsAttachUtils.class);

    public static List<VirtualMachineDescriptorDTO> vmList() {
        List<VirtualMachineDescriptor> descriptorList = VirtualMachine.list();
        Set<String> pidSet = descriptorList.stream().map(VirtualMachineDescriptor::id).collect(Collectors.toSet());
        StateUtils.refreshShortenCommandLineMap(pidSet);
        return descriptorList
                .stream()
                .map(descriptor -> {
                    VirtualMachineDescriptorDTO descriptorDTO = new VirtualMachineDescriptorDTO();
                    descriptorDTO.setId(descriptor.id());
                    descriptorDTO.setDisplayName(StateUtils.getShortenCommandLineMap(descriptor.id(), descriptor.displayName()));
                    return descriptorDTO;
                })
                .filter(descriptorDTO -> !descriptorDTO.getDisplayName().startsWith("org.gradle")
                        && !descriptorDTO.getDisplayName().startsWith("org.jetbrains")
                        && !descriptorDTO.getDisplayName().startsWith("com.intellij")).collect(Collectors.toList());
    }

    public static void vmConsumer(Consumer<VirtualMachineDescriptorDTO> consumer) {
        vmConsumer(null, consumer);
    }

    public static void vmConsumer(Consumer<Integer> sizeConsumer, Consumer<VirtualMachineDescriptorDTO> descriptorConsumer) {
        List<VirtualMachineDescriptorDTO> list = vmList();
        if (sizeConsumer != null) {
            sizeConsumer.accept(list.size());
        }
        for (VirtualMachineDescriptorDTO descriptor : list) {
            descriptorConsumer.accept(descriptor);
        }
    }

    public static void attachRemote(Project project, String host, int tcpPort) {
        try {
            String applicationName = HttpClientUtils.getApplicationName(project, false);
            StateUtils.setProjectAttachApplicationName(project, applicationName);
            DebugToolsSettingState settingState = DebugToolsSettingState.getInstance(project);
            ApplicationProjectHolder.Info info = ApplicationProjectHolder.getInfo(applicationName);
            if (info != null) {
                if (!settingState.isLocal() && info.getClient().isActive()) {
                    return;
                }
                if (settingState.isLocal() && info.getClient().isActive()) {
                    ApplicationProjectHolder.close(applicationName);
                }
            }
        } catch (Exception ignored) {
            // 没有连接
        }
        String applicationName;
        try {
            applicationName = HttpClientUtils.getApplicationName(project, false);
        } catch (Exception e) {
            DebugToolsNotifierUtil.notifyError(project, e.getMessage());
            return;
        }
        DebugToolsNettyTcpClient client = ApplicationProjectHolder.setProject(applicationName, project, null, host, tcpPort).getClient();
        try {
            client.stop();
        } catch (Exception ignored) {
        }
        try {
            client.start();
            StateUtils.getClassLoaderComboBox(project).refreshClassLoaderLater(true);
            StateUtils.getPrintSqlPanel(project).refresh();
        } catch (Exception ex) {
            ApplicationProjectHolder.close(project);
            Messages.showErrorDialog(project, ex.getMessage(), "Connection Error");
        }
    }

    public static void attachLocal(Project project, String pid, String applicationName, String agentPath) {
        attachLocal(project, pid, applicationName, agentPath, null);
    }

    public static void attachLocal(Project project, String pid, String applicationName, String agentPath, Runnable onConnected) {
        StateUtils.setProjectAttachApplicationName(project, applicationName);
        int tcpPort = ApplicationProjectHolder.getTcpPort(applicationName);
        Integer httpPort = ApplicationProjectHolder.getHttpPort(applicationName);
        DebugToolsSettingState settingState = DebugToolsSettingState.getInstance(project);
        settingState.setLocalHttpPort(httpPort);
        ApplicationProjectHolder.Info info = ApplicationProjectHolder.getInfo(applicationName);
        if (info != null) {
            boolean connected = info.getClient().isActive();
            if (settingState.isLocal() && connected) {
                return;
            }
            if (!settingState.isLocal() && connected) {
                ApplicationProjectHolder.close(applicationName);
            }
        }
        DebugToolsNettyTcpClient client = ApplicationProjectHolder.setProject(applicationName, project, pid, "127.0.0.1", tcpPort).getClient();
        try {
            client.connect(() -> {
                // attach;
                AgentArgs agentArgs = new AgentArgs();
                agentArgs.setApplicationName(applicationName);
                agentArgs.setTcpPort(String.valueOf(tcpPort));
                agentArgs.setHttpPort(String.valueOf(httpPort));
                attach(() -> {
                    try {
                        client.connect(null);
                        if (onConnected != null) {
                            onConnected.run();
                        }
                    } catch (Exception ex) {
                        log.error("start client exception", ex);
                        DebugToolsNotifierUtil.notifyError(project, "服务拒绝连接，请确认服务端已经启动");
                    }
                }, project, pid, agentPath, agentArgs.format());
            });
        } catch (Exception e) {
            log.error("attach失败 [errMsg:{}]", e.getMessage());
        }
    }

    public static void attach(Runnable runnable, Project project, String pid, String agentPath, String agentParam) {
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            VirtualMachine virtualMachine = null;
            try {
                virtualMachine = VirtualMachine.attach(pid);
                virtualMachine.loadAgent(agentPath, agentParam);
                runnable.run();
            } catch (IOException ioException) {
                if (ioException.getMessage() != null && ioException.getMessage().contains("Non-numeric value found")) {
                    log.warn("jdk较低版本附加较高版本，没有影响可以忽略");
                    runnable.run();
                } else {
                    if (Objects.equals(ioException.getMessage(), "No such process")) {
                        DebugToolsNotifierUtil.notifyError(project, "没有找到attach这个进程，请刷新重新attach");
                    } else if (Objects.equals(ioException.getMessage(), "Connection refused")) {
                        DebugToolsNotifierUtil.notifyError(project, "进程拒绝连接，请确认进程已经启动");
                    } else if (Objects.equals(ioException.getMessage(), "File exists")) {
                        // 可以忽略掉，项目重新启动时，开启自动附着和重新连接会同时尝试attach时会报File exists
                    } else {
                        log.error("attach agent error：", ioException);
                        DebugToolsNotifierUtil.notifyError(project, "attach agent error：" + ioException.getMessage());
                    }
                }
            } catch (AgentLoadException agentLoadException) {
                if ("0".equals(agentLoadException.getMessage())) {
                    log.warn("jdk较高版本附加较低版本，没有影响可以忽略");
                    runnable.run();
                } else {
                    log.error("attach核心依赖失败 [errMsg:{}]", agentLoadException.getMessage());
                    DebugToolsNotifierUtil.notifyError(project, "attach失败:" + agentLoadException.getMessage());
                }
            } catch (Exception exception) {
                log.error("attach失败 [errMsg:{}]", exception.getMessage());
                DebugToolsNotifierUtil.notifyError(project, "attach失败:" + exception.getMessage());
            } finally {
                if (virtualMachine != null) {
                    try {
                        virtualMachine.detach();
                    } catch (Exception ignored) {
                    }
                }
            }
        });
    }

}
