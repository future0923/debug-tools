package io.github.future0923.debug.power.server;

import io.github.future0923.debug.power.base.config.AgentArgs;
import io.github.future0923.debug.power.base.config.AgentConfig;
import io.github.future0923.debug.power.server.config.ServerConfig;
import io.github.future0923.debug.power.server.jvm.VmToolsUtils;

import java.lang.instrument.Instrumentation;

/**
 * @author future0923
 */
public class DebugPowerBootstrap {

    private static DebugPowerBootstrap debugBootstrap;

    private final DebugPowerSocketServer socketServer;

    private DebugPowerBootstrap(String agentArgs, Instrumentation instrumentation) {
        VmToolsUtils.init();
        ServerConfig serverConfig = new ServerConfig();
        AgentArgs parse = AgentArgs.parse(agentArgs);
        serverConfig.setPort(Integer.parseInt(parse.getListenPort()));
        this.socketServer = new DebugPowerSocketServer(serverConfig);
    }

    public static synchronized DebugPowerBootstrap getInstance(String agentArgs, Instrumentation instrumentation) {
        if (debugBootstrap == null) {
            debugBootstrap = new DebugPowerBootstrap(agentArgs, instrumentation);
        }
        return debugBootstrap;
    }

    public void start() {
        socketServer.start();
    }
}
