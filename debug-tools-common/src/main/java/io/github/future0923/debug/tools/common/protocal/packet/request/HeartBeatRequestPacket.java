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
package io.github.future0923.debug.tools.common.protocal.packet.request;

import io.github.future0923.debug.tools.common.protocal.Command;
import io.github.future0923.debug.tools.common.protocal.packet.Packet;
import io.netty.buffer.ByteBuf;

/**
 * 心跳请求：无 payload
 */
public final class HeartBeatRequestPacket extends Packet {

    public static final HeartBeatRequestPacket INSTANCE = new HeartBeatRequestPacket();

    @Override
    public byte getCommand() {
        return Command.HEARTBEAT_REQUEST;
    }

    /**
     * 无 body，不写任何字节
     */
    @Override
    public void binarySerialize(ByteBuf out) {
        // do nothing
    }

    /**
     * 无 body，不读任何字节
     */
    @Override
    public void binaryDeserialization(ByteBuf in) {
        // do nothing
    }
}
