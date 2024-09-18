package io.github.future0923.debug.power.server.scoket.handler;

import io.github.future0923.debug.power.common.handler.BasePacketHandler;
import io.github.future0923.debug.power.common.protocal.packet.request.ServerCloseRequestPacket;
import io.github.future0923.debug.power.server.DebugPowerBootstrap;

import java.io.OutputStream;

/**
 * @author future0923
 */
public class ServerCloseRequestHandler extends BasePacketHandler<ServerCloseRequestPacket> {

    public static final ServerCloseRequestHandler INSTANCE = new ServerCloseRequestHandler();

    private ServerCloseRequestHandler() {
    }

    @Override
    public void handle(OutputStream outputStream, ServerCloseRequestPacket packet) throws Exception {
        DebugPowerBootstrap.debugBootstrap.stop();
    }
}
