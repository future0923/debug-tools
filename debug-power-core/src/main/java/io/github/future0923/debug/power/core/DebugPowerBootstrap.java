package io.github.future0923.debug.power.core;

import io.github.future0923.debug.power.server.SocketServer;

import java.lang.instrument.Instrumentation;

/**
 * @author future0923
 */
public class DebugPowerBootstrap {

    private static DebugPowerBootstrap debugBootstrap;

    private final SocketServer socketServer;

    private DebugPowerBootstrap(Instrumentation instrumentation) {
        this.socketServer = new SocketServer(instrumentation);
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
