package io.github.future0923.debug.power.idea.utils;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.VirtualMachine;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * @author future0923
 */
public class DebugPowerAttachUtils {

    private static final Logger log = Logger.getInstance(DebugPowerAttachUtils.class);

    public static void attach(Project project, String pid, String agentPath, String agentParam) {
        CompletableFuture.runAsync(() -> {
            VirtualMachine virtualMachine = null;
            try {
                virtualMachine = VirtualMachine.attach(pid);
                virtualMachine.loadAgent(agentPath, agentParam);
            } catch (IOException ioException) {
                if (ioException.getMessage() != null && ioException.getMessage().contains("Non-numeric value found")) {
                    log.warn("jdk较低版本附加较高版本，没有影响可以忽略");
                } else {
                    if (Objects.equals(ioException.getMessage(), "No such process")) {
                        DebugPowerNotifierUtil.notifyError(project, "没有找到attach这个进程，请刷新重新attach");
                    } else if (Objects.equals(ioException.getMessage(), "Connection refused")) {
                        DebugPowerNotifierUtil.notifyError(project, "进程拒绝连接，请确认进程已经启动");
                    } else {
                        log.error("没有找到核心依赖 [jarFilePath:{} errMsg:{}]", agentPath, ioException.getMessage());
                        DebugPowerNotifierUtil.notifyError(project, "没有找到核心依赖，请 clear cache 后重试");
                    }
                }
            } catch (AgentLoadException agentLoadException) {
                if ("0".equals(agentLoadException.getMessage())) {
                    log.warn("jdk较高版本附加较低版本，没有影响可以忽略");
                } else {
                    log.error("attach核心依赖失败 [errMsg:{}]", agentLoadException.getMessage());
                    DebugPowerNotifierUtil.notifyError(project, "attach失败");
                }
            } catch (Exception exception) {
                log.error("attach失败 [errMsg:{}]", exception.getMessage());
                DebugPowerNotifierUtil.notifyError(project, "attach失败");
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
