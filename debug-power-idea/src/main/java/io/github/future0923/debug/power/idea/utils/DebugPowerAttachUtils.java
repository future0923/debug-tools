package io.github.future0923.debug.power.idea.utils;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ProjectRootManager;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import io.github.future0923.debug.power.base.config.AgentArgs;
import io.github.future0923.debug.power.base.utils.DebugPowerExecUtils;
import io.github.future0923.debug.power.client.DebugPowerSocketClient;
import io.github.future0923.debug.power.idea.client.ApplicationProjectHolder;

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
public class DebugPowerAttachUtils {

    private static final Logger log = Logger.getInstance(DebugPowerAttachUtils.class);

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

    public static void attachLocal(Project project, String pid, String applicationName, String agentPath) {
        int port = ApplicationProjectHolder.getPort(applicationName);
        ApplicationProjectHolder.Info info = ApplicationProjectHolder.setProject(applicationName, project, pid, "127.0.0.1", port);
        DebugPowerSocketClient client = info.getClient();
        if (!client.isClosed()) {
            return;
        }
        try {
            client.reconnect();
        } catch (ConnectException e) {
            // attach;
            AgentArgs agentArgs = new AgentArgs();
            agentArgs.setApplicationName(applicationName);
            agentArgs.setListenPort(String.valueOf(port));
            attach(() -> {
                try {
                    client.start();
                } catch (Exception ex) {
                    log.error("start client exception", ex);
                    DebugPowerNotifierUtil.notifyError(project, "服务拒绝连接，请确认服务端已经启动");
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
                        DebugPowerNotifierUtil.notifyError(project, "没有找到attach这个进程，请刷新重新attach");
                    } else if (Objects.equals(ioException.getMessage(), "Connection refused")) {
                        DebugPowerNotifierUtil.notifyError(project, "进程拒绝连接，请确认进程已经启动");
                    } else {
                        log.error("attach agent error：", ioException);
                        DebugPowerNotifierUtil.notifyError(project, "attach agent error：" + ioException.getMessage());
                    }
                }
            } catch (AgentLoadException agentLoadException) {
                if ("0".equals(agentLoadException.getMessage())) {
                    log.warn("jdk较高版本附加较低版本，没有影响可以忽略");
                    runnable.run();
                } else {
                    log.error("attach核心依赖失败 [errMsg:{}]", agentLoadException.getMessage());
                    DebugPowerNotifierUtil.notifyError(project, "attach失败:" + agentLoadException.getMessage());
                }
            } catch (Exception exception) {
                log.error("attach失败 [errMsg:{}]", exception.getMessage());
                DebugPowerNotifierUtil.notifyError(project, "attach失败:" + exception.getMessage());
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
            String result = DebugPowerExecUtils.exec(jps);
            return result != null && result.contains(pid);
        }
        return true;
    }
}
