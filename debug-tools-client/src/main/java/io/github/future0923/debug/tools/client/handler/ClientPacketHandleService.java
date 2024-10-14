package io.github.future0923.debug.tools.client.handler;

import io.github.future0923.debug.tools.common.handler.PacketHandleService;
import io.github.future0923.debug.tools.common.protocal.packet.response.HeartBeatResponsePacket;

/**
 * @author future0923
 */
public class ClientPacketHandleService extends PacketHandleService {

    public ClientPacketHandleService() {
        register(HeartBeatResponsePacket.class, HeartBeatResponseHandler.INSTANCE);
    }
}
