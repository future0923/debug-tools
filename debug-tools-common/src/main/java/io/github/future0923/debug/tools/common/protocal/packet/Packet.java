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
package io.github.future0923.debug.tools.common.protocal.packet;

import io.github.future0923.debug.tools.common.protocal.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 需要加入到{@link PacketCodec}中
 *
 * @author future0923
 */
public abstract class Packet {

    private static final String EMPTY_BYTE = "\u0000";
    private static final String EMPTY_STRING = "";
    @Setter
    @Getter
    private byte version;
    private final byte[] ipBytes = new byte[15];
    @Setter
    @Getter
    private byte resultFlag = SUCCESS;
    public static final byte SUCCESS = 1;
    public static final byte FAIL = 0;

    public Packet() {
    }

    public abstract Byte getCommand();

    public abstract byte[] binarySerialize();

    public abstract void binaryDeserialization(byte[] bytes);

    public void setIpBytes(byte[] bytes) {
        System.arraycopy(bytes, 0, this.ipBytes, 0, bytes.length);
    }

    public byte[] getIpBytes() {
        return ipBytes;
    }

    public String getIp() {
        return (new String(this.ipBytes)).replaceAll(EMPTY_BYTE, EMPTY_STRING);
    }

    public boolean isSuccess() {
        return resultFlag == SUCCESS;
    }

    public void writeAndFlush(OutputStream outputStream) throws IOException {
        ByteBuf byteBuf = PacketCodec.INSTANCE.encode(this);
        outputStream.write(byteBuf.toByteArray());
        outputStream.flush();
    }
}
