package io.github.future0923.debug.power.idea.client;

import cn.hutool.core.util.ObjectUtil;
import com.intellij.openapi.project.Project;
import io.github.future0923.debug.power.base.utils.DebugPowerIOUtils;
import io.github.future0923.debug.power.client.DebugPowerSocketClient;
import io.github.future0923.debug.power.client.config.ClientConfig;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author future0923
 */
public class ApplicationProjectHolder {

    private static final Map<String, Info> APPLICATION_MAPPING = new ConcurrentHashMap<>();

    private static final Map<Project, Info> PROJECT_MAPPING = new ConcurrentHashMap<>();

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

    public static Info setProject(String applicationName, Project project, String pid, String host, int port) {
        Info info = PROJECT_MAPPING.get(project);
        if (info != null && info.getApplicationName().equals(applicationName)) {
            return info;
        }
        if (info != null && info.getClient() != null) {
            info.getClient().disconnect();
        }
        if (info != null && info.getApplicationName() != null) {
            APPLICATION_MAPPING.remove(info.getApplicationName());
        }
        Info clientInfo = new Info();
        clientInfo.setApplicationName(applicationName);
        clientInfo.setProject(project);
        clientInfo.setPid(pid);
        clientInfo.setHost(host);
        clientInfo.setPort(port);
        ClientConfig config = new ClientConfig();
        config.setHost(host);
        config.setPort(port);
        config.setHeartbeatInterval(5);
        clientInfo.setClient(new DebugPowerSocketClient(config, IdeaPacketHandleService.INSTANCE));
        PROJECT_MAPPING.put(project, clientInfo);
        APPLICATION_MAPPING.put(applicationName, clientInfo);
        return clientInfo;
    }

    public static Info getInfo(String applicationName) {
        return APPLICATION_MAPPING.get(applicationName);
    }

    public static Info getInfo(Project project) {
        return PROJECT_MAPPING.get(project);
    }

    @Data
    public static class Info {

        private String applicationName;

        private Project project;

        private String pid;

        private String host;

        private int port;

        private DebugPowerSocketClient client;

    }
}
