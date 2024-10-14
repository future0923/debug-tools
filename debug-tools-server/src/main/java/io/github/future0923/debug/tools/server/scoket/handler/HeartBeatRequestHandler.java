package io.github.future0923.debug.tools.server.scoket.handler;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.common.handler.BasePacketHandler;
import io.github.future0923.debug.tools.common.protocal.packet.request.HeartBeatRequestPacket;
import io.github.future0923.debug.tools.common.protocal.packet.response.HeartBeatResponsePacket;

import java.io.OutputStream;

/**
 * 心跳请求处理器
 *
 * @author future0923
 */
public class HeartBeatRequestHandler extends BasePacketHandler<HeartBeatRequestPacket> {

    private static final Logger logger = Logger.getLogger(HeartBeatRequestHandler.class);

    public static final HeartBeatRequestHandler INSTANCE = new HeartBeatRequestHandler();

    private HeartBeatRequestHandler() {
    }

    @Override
    public void handle(OutputStream outputStream, HeartBeatRequestPacket packet) throws Exception {
        logger.debug("收到心跳请求{}", packet);
        writeAndFlush(outputStream, new HeartBeatResponsePacket());
    }
}
