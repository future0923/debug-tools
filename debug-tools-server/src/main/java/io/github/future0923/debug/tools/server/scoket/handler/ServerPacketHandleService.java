package io.github.future0923.debug.tools.server.scoket.handler;

import io.github.future0923.debug.tools.common.handler.PacketHandleService;
import io.github.future0923.debug.tools.common.protocal.packet.request.ClearRunResultRequestPacket;
import io.github.future0923.debug.tools.common.protocal.packet.request.RemoteCompilerRequestPacket;
import io.github.future0923.debug.tools.common.protocal.packet.request.HeartBeatRequestPacket;
import io.github.future0923.debug.tools.common.protocal.packet.request.HotSwapRequestPacket;
import io.github.future0923.debug.tools.common.protocal.packet.request.RunGroovyScriptRequestPacket;
import io.github.future0923.debug.tools.common.protocal.packet.request.RunTargetMethodRequestPacket;
import io.github.future0923.debug.tools.common.protocal.packet.request.ServerCloseRequestPacket;

/**
 * 定义数据由哪个 PacketHandler 处理
 *
 * @author future0923
 */
public class ServerPacketHandleService extends PacketHandleService {

    public ServerPacketHandleService() {
        register(HeartBeatRequestPacket.class, HeartBeatRequestHandler.INSTANCE);
        register(ServerCloseRequestPacket.class, ServerCloseRequestHandler.INSTANCE);
        register(RunTargetMethodRequestPacket.class, RunTargetMethodRequestHandler.INSTANCE);
        register(ClearRunResultRequestPacket.class, ClearRunResultRequestHandler.INSTANCE);
        register(RunGroovyScriptRequestPacket.class, RunGroovyScriptRequestHandler.INSTANCE);
        register(HotSwapRequestPacket.class, HotSwapRequestHandler.INSTANCE);
        register(RemoteCompilerRequestPacket.class, RemoteCompilerRequestHandler.INSTANCE);
    }
}
