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
package io.github.future0923.debug.tools.client;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.client.config.ClientConfig;
import io.github.future0923.debug.tools.client.handler.ClientPacketHandleService;
import io.github.future0923.debug.tools.client.holder.ClientSocketHolder;
import io.github.future0923.debug.tools.common.handler.PacketHandleService;
import lombok.Getter;

import java.io.IOException;

/**
 * @author future0923
 */
@Getter
public class DebugToolsSocketClient {

    private static final Logger logger = Logger.getLogger(DebugToolsSocketClient.class);

    private final ClientSocketHolder holder;

    private final ClientConfig config;

    public DebugToolsSocketClient() {
        this(new ClientConfig(), new ClientPacketHandleService());
    }

    public DebugToolsSocketClient(ClientConfig config, PacketHandleService packetHandleService) {
        this.config = config;
        this.holder = new ClientSocketHolder(config, packetHandleService);
    }

    public void start() throws IOException {
        holder.connect();
        holder.sendHeartBeat();
    }

    public void connect() throws IOException {
        holder.connect();
    }

    public void disconnect() {
        holder.close();
    }

    public void reconnect() throws Exception {
        holder.reconnect();
    }

    public boolean isClosed() {
        return holder.isClosed();
    }

    public boolean isClosedNow() {
        return holder.isClosedNow();
    }
}
