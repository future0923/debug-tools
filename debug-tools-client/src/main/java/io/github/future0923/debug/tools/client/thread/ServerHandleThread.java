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
package io.github.future0923.debug.tools.client.thread;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.client.holder.ClientSocketHolder;
import io.github.future0923.debug.tools.common.handler.PacketHandleService;
import io.github.future0923.debug.tools.common.protocal.packet.Packet;
import io.github.future0923.debug.tools.common.protocal.packet.PacketCodec;

/**
 * @author future0923
 */
public class ServerHandleThread extends Thread {

    private static final Logger logger = Logger.getLogger(ServerHandleThread.class);

    private final ClientSocketHolder holder;

    private final PacketHandleService packetHandleService;

    public ServerHandleThread(ClientSocketHolder holder, PacketHandleService packetHandleService) {
        setDaemon(true);
        setName("DebugTools-ServerHandle-Thread-" + holder.getConfig().getPort());
        this.holder = holder;
        this.packetHandleService = packetHandleService;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            if (holder.isClosed()) {
                logger.warning("debug tools client disconnect the link, waiting to reconnect");
                break;
            }
            try {
                Packet packet = PacketCodec.INSTANCE.getPacket(holder.getInputStream());
                if (packet != null) {
                    packetHandleService.handle(holder.getOutputStream(), packet);
                }
            } catch (Exception e) {
                logger.error("socket io error :{} , error:{}", holder.getSocket(), e);
                break;
            }
        }
    }
}