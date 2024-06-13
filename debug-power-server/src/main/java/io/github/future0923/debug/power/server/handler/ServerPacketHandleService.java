package io.github.future0923.debug.power.server.handler;

import io.github.future0923.debug.power.common.handler.PacketHandleService;
import io.github.future0923.debug.power.common.protocal.packet.request.HeartBeatRequestPacket;
import io.github.future0923.debug.power.common.protocal.packet.request.RunTargetMethodRequestPacket;

/**
 * 定义数据由哪个 PacketHandler 处理
 *
 * @author future0923
 */
public class ServerPacketHandleService extends PacketHandleService {

    public ServerPacketHandleService() {
        register(HeartBeatRequestPacket.class, HeartBeatRequestHandler.INSTANCE);
        register(RunTargetMethodRequestPacket.class, RunTargetMethodRequestHandler.INSTANCE);
    }
}
