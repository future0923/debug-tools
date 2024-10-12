package io.github.future0923.debug.power.common.handler;

import io.github.future0923.debug.power.base.logging.Logger;
import io.github.future0923.debug.power.common.protocal.packet.Packet;

import java.io.OutputStream;

/**
 * @author future0923
 */
public abstract class BasePacketHandler<T extends Packet> implements PacketHandler<T> {

    private static final Logger logger = Logger.getLogger(BasePacketHandler.class);

    public static void writeAndFlush(OutputStream outputStream, Packet packet) throws Exception {
        packet.writeAndFlush(outputStream);
    }

    public static void writeAndFlushNotException(OutputStream outputStream, Packet packet) {
        try {
            writeAndFlush(outputStream, packet);
        } catch (Exception e) {
            logger.error("{} write and flush error", e, packet.getClass().getSimpleName());
        }
    }
}
