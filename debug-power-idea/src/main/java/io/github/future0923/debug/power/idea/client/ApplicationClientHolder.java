package io.github.future0923.debug.power.idea.client;

import cn.hutool.core.util.ObjectUtil;
import com.intellij.openapi.project.Project;
import io.github.future0923.debug.power.client.DebugPowerSocketClient;
import io.github.future0923.debug.power.client.config.ClientConfig;

/**
 * @author future0923
 */
public class ApplicationClientHolder {

    public static String PID;

    public static String APPLICATION_NAME;

    public static String HOST;

    public static int PORT;

    public static Project PROJECT;

    public static DebugPowerSocketClient CLIENT;

    public static void set(Project project, String pid, String applicationName, String host, int port) {
        if (ObjectUtil.equals(ApplicationClientHolder.APPLICATION_NAME, applicationName)) {
            return;
        }
        ApplicationClientHolder.PROJECT = project;
        ApplicationClientHolder.PID = pid;
        ApplicationClientHolder.APPLICATION_NAME = applicationName;
        ApplicationClientHolder.HOST = host;
        ApplicationClientHolder.PORT = port;
        ClientConfig config = new ClientConfig();
        config.setHost(host);
        config.setPort(port);
        config.setHeartbeatInterval(5);
        ApplicationClientHolder.CLIENT = new DebugPowerSocketClient(config, IdeaPacketHandleService.INSTANCE);
    }

}
