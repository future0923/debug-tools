package io.github.future0923.debug.power.client.handler;

import io.github.future0923.debug.power.common.handler.PacketHandleService;
import io.github.future0923.debug.power.common.protocal.packet.response.HeartBeatResponsePacket;

/**
 * @author future0923
 */
public class ClientPacketHandleService extends PacketHandleService {

    public ClientPacketHandleService() {
        register(HeartBeatResponsePacket.class, HeartBeatResponseHandler.INSTANCE);
    }
}
