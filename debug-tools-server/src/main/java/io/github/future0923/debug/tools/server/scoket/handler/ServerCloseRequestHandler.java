package io.github.future0923.debug.tools.server.scoket.handler;

import io.github.future0923.debug.tools.common.handler.BasePacketHandler;
import io.github.future0923.debug.tools.common.protocal.packet.request.ServerCloseRequestPacket;
import io.github.future0923.debug.tools.server.DebugToolsBootstrap;

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
        DebugToolsBootstrap.INSTANCE.stop();
    }
}
