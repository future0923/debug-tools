package io.github.future0923.debug.tools.idea.utils;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.Messages;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import io.github.future0923.debug.tools.base.config.AgentArgs;
import io.github.future0923.debug.tools.base.utils.DebugToolsExecUtils;
import io.github.future0923.debug.tools.client.DebugToolsSocketClient;
import io.github.future0923.debug.tools.idea.client.ApplicationProjectHolder;
import io.github.future0923.debug.tools.idea.client.http.HttpClientUtils;
import io.github.future0923.debug.tools.idea.setting.DebugToolsSettingState;

import java.io.IOException;
import java.net.ConnectException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author future0923
 */
public class DebugToolsAttachUtils {

    private static final Logger log = Logger.getInstance(DebugToolsAttachUtils.class);

    public static List<VirtualMachineDescriptor> vmList() {
        return VirtualMachine.list().stream().filter(descriptor -> !descriptor.displayName().startsWith("org.gradle")
                && !descriptor.displayName().startsWith("org.jetbrains")
                && !descriptor.displayName().startsWith("com.intellij")).collect(Collectors.toList());
    }

    public static void vmConsumer(Consumer<VirtualMachineDescriptor> consumer) {
        vmConsumer(null, consumer);
    }

    public static void vmConsumer(Consumer<Integer> sizeConsumer, Consumer<VirtualMachineDescriptor> descriptorConsumer) {
        List<VirtualMachineDescriptor> list = vmList();
        if (sizeConsumer != null) {
            sizeConsumer.accept(list.size());
        }
        for (VirtualMachineDescriptor descriptor : list) {
            if (descriptor.displayName().startsWith("org.gradle")
                    || descriptor.displayName().startsWith("org.jetbrains")
                    || descriptor.displayName().startsWith("com.intellij")
            ) {
                continue;
            }
            descriptorConsumer.accept(descriptor);
        }
    }

    public static void attachRemote(Project project, String host, int tcpPort) {
        HttpClientUtils.removeAllClassLoaderCache(project);
        try {
            String applicationName = HttpClientUtils.getApplicationName(project, true);
            DebugToolsSettingState settingState = DebugToolsSettingState.getInstance(project);
            ApplicationProjectHolder.Info info = ApplicationProjectHolder.getInfo(applicationName);
            if (info != null) {
                if (!settingState.isLocal() && !info.getClient().isClosed()) {
                    return;
                }
                if (settingState.isLocal() && !info.getClient().isClosed()) {
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
        DebugToolsSocketClient client = ApplicationProjectHolder.setProject(applicationName, project, null, host, tcpPort).getClient();
        try {
            client.disconnect();
        } catch (Exception ignored) {
        }
        try {
            client.start();
        } catch (Exception ex) {
            ApplicationProjectHolder.close(project);
            Messages.showErrorDialog(project, ex.getMessage(), "Connection Error");
        }
    }

    public static void attachLocal(Project project, String pid, String applicationName, String agentPath) {
        HttpClientUtils.removeAllClassLoaderCache(project);
        int tcpPort = ApplicationProjectHolder.getTcpPort(applicationName);
        Integer httpPort = ApplicationProjectHolder.getHttpPort(applicationName);
        DebugToolsSettingState settingState = DebugToolsSettingState.getInstance(project);
        settingState.setLocalHttpPort(httpPort);
        ApplicationProjectHolder.Info info = ApplicationProjectHolder.getInfo(applicationName);
        if (info != null) {
            if (settingState.isLocal() && !info.getClient().isClosed()) {
                return;
            }
            if (!settingState.isLocal() && !info.getClient().isClosed()) {
                ApplicationProjectHolder.close(applicationName);
            }
        }
        DebugToolsSocketClient client = ApplicationProjectHolder.setProject(applicationName, project, pid, "127.0.0.1", tcpPort).getClient();
        try {
            client.reconnect();
        } catch (ConnectException e) {
            // attach;
            AgentArgs agentArgs = new AgentArgs();
            agentArgs.setApplicationName(applicationName);
            agentArgs.setTcpPort(String.valueOf(tcpPort));
            agentArgs.setHttpPort(String.valueOf(httpPort));
            attach(() -> {
                try {
                    client.start();
                } catch (Exception ex) {
                    log.error("start client exception", ex);
                    DebugToolsNotifierUtil.notifyError(project, "服务拒绝连接，请确认服务端已经启动");
                }
            }, project, pid, agentPath, agentArgs.format());

        } catch (Exception e) {
            log.error("attach失败 [errMsg:{}]", e.getMessage());
        }
    }

    public static void attach(Runnable runnable, Project project, String pid, String agentPath, String agentParam) {
        CompletableFuture.runAsync(() -> {
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

    public static boolean status(Project project, String pid) {
        // jps
        Sdk projectSdk = ProjectRootManager.getInstance(project).getProjectSdk();
        if (null != projectSdk) {
            String jps = projectSdk.getHomePath() + "/bin/jps";
            String result = DebugToolsExecUtils.exec(jps);
            return result != null && result.contains(pid);
        }
        return true;
    }
}
