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

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;

import java.nio.charset.StandardCharsets;

@Setter
@Getter
public abstract class Packet {

    private byte version;

    private byte resultFlag = SUCCESS;

    public static final byte SUCCESS = 1;

    public static final byte FAIL = 0;

    public abstract byte getCommand();

    /* ====== 关键改造点 ====== */

    /**
     * 直接写入 ByteBuf（不返回 byte[]）
     */
    public abstract void binarySerialize(ByteBuf byteBuf);

    /**
     * 从 ByteBuf 反序列化（slice，不 copy）
     */
    public abstract void binaryDeserialization(ByteBuf byteBuf);

    public boolean isSuccess() {
        return resultFlag == SUCCESS;
    }

    public void writeString(ByteBuf out, String s) {
        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
    }

    public String readString(ByteBuf in) {
        int len = in.readInt();
        byte[] bytes = new byte[len];
        in.readBytes(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
