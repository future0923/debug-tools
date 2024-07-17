package io.github.future0923.debug.power.client;

import io.github.future0923.debug.power.base.logging.Logger;
import io.github.future0923.debug.power.client.config.ClientConfig;
import io.github.future0923.debug.power.client.handler.ClientPacketHandleService;
import io.github.future0923.debug.power.client.holder.ClientSocketHolder;
import io.github.future0923.debug.power.common.exception.SocketCloseException;
import io.github.future0923.debug.power.common.handler.PacketHandleService;
import io.github.future0923.debug.power.common.protocal.packet.request.ServerCloseRequestPacket;
import lombok.Getter;

import java.io.IOException;

/**
 * @author future0923
 */
@Getter
public class DebugPowerSocketClient {

    private static final Logger logger = Logger.getLogger(DebugPowerSocketClient.class);

    private final ClientSocketHolder holder;

    public DebugPowerSocketClient() {
        this(ClientConfig.DEFAULT, new ClientPacketHandleService());
    }

    public DebugPowerSocketClient(PacketHandleService packetHandleService) {
        this(ClientConfig.DEFAULT, packetHandleService);
    }

    public DebugPowerSocketClient(ClientConfig config, PacketHandleService packetHandleService) {
        holder = new ClientSocketHolder(config, packetHandleService);
    }

    public void connect() {
        holder.connect();
        //holder.sendHeartBeat();
    }

    public void disconnect() {
        holder.close();
    }

    public static void main(String[] args) throws Exception {
        DebugPowerSocketClient socketClient = new DebugPowerSocketClient();
        socketClient.connect();
        ServerCloseRequestPacket packet = new ServerCloseRequestPacket();
        ClientSocketHolder.INSTANCE.send(packet);
        Thread.sleep(1000000000);
    }
}
