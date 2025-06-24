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
