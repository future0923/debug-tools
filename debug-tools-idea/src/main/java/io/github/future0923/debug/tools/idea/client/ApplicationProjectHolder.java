package io.github.future0923.debug.tools.idea.client;

import io.github.future0923.debug.tools.base.hutool.core.util.ObjectUtil;
import com.intellij.openapi.project.Project;
import io.github.future0923.debug.tools.base.utils.DebugToolsIOUtils;
import io.github.future0923.debug.tools.client.DebugToolsSocketClient;
import io.github.future0923.debug.tools.client.config.ClientConfig;
import io.github.future0923.debug.tools.common.exception.SocketCloseException;
import io.github.future0923.debug.tools.common.protocal.packet.Packet;
import io.github.future0923.debug.tools.idea.client.socket.IdeaPacketHandleService;
import lombok.Data;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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
            int availablePort = DebugToolsIOUtils.getAvailablePort(maxPort, 10);
            USE_TCP_PORT.add(availablePort);
            return availablePort;
        });
    }

    public static synchronized Integer getHttpPort(String applicationName) {
        return APPLICATION_HTTP_PORT_MAPPING.computeIfAbsent(applicationName, (key) -> {
            Integer maxPort = ObjectUtil.defaultIfNull(USE_HTTP_PORT.isEmpty() ? null : USE_HTTP_PORT.last(), INIT_HTTP_PORT);
            int availablePort = DebugToolsIOUtils.getAvailablePort(maxPort, 10);
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
        clientInfo.setClient(new DebugToolsSocketClient(config, IdeaPacketHandleService.INSTANCE));
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
        Optional.ofNullable(getInfo(project)).map(Info::getClient).ifPresent(DebugToolsSocketClient::disconnect);
        Info remove = PROJECT_MAPPING.remove(project);
        Optional.ofNullable(remove).map(Info::getApplicationName).ifPresent(APPLICATION_MAPPING::remove);
    }

    public static void close(String applicationName) {
        Optional.ofNullable(getInfo(applicationName)).map(Info::getClient).ifPresent(DebugToolsSocketClient::disconnect);
        Info remove = APPLICATION_MAPPING.remove(applicationName);
        Optional.ofNullable(remove).map(Info::getProject).ifPresent(PROJECT_MAPPING::remove);
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

        private DebugToolsSocketClient client;

    }
}
