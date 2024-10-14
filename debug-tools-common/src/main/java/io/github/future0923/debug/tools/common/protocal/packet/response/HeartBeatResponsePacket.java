package io.github.future0923.debug.tools.common.protocal.packet.response;

import io.github.future0923.debug.tools.common.protocal.Command;
import io.github.future0923.debug.tools.common.protocal.packet.Packet;

/**
 * @author future0923
 */
public class HeartBeatResponsePacket extends Packet {

    public HeartBeatResponsePacket() {
        this(SUCCESS);
    }

    public HeartBeatResponsePacket(byte resultFlag) {
        setResultFlag(resultFlag);
    }

    @Override
    public Byte getCommand() {
        return Command.HEARTBEAT_RESPONSE;
    }

    @Override
    public byte[] binarySerialize() {
        return new byte[0];
    }

    @Override
    public void binaryDeserialization(byte[] bytes) {

    }
}
