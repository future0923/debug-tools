package io.github.future0923.debug.power.idea.client;

import cn.hutool.core.util.ObjectUtil;
import com.intellij.openapi.project.Project;
import io.github.future0923.debug.power.base.utils.DebugPowerIOUtils;
import io.github.future0923.debug.power.client.DebugPowerSocketClient;
import io.github.future0923.debug.power.client.config.ClientConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

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

    private static final int INIT_PORT = 12345;

    private static final SortedSet<Integer> USE_PORT = new TreeSet<>();

    private static final Map<String, Integer> APPLICATION_PORT_MAPPING = new HashMap<>();

    public static synchronized int getPort(String applicationName) {
        return APPLICATION_PORT_MAPPING.computeIfAbsent(applicationName, (key) -> {
            Integer maxPort = ObjectUtil.defaultIfNull(USE_PORT.isEmpty() ? null : USE_PORT.last(), INIT_PORT);
            int availablePort = DebugPowerIOUtils.getAvailablePort(maxPort, 10);
            USE_PORT.add(availablePort);
            return availablePort;
        });
    }

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
        if (ApplicationClientHolder.CLIENT != null) {
            ApplicationClientHolder.CLIENT.disconnect();
        }
        ApplicationClientHolder.CLIENT = new DebugPowerSocketClient(config, IdeaPacketHandleService.INSTANCE);
    }

}
