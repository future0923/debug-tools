package io.github.future0923.debug.power.server;

import io.github.future0923.debug.power.server.thread.ClientAcceptThread;

import java.lang.instrument.Instrumentation;

/**
 * @author future0923
 */
public class DebugPowerSocketServer {

    public static void main(String[] args) throws InterruptedException {
        new DebugPowerSocketServer().start();
        Thread.sleep(100000000L);
    }

    public DebugPowerSocketServer() {

    }

    public DebugPowerSocketServer(Instrumentation instrumentation) {

    }

    public void start() {
        ClientAcceptThread thread = new ClientAcceptThread();
        thread.start();
    }
}
