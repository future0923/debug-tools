package io.github.future0923.debug.power.common.handler;

import io.github.future0923.debug.power.common.protocal.packet.Packet;
import io.github.future0923.debug.power.common.utils.DebugPowerIOUtils;

import java.io.OutputStream;

/**
 * @author future0923
 */
public abstract class BasePacketHandler<T extends Packet> implements PacketHandler<T> {

    public static void writeAndFlush(OutputStream outputStream, Packet packet) throws Exception {
        DebugPowerIOUtils.writeAndFlush(outputStream, packet);
    }
}
