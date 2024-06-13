package io.github.future0923.debug.power.client;

import io.github.future0923.debug.power.base.logging.Logger;
import io.github.future0923.debug.power.client.holder.ClientSocketHolder;
import io.github.future0923.debug.power.client.thread.HeartBeatRequestThread;

/**
 * @author future0923
 */
public class SocketClient {

    private static final Logger logger = Logger.getLogger(SocketClient.class);

    private final ClientSocketHolder holder = new ClientSocketHolder();

    public SocketClient() {

    }

    public void start() {
        holder.connect();
        holder.sendHeartBeat();
    }

    public static void main(String[] args) throws InterruptedException {
        SocketClient socketClient = new SocketClient();
        socketClient.start();
        Thread.sleep(1000000000);
    }
}
