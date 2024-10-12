package io.github.future0923.debug.power.idea.client;

import cn.hutool.core.util.ObjectUtil;
import com.intellij.openapi.project.Project;
import io.github.future0923.debug.power.base.utils.DebugPowerIOUtils;
import io.github.future0923.debug.power.client.DebugPowerSocketClient;
import io.github.future0923.debug.power.client.config.ClientConfig;
import io.github.future0923.debug.power.common.exception.SocketCloseException;
import io.github.future0923.debug.power.common.protocal.packet.Packet;
import io.github.future0923.debug.power.idea.client.socket.IdeaPacketHandleService;
import lombok.Data;

import java.io.IOException;
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

    private static final int INIT_TCP_PORT = 12345;

    private static final SortedSet<Integer> USE_TCP_PORT = new TreeSet<>();

    private static final Map<String, Integer> APPLICATION_TCP_PORT_MAPPING = new HashMap<>();

    private static final int INIT_HTTP_PORT = 22222;

    private static final SortedSet<Integer> USE_HTTP_PORT = new TreeSet<>();

    private static final Map<String, Integer> APPLICATION_HTTP_PORT_MAPPING = new HashMap<>();

    public static synchronized int getTcpPort(String applicationName) {
        return APPLICATION_TCP_PORT_MAPPING.computeIfAbsent(applicationName, (key) -> {
            Integer maxPort = ObjectUtil.defaultIfNull(USE_TCP_PORT.isEmpty() ? null : USE_TCP_PORT.last(), INIT_TCP_PORT);
            int availablePort = DebugPowerIOUtils.getAvailablePort(maxPort, 10);
            USE_TCP_PORT.add(availablePort);
            return availablePort;
        });
    }

    public static synchronized Integer getHttpPort(String applicationName) {
        return APPLICATION_HTTP_PORT_MAPPING.computeIfAbsent(applicationName, (key) -> {
            Integer maxPort = ObjectUtil.defaultIfNull(USE_HTTP_PORT.isEmpty() ? null : USE_HTTP_PORT.last(), INIT_HTTP_PORT);
            int availablePort = DebugPowerIOUtils.getAvailablePort(maxPort, 10);
            USE_HTTP_PORT.add(availablePort);
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

    public static void close(Project project) {
        getInfo(project).getClient().disconnect();
        Info remove = PROJECT_MAPPING.remove(project);
        APPLICATION_MAPPING.remove(remove.getApplicationName());
    }

    public static void send(Project project, Packet packet) throws SocketCloseException, IOException {
        getInfo(project).getClient().getHolder().send(packet);
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
