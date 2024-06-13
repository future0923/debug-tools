package io.github.future0923.debug.power.client.holder;

import io.github.future0923.debug.power.base.logging.Logger;
import io.github.future0923.debug.power.client.thread.HeartBeatRequestThread;
import io.github.future0923.debug.power.client.thread.ServerHandleThread;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @author future0923
 */
public class ClientSocketHolder {

    private static final Logger logger = Logger.getLogger(ClientSocketHolder.class);

    private Socket socket;

    private InputStream inputStream;

    private OutputStream outputStream;

    private ServerHandleThread serverHandleThread;

    private HeartBeatRequestThread heartBeatRequestThread;

    public ClientSocketHolder() {

    }

    public ClientSocketHolder(Socket socket) {
        setSocket(socket);
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
        try {
            this.inputStream = socket.getInputStream();
            this.outputStream = socket.getOutputStream();
        } catch (IOException e) {
            logger.error("create ClientSocketHolder happen error ", e);
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public boolean isClosed() {
        return socket == null || socket.isClosed();
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public void connect() {
        try {
            setSocket(new Socket("127.0.0.1", 50889));
            logger.info("debug power client connect successful");
            if (serverHandleThread != null) {
                serverHandleThread.interrupt();
            }
            serverHandleThread = new ServerHandleThread(this);
            serverHandleThread.setDaemon(true);
            serverHandleThread.start();
        } catch (IOException e) {
            logger.error("HeartBeatRequest reconnect debug power server error, {} second", e);
        }
    }

    public void sendHeartBeat() {
        Long INTERVAL = 10L;
        heartBeatRequestThread = new HeartBeatRequestThread(this, INTERVAL);
        heartBeatRequestThread.start();
    }

    public void close() {
        try {
            if (this.outputStream != null) {
                this.outputStream.close();
            }
            if (this.inputStream != null) {
                this.inputStream.close();
            }
            if (this.socket != null) {
                this.socket.close();
            }
            if (serverHandleThread != null) {
                serverHandleThread.interrupt();
            }
            if (heartBeatRequestThread != null) {
                heartBeatRequestThread.interrupt();
            }
        } catch (IOException ignored) {
        }
    }
}
