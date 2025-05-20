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
package io.github.future0923.debug.tools.server.thread;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.common.handler.PacketHandleService;
import io.github.future0923.debug.tools.common.protocal.buffer.ByteBuf;
import io.github.future0923.debug.tools.common.protocal.packet.Packet;
import io.github.future0923.debug.tools.common.protocal.packet.PacketCodec;
import io.github.future0923.debug.tools.common.protocal.packet.response.HeartBeatResponsePacket;
import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;

/**
 * @author future0923
 */
public class ClientHandleThread extends Thread {

    private static final Logger logger = Logger.getLogger(ClientHandleThread.class);

    @Getter
    private final Socket socket;

    private InputStream inputStream;

    private OutputStream outputStream;

    private final Map<ClientHandleThread, Long> lastUpdateTime2Thread;

    private final PacketHandleService packetHandleService;

    private volatile boolean isClosed = false;

    public void setClosed(boolean closed) {
        isClosed = closed;
    }

    public ClientHandleThread(Socket socket, Map<ClientHandleThread, Long> lastUpdateTime2Thread, PacketHandleService packetHandleService) {
        setDaemon(true);
        setName("DebugTools-ClientHandle-Thread-" + socket.getPort());
        this.socket = socket;
        this.lastUpdateTime2Thread = lastUpdateTime2Thread;
        this.packetHandleService = packetHandleService;
        try {
            this.inputStream = socket.getInputStream();
            this.outputStream = socket.getOutputStream();
        } catch (IOException e) {
            logger.error("create SocketHandleThread happen error ", e);
        }
    }

    @Override
    public void run() {
        try {
            while(!isClosed) {
                try {
                    Packet packet = PacketCodec.INSTANCE.getPacket(inputStream, socket);
                    if (packet != null) {
                        refresh();
                        if (!socket.isClosed()) {
                            packetHandleService.handle(outputStream, packet);
                        } else {
                            this.lastUpdateTime2Thread.remove(this);
                        }
                    } else {
                        boolean isConn = touch(socket.getOutputStream());
                        if (!isConn) {
                            throw new RuntimeException(socket + " close !");
                        }
                    }
                } catch (Exception e) {
                    this.lastUpdateTime2Thread.remove(this);
                    logger.warning("remote client close socket:{} , error:{}", socket, e);
                    return;
                }
            }
        } finally {
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

        }
    }

    private void refresh() {
        this.lastUpdateTime2Thread.put(this, System.currentTimeMillis());
    }

    private boolean touch(OutputStream outputStream) {
        try {
            HeartBeatResponsePacket heartBeatResponsePacket = new HeartBeatResponsePacket();
            ByteBuf byteBuf = PacketCodec.INSTANCE.encode(heartBeatResponsePacket);
            outputStream.write(byteBuf.toByteArray());
            outputStream.flush();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
