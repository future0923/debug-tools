package io.github.future0923.debug.power.common.protocal.serializer;

import io.github.future0923.debug.power.common.protocal.packet.Packet;

/**
 * @author future0923
 */
public interface Serializer {

    BinarySerializer DEFAULT = new BinarySerializer();

    byte getSerializerAlgorithm();

    byte[] serialize(Packet packet);

    void deserialize(Packet packet, byte[] bytes);
}
