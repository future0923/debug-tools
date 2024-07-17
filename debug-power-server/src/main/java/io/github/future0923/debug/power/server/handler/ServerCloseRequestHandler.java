package io.github.future0923.debug.power.server.handler;

import io.github.future0923.debug.power.base.logging.Logger;
import io.github.future0923.debug.power.common.handler.BasePacketHandler;
import io.github.future0923.debug.power.common.protocal.packet.request.ServerCloseRequestPacket;
import io.github.future0923.debug.power.server.thread.ClientAcceptThread;
import io.github.future0923.debug.power.server.thread.SocketServerHolder;

import java.io.OutputStream;

/**
 * @author future0923
 */
public class ServerCloseRequestHandler extends BasePacketHandler<ServerCloseRequestPacket> {

    private static final Logger logger = Logger.getLogger(ServerCloseRequestHandler.class);

    public static final ServerCloseRequestHandler INSTANCE = new ServerCloseRequestHandler();

    private ServerCloseRequestHandler() {
    }

    @Override
    public void handle(OutputStream outputStream, ServerCloseRequestPacket packet) throws Exception {
        SocketServerHolder.getClientAcceptThread().close();
    }
}
