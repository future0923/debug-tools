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
}
