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
package io.github.future0923.debug.tools.common.codec;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public final class PacketFrameDecoder {

    public static final int MAGIC_NUMBER = 20240508;

    private PacketFrameDecoder() {}

    public static LengthFieldBasedFrameDecoder newDefault() {
        // 你协议：magic(4)+ver(1)+ser(1)+cmd(1)+flag(1)+len(4)+body
        // len 的 offset = 4+1+1+1+1 = 8
        int maxFrame = 32 * 1024 * 1024; // 32MB，按业务可调
        return new LengthFieldBasedFrameDecoder(
                maxFrame,
                8,
                4,
                0,
                0
        );
    }
}
