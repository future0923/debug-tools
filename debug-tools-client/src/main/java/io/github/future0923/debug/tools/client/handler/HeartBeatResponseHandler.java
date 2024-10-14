package io.github.future0923.debug.tools.client.handler;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.common.handler.BasePacketHandler;
import io.github.future0923.debug.tools.common.protocal.packet.response.HeartBeatResponsePacket;

import java.io.OutputStream;

/**
 * 心跳请求处理器
 *
 * @author future0923
 */
public class HeartBeatResponseHandler extends BasePacketHandler<HeartBeatResponsePacket> {

    private static final Logger logger = Logger.getLogger(HeartBeatResponseHandler.class);

    public static final HeartBeatResponseHandler INSTANCE = new HeartBeatResponseHandler();

    private HeartBeatResponseHandler() {
    }

    @Override
    public void handle(OutputStream outputStream, HeartBeatResponsePacket packet) throws Exception {
        logger.debug("Received debug tools server HeartBeatResponsePacket");
    }
}
