package io.github.future0923.debug.tools.common.protocal.packet.request;

import io.github.future0923.debug.tools.common.protocal.Command;
import io.github.future0923.debug.tools.common.protocal.packet.Packet;

/**
 * @author future0923
 */
public class ServerCloseRequestPacket extends Packet {
    @Override
    public Byte getCommand() {
        return Command.SERVER_CLOSE_REQUEST;
    }

    @Override
    public byte[] binarySerialize() {
        return new byte[0];
    }

    @Override
    public void binaryDeserialization(byte[] bytes) {

    }
}
