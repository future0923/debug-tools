package io.github.future0923.debug.power.server.handler;

import io.github.future0923.debug.power.base.logging.Logger;
import io.github.future0923.debug.power.common.handler.BasePacketHandler;
import io.github.future0923.debug.power.common.protocal.packet.request.HeartBeatRequestPacket;
import io.github.future0923.debug.power.common.protocal.packet.response.HeartBeatResponsePacket;

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
        logger.info("收到心跳请求{}", packet);
        writeAndFlush(outputStream, new HeartBeatResponsePacket());
    }
}
