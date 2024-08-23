package io.github.future0923.debug.power.server.scoket;

import io.github.future0923.debug.power.server.thread.ClientAcceptThread;
import io.github.future0923.debug.power.server.thread.SessionCheckThread;
import io.github.future0923.debug.power.server.thread.SocketServerHolder;

import java.util.concurrent.CountDownLatch;

/**
 * @author future0923
 */
public class DebugPowerSocketServer {

    public final ClientAcceptThread clientAcceptThread;

    private final SessionCheckThread sessionCheckThread;

    private final CountDownLatch countDownLatch = new CountDownLatch(1);

    public DebugPowerSocketServer() {
        clientAcceptThread = new ClientAcceptThread(countDownLatch);
        SocketServerHolder.setClientAcceptThread(clientAcceptThread);
        sessionCheckThread = new SessionCheckThread(clientAcceptThread.getLastUpdateTime2Thread());
        SocketServerHolder.setSessionCheckThread(sessionCheckThread);
    }

    public void start() {
        clientAcceptThread.start();
        sessionCheckThread.start();
        try {
            countDownLatch.await();
        } catch (InterruptedException ignored) {
        }
    }

    public void close() {
        clientAcceptThread.close();
        sessionCheckThread.interrupt();
    }
}
