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
