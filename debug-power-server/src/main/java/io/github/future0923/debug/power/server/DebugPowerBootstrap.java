package io.github.future0923.debug.power.server;

import io.github.future0923.debug.power.base.config.AgentArgs;
import io.github.future0923.debug.power.base.logging.Logger;
import io.github.future0923.debug.power.common.utils.DebugPowerClassUtils;
import io.github.future0923.debug.power.server.config.ServerConfig;
import io.github.future0923.debug.power.server.http.DebugPowerHttpServer;
import io.github.future0923.debug.power.server.jvm.VmToolsUtils;
import io.github.future0923.debug.power.server.scoket.DebugPowerSocketServer;
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

    @Getter
    private final Instrumentation instrumentation;

    public static final ServerConfig serverConfig = new ServerConfig();

    private Integer port;

    public static Integer httpPort;

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
        int listenPort = Integer.parseInt(parse.getListenPort());
        serverConfig.setApplicationName(parse.getApplicationName());
        serverConfig.setPort(listenPort);
        if (!started || socketServer == null) {
            socketServer = new DebugPowerSocketServer();
            socketServer.start();
        } else if (port != null && listenPort != port) {
            logger.error("The two ports are inconsistent. Stopping port {}, preparing to start port {}", port, listenPort);
            socketServer.close();
            socketServer = new DebugPowerSocketServer();
            socketServer.start();
        }
        this.port = listenPort;
        DebugPowerHttpServer httpServer = DebugPowerHttpServer.getInstance();
        httpServer.start();
        httpPort = httpServer.getListenPort();
        started = true;
    }

    public void stop() {
        if (socketServer != null) {
            socketServer.close();
            socketServer = null;
        }
        started = false;
        logger.info("stop successful");
    }
}
