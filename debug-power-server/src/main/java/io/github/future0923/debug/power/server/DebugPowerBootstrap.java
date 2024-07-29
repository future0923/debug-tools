package io.github.future0923.debug.power.server;

import cn.hutool.core.util.ObjectUtil;
import io.github.future0923.debug.power.base.config.AgentArgs;
import io.github.future0923.debug.power.base.logging.Logger;
import io.github.future0923.debug.power.server.config.ServerConfig;
import io.github.future0923.debug.power.server.jvm.VmToolsUtils;

import java.lang.instrument.Instrumentation;

/**
 * @author future0923
 */
public class DebugPowerBootstrap {

    private static final Logger logger = Logger.getLogger(DebugPowerBootstrap.class);

    private static DebugPowerBootstrap debugBootstrap;

    private DebugPowerSocketServer socketServer;

    private Integer port;

    private DebugPowerBootstrap(Instrumentation instrumentation) {
        VmToolsUtils.init();
    }

    public static synchronized DebugPowerBootstrap getInstance(Instrumentation instrumentation) {
        if (debugBootstrap == null) {
            debugBootstrap = new DebugPowerBootstrap(instrumentation);
        }
        return debugBootstrap;
    }

    public void start(String agentArgs) {
        ServerConfig serverConfig = new ServerConfig();
        AgentArgs parse = AgentArgs.parse(agentArgs);
        int listenPort = Integer.parseInt(parse.getListenPort());
        serverConfig.setPort(listenPort);
        if (socketServer == null) {
            socketServer = new DebugPowerSocketServer(serverConfig);
            socketServer.start();
        } else if (ObjectUtil.notEqual(listenPort, port)) {
            logger.error("The two ports are inconsistent. Stopping port {}, preparing to start port {}", port, listenPort);
            socketServer.close();
            socketServer = new DebugPowerSocketServer(serverConfig);
            socketServer.start();
        }
        port = listenPort;
    }
}
