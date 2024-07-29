package io.github.future0923.debug.power.server;

import io.github.future0923.debug.power.server.config.ServerConfig;
import io.github.future0923.debug.power.server.thread.ClientAcceptThread;
import io.github.future0923.debug.power.server.thread.SessionCheckThread;
import io.github.future0923.debug.power.server.thread.SocketServerHolder;

/**
 * @author future0923
 */
public class DebugPowerSocketServer {

    public final ClientAcceptThread clientAcceptThread;

    private final SessionCheckThread sessionCheckThread;

    public static void main(String[] args) throws InterruptedException {
        new DebugPowerSocketServer().start();
        Thread.sleep(100000000L);
    }

    public DebugPowerSocketServer() {
        this(ServerConfig.DEFAULT);
    }

    public DebugPowerSocketServer(ServerConfig serverConfig) {
        clientAcceptThread = new ClientAcceptThread(serverConfig);
        SocketServerHolder.setClientAcceptThread(clientAcceptThread);
        sessionCheckThread = new SessionCheckThread(clientAcceptThread.getLastUpdateTime2Thread());
        SocketServerHolder.setSessionCheckThread(sessionCheckThread);
    }

    public void start() {
        clientAcceptThread.start();
        sessionCheckThread.start();
    }

    public void close() {
        clientAcceptThread.close();
        sessionCheckThread.interrupt();
    }
}
