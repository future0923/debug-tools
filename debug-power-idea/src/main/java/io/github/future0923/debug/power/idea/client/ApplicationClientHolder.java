package io.github.future0923.debug.power.idea.client;

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

    public static DebugPowerSocketClient CLIENT;

    public static void set(String pid, String applicationName, String host, int port) {
        ApplicationClientHolder.PID = pid;
        ApplicationClientHolder.APPLICATION_NAME = applicationName;
        ApplicationClientHolder.HOST = host;
        ApplicationClientHolder.PORT = port;
        ClientConfig config = new ClientConfig();
        config.setHost(host);
        config.setPort(port);
        config.setHeartbeatInterval(10);
        ApplicationClientHolder.CLIENT = new DebugPowerSocketClient(config, IdeaPacketHandleService.INSTANCE);
    }

}
