/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.future0923.debug.tools.client.holder;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.client.config.ClientConfig;
import io.github.future0923.debug.tools.client.thread.HeartBeatRequestThread;
import io.github.future0923.debug.tools.client.thread.ServerHandleThread;
import io.github.future0923.debug.tools.common.exception.SocketCloseException;
import io.github.future0923.debug.tools.common.handler.PacketHandleService;
import io.github.future0923.debug.tools.common.protocal.packet.Packet;
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
