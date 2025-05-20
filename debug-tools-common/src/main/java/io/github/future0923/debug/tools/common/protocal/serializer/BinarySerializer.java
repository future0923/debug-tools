/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
