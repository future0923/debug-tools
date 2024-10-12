package io.github.future0923.debug.power.server;

import io.github.future0923.debug.power.base.config.AgentArgs;
import io.github.future0923.debug.power.base.logging.Logger;
import io.github.future0923.debug.power.base.utils.DebugPowerIOUtils;
import io.github.future0923.debug.power.base.utils.DebugPowerStringUtils;
import io.github.future0923.debug.power.common.utils.DebugPowerClassUtils;
import io.github.future0923.debug.power.common.utils.DebugPowerJvmUtils;
import io.github.future0923.debug.power.server.config.ServerConfig;
import io.github.future0923.debug.power.server.http.DebugPowerHttpServer;
import io.github.future0923.debug.power.server.jvm.VmToolsUtils;
import io.github.future0923.debug.power.server.scoket.DebugPowerSocketServer;
import io.github.future0923.debug.power.server.utils.DebugPowerEnvUtils;
import lombok.Getter;

import java.lang.instrument.Instrumentation;

/**
 * @author future0923
 */
public class DebugPowerBootstrap {

    private static final Logger logger = Logger.getLogger(DebugPowerBootstrap.class);

    public static volatile DebugPowerBootstrap INSTANCE;

    public static volatile boolean started = false;

    private DebugPowerSocketServer socketServer;

    private DebugPowerHttpServer httpServer;

    @Getter
    private final Instrumentation instrumentation;

    public static final ServerConfig serverConfig = new ServerConfig();

    private Integer tcpPort;

    public Integer httpPort;

    private DebugPowerBootstrap(Instrumentation instrumentation, ClassLoader classloader) {
        this.instrumentation = instrumentation;
        DebugPowerClassUtils.setClassLoader(classloader);
        VmToolsUtils.init();
    }

    public static synchronized DebugPowerBootstrap getInstance(Instrumentation instrumentation, ClassLoader classloader) {
        if (INSTANCE == null) {
            INSTANCE = new DebugPowerBootstrap(instrumentation, classloader);
        }
        return INSTANCE;
    }

    public void start(String agentArgs) {
        AgentArgs parse = AgentArgs.parse(agentArgs);
        int tcpPort = Integer.parseInt(parse.getTcpPort());
        int httpPort = parse.getHttpPort() == null ? DebugPowerIOUtils.getAvailablePort(22222) : Integer.parseInt(parse.getHttpPort());
        serverConfig.setApplicationName(getApplicationName(parse));
        serverConfig.setTcpPort(tcpPort);
        serverConfig.setHttpPort(httpPort);
        startTcpServer(tcpPort);
        startHttpServer(httpPort);
        started = true;
    }

    private String getApplicationName(AgentArgs parse) {
        if (DebugPowerStringUtils.isNotBlank(parse.getApplicationName())) {
            return parse.getApplicationName();
        }
        try {
            String applicationName = (String) DebugPowerEnvUtils.getSpringConfig("spring.application.name");
            if (applicationName != null) {
                return applicationName;
            }
        } catch (Exception ignored) {
        }
        return DebugPowerJvmUtils.getApplicationName();
    }

    private void startTcpServer(int tcpPort) {
        if (!started || socketServer == null) {
            socketServer = new DebugPowerSocketServer();
            socketServer.start();
        } else if (this.tcpPort != null && tcpPort != this.tcpPort) {
            logger.error("The tcp two ports are inconsistent. Stopping port {}, preparing to start port {}", this.tcpPort, tcpPort);
            socketServer.close();
            socketServer = new DebugPowerSocketServer();
            socketServer.start();
        }
        this.tcpPort = tcpPort;
    }

    private void startHttpServer(int httpPort) {
        if (!started || httpServer == null) {
            httpServer = new DebugPowerHttpServer(httpPort);
            httpServer.start();
        } else if (this.httpPort != null && httpPort != this.httpPort) {
            logger.error("The http two ports are inconsistent. Stopping port {}, preparing to start port {}", this.httpPort, httpPort);
            httpServer.close();
            httpServer = new DebugPowerHttpServer(httpPort);
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
