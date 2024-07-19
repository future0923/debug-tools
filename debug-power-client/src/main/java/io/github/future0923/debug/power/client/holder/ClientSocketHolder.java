package io.github.future0923.debug.power.client.holder;

import io.github.future0923.debug.power.base.logging.Logger;
import io.github.future0923.debug.power.client.config.ClientConfig;
import io.github.future0923.debug.power.client.thread.HeartBeatRequestThread;
import io.github.future0923.debug.power.client.thread.ServerHandleThread;
import io.github.future0923.debug.power.common.exception.SocketCloseException;
import io.github.future0923.debug.power.common.handler.PacketHandleService;
import io.github.future0923.debug.power.common.protocal.packet.Packet;
import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @author future0923
 */
public class ClientSocketHolder {

    private static final Logger logger = Logger.getLogger(ClientSocketHolder.class);

    public static ClientSocketHolder INSTANCE;

    public static String PID;

    public static String APPLICATION_NAME;

    public static String HOST;

    public static int PORT;

    @Getter
    private Socket socket;

    private volatile boolean closed = true;

    @Getter
    private InputStream inputStream;

    @Getter
    private OutputStream outputStream;

    private ServerHandleThread serverHandleThread;

    private HeartBeatRequestThread heartBeatRequestThread;

    private final PacketHandleService packetHandleService;

    private final ClientConfig config;

    public ClientSocketHolder(ClientConfig config, PacketHandleService packetHandleService) {
        this.config = config;
        this.packetHandleService = packetHandleService;
        INSTANCE = this;
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

    public boolean isClosed() {
        return socket == null || socket.isClosed() || closed;
    }

    public void connect() throws IOException {
        setSocket(new Socket(config.getHost(), config.getPort()));
        closed = false;
        logger.info("debug power client connect successful");
        serverHandleThread = new ServerHandleThread(this, packetHandleService);
        serverHandleThread.setDaemon(true);
        serverHandleThread.start();
    }

    public void reconnect() throws Exception {
        close();
        connect();
    }

    public void sendHeartBeat() {
        heartBeatRequestThread = new HeartBeatRequestThread(this, config.getHeartbeatInterval());
        heartBeatRequestThread.start();
    }

    public void send(Packet packet) throws SocketCloseException, IOException {
        if (!isClosed()) {
            packet.writeAndFlush(this.getOutputStream());
        } else {
            throw new SocketCloseException();
        }
    }

    public void close() {
        closeSocket();
        if (serverHandleThread != null) {
            serverHandleThread.interrupt();
        }
        if (heartBeatRequestThread != null) {
            heartBeatRequestThread.interrupt();
        }
    }

    public void closeSocket() {
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
        } catch (IOException ignored) {
        }
        closed = true;
    }
}
