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
package io.github.future0923.debug.tools.server.thread;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.common.handler.PacketHandleService;
import io.github.future0923.debug.tools.server.DebugToolsBootstrap;
import io.github.future0923.debug.tools.server.scoket.handler.ServerPacketHandleService;
import lombok.Getter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * @author future0923
 */
public class ClientAcceptThread extends Thread {

    private static final Logger logger = Logger.getLogger(ClientAcceptThread.class);

    @Getter
    private final Map<ClientHandleThread, Long> lastUpdateTime2Thread = new ConcurrentHashMap<>();

    private final PacketHandleService packetHandleService = new ServerPacketHandleService();

    private ServerSocket serverSocket;

    private final CountDownLatch countDownLatch;

    public ClientAcceptThread(CountDownLatch countDownLatch) {
        setName("DebugTools-ClientAccept-Thread");
        setDaemon(true);
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(DebugToolsBootstrap.serverConfig.getTcpPort());
            int bindPort = serverSocket.getLocalPort();
            logger.info("start server trans and bind port in {}", bindPort);
            countDownLatch.countDown();
            while (!Thread.currentThread().isInterrupted()) {
                Socket socket;
                try {
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    serverSocket.close();
                    return;
                }
                logger.info("get client conn start handle thread socket: {}", socket);
                ClientHandleThread socketHandleThread = new ClientHandleThread(socket, lastUpdateTime2Thread, packetHandleService);
                socketHandleThread.start();
                lastUpdateTime2Thread.put(socketHandleThread, System.currentTimeMillis());
            }
        } catch (Exception e) {
            logger.error("运行过程中发生异常，关闭对应链接:{}", e);
        }
    }

    public void close() {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException ignored) {

            }
        }
        this.interrupt();
        for (ClientHandleThread clientHandleThread : lastUpdateTime2Thread.keySet()) {
            clientHandleThread.interrupt();
        }
    }
}
