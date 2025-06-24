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

    public boolean isSuccess() {
        return resultFlag == SUCCESS;
    }

    public void writeAndFlush(OutputStream outputStream) throws IOException {
        ByteBuf byteBuf = PacketCodec.INSTANCE.encode(this);
        outputStream.write(byteBuf.toByteArray());
        outputStream.flush();
    }
}
