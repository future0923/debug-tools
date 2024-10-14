package io.github.future0923.debug.tools.server;

import io.github.future0923.debug.tools.base.config.AgentArgs;
import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.base.utils.DebugToolsIOUtils;
import io.github.future0923.debug.tools.base.utils.DebugToolsStringUtils;
import io.github.future0923.debug.tools.common.utils.DebugToolsClassUtils;
import io.github.future0923.debug.tools.common.utils.DebugToolsJvmUtils;
import io.github.future0923.debug.tools.server.config.ServerConfig;
import io.github.future0923.debug.tools.server.http.DebugToolsHttpServer;
import io.github.future0923.debug.tools.server.jvm.VmToolsUtils;
import io.github.future0923.debug.tools.server.scoket.DebugToolsSocketServer;
import io.github.future0923.debug.tools.server.utils.DebugToolsEnvUtils;
import lombok.Getter;

import java.lang.instrument.Instrumentation;

/**
 * @author future0923
 */
public class DebugToolsBootstrap {

    private static final Logger logger = Logger.getLogger(DebugToolsBootstrap.class);

    public static volatile DebugToolsBootstrap INSTANCE;

    public static volatile boolean started = false;

    private DebugToolsSocketServer socketServer;

    private DebugToolsHttpServer httpServer;

    @Getter
    private final Instrumentation instrumentation;

    public static final ServerConfig serverConfig = new ServerConfig();

    private Integer tcpPort;

    public Integer httpPort;

    private DebugToolsBootstrap(Instrumentation instrumentation, ClassLoader classloader) {
        this.instrumentation = instrumentation;
        DebugToolsClassUtils.setClassLoader(classloader);
        VmToolsUtils.init();
    }

    public static synchronized DebugToolsBootstrap getInstance(Instrumentation instrumentation, ClassLoader classloader) {
        if (INSTANCE == null) {
            INSTANCE = new DebugToolsBootstrap(instrumentation, classloader);
        }
        return INSTANCE;
    }

    public void start(String agentArgs) {
        AgentArgs parse = AgentArgs.parse(agentArgs);
        int tcpPort = Integer.parseInt(parse.getTcpPort());
        int httpPort = parse.getHttpPort() == null ? DebugToolsIOUtils.getAvailablePort(22222) : Integer.parseInt(parse.getHttpPort());
        serverConfig.setApplicationName(getApplicationName(parse));
        serverConfig.setTcpPort(tcpPort);
        serverConfig.setHttpPort(httpPort);
        startTcpServer(tcpPort);
        startHttpServer(httpPort);
        started = true;
    }

    private String getApplicationName(AgentArgs parse) {
        if (DebugToolsStringUtils.isNotBlank(parse.getApplicationName())) {
            return parse.getApplicationName();
        }
        try {
            String applicationName = (String) DebugToolsEnvUtils.getSpringConfig("spring.application.name");
            if (applicationName != null) {
                return applicationName;
            }
        } catch (Exception ignored) {
        }
        return DebugToolsJvmUtils.getApplicationName();
    }

    private void startTcpServer(int tcpPort) {
        if (!started || socketServer == null) {
            socketServer = new DebugToolsSocketServer();
            socketServer.start();
        } else if (this.tcpPort != null && tcpPort != this.tcpPort) {
            logger.error("The tcp two ports are inconsistent. Stopping port {}, preparing to start port {}", this.tcpPort, tcpPort);
            socketServer.close();
            socketServer = new DebugToolsSocketServer();
            socketServer.start();
        }
        this.tcpPort = tcpPort;
    }

    private void startHttpServer(int httpPort) {
        if (!started || httpServer == null) {
            httpServer = new DebugToolsHttpServer(httpPort);
            httpServer.start();
        } else if (this.httpPort != null && httpPort != this.httpPort) {
            logger.error("The http two ports are inconsistent. Stopping port {}, preparing to start port {}", this.httpPort, httpPort);
            httpServer.close();
            httpServer = new DebugToolsHttpServer(httpPort);
            httpServer.start();
        }
        this.httpPort = httpPort;
    }

    public void stop() {
        if (socketServer != null) {
            socketServer.close();
            socketServer = null;
        }
        if(httpServer != null) {
            httpServer.close();
            httpServer = null;
        }
        started = false;
        logger.info("stop successful");
    }
}
