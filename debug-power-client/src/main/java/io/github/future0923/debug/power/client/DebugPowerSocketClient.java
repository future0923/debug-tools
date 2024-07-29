package io.github.future0923.debug.power.client;

import io.github.future0923.debug.power.base.logging.Logger;
import io.github.future0923.debug.power.client.config.ClientConfig;
import io.github.future0923.debug.power.client.handler.ClientPacketHandleService;
import io.github.future0923.debug.power.client.holder.ClientSocketHolder;
import io.github.future0923.debug.power.client.thread.HeartBeatRequestThread;
import io.github.future0923.debug.power.common.handler.PacketHandleService;
import lombok.Getter;

import java.io.IOException;

/**
 * @author future0923
 */
@Getter
public class DebugPowerSocketClient {

    private static final Logger logger = Logger.getLogger(DebugPowerSocketClient.class);

    private final ClientSocketHolder holder;

    private final ClientConfig config;

    public DebugPowerSocketClient() {
        this(new ClientConfig(), new ClientPacketHandleService());
    }

    public DebugPowerSocketClient(ClientConfig config, PacketHandleService packetHandleService) {
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
