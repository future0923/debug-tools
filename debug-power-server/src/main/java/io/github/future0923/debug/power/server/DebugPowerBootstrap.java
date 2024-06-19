package io.github.future0923.debug.power.server;

import io.github.future0923.debug.power.server.jvm.VmToolsUtils;

import java.lang.instrument.Instrumentation;

/**
 * @author future0923
 */
public class DebugPowerBootstrap {

    private static DebugPowerBootstrap debugBootstrap;

    private final DebugPowerSocketServer socketServer;

    private DebugPowerBootstrap(Instrumentation instrumentation) {
        VmToolsUtils.init();
        this.socketServer = new DebugPowerSocketServer(instrumentation);
    }

    public static synchronized DebugPowerBootstrap getInstance(Instrumentation instrumentation) {
        if (debugBootstrap == null) {
            debugBootstrap = new DebugPowerBootstrap(instrumentation);
        }
        return debugBootstrap;
    }

    public void start() {
        socketServer.start();
    }
}
