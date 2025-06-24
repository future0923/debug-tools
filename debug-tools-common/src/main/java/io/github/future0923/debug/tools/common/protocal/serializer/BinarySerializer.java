/*
 * Copyright (C) 2024-2025 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package io.github.future0923.debug.tools.common.protocal.serializer;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.common.protocal.packet.Packet;

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
