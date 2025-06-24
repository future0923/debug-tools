/*
 * Copyright (C) 2024-2025 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package io.github.future0923.debug.tools.client.holder;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.client.config.ClientConfig;
import io.github.future0923.debug.tools.client.thread.HeartBeatRequestThread;
import io.github.future0923.debug.tools.client.thread.ServerHandleThread;
import io.github.future0923.debug.tools.common.exception.SocketCloseException;
import io.github.future0923.debug.tools.common.handler.PacketHandleService;
import io.github.future0923.debug.tools.common.protocal.packet.Packet;
import io.github.future0923.debug.tools.common.protocal.packet.request.HeartBeatRequestPacket;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @author future0923
 */
public class ClientSocketHolder {

    private static final Logger logger = Logger.getLogger(ClientSocketHolder.class);

    @Getter
    private Socket socket;

    private volatile boolean closed = true;

    @Setter
    @Getter
    private volatile int retry;

    public static final int INIT = 1;

    public static final int RETRYING = 1 << 1;

    public static final int FAIL = 1 << 2;

    @Getter
    private InputStream inputStream;

    @Getter
    private OutputStream outputStream;

    private ServerHandleThread serverHandleThread;

    private HeartBeatRequestThread heartBeatRequestThread;

    private final PacketHandleService packetHandleService;

    @Getter
    private final ClientConfig config;

    public ClientSocketHolder(ClientConfig config, PacketHandleService packetHandleService) {
        this.config = config;
        this.packetHandleService = packetHandleService;
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
        return socket == null || socket.isClosed() || closed || retry == FAIL;
    }

    public boolean isClosedNow() {
        boolean isClosed = socket == null || socket.isClosed() || closed || retry == FAIL;
        if (isClosed) {
            return true;
        }
        try {
            HeartBeatRequestPacket.INSTANCE.writeAndFlush(getOutputStream());
            return false;
        } catch (IOException e) {
            closeSocket();
            setRetry(ClientSocketHolder.FAIL);
            return true;
        }
    }

    public void connect() throws IOException {
        setSocket(new Socket(config.getHost(), config.getPort()));
        closed = false;
        logger.info("debug tools client connect successful");
        serverHandleThread = new ServerHandleThread(this, packetHandleService);
        serverHandleThread.setDaemon(true);
        serverHandleThread.start();
    }

    public void reconnect() throws Exception {
        close();
        connect();
        sendHeartBeat();
    }

    public void sendHeartBeat() {
        if (heartBeatRequestThread != null && heartBeatRequestThread.isAlive()) {
            heartBeatRequestThread.interrupt();
        }
        setRetry(INIT);
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
