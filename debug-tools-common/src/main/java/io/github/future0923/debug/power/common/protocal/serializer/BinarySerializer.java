package io.github.future0923.debug.power.common.protocal.serializer;

import io.github.future0923.debug.power.base.logging.Logger;
import io.github.future0923.debug.power.common.protocal.packet.Packet;

/**
 * @author future0923
 */
public class BinarySerializer implements Serializer {

    private static final Logger logger = Logger.getLogger(BinarySerializer.class);

    @Override
    public byte getSerializerAlgorithm() {
        return SerializerAlgorithm.BINARY;
    }

    @Override
    public byte[] serialize(Packet packet) {
        return packet.binarySerialize();
    }

    @Override
    public void deserialize(Packet packet, byte[] bytes) {
        packet.binaryDeserialization(bytes);
    }
}
