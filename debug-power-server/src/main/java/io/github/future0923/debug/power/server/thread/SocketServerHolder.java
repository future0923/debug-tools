package io.github.future0923.debug.power.server.thread;

import lombok.Getter;

/**
 * @author future0923
 */
public class SocketServerHolder {

    @Getter
    public static ClientAcceptThread clientAcceptThread;

    public static void setClientAcceptThread(ClientAcceptThread clientAcceptThread) {
        SocketServerHolder.clientAcceptThread = clientAcceptThread;
    }
}
