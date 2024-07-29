package io.github.future0923.debug.power.server.thread;

import lombok.Getter;

/**
 * @author future0923
 */
public class SocketServerHolder {

    @Getter
    private static ClientAcceptThread clientAcceptThread;

    @Getter
    private static SessionCheckThread sessionCheckThread;

    public static void setClientAcceptThread(ClientAcceptThread clientAcceptThread) {
        SocketServerHolder.clientAcceptThread = clientAcceptThread;
    }

    public static void setSessionCheckThread(SessionCheckThread sessionCheckThread) {
        SocketServerHolder.sessionCheckThread = sessionCheckThread;
    }
}
