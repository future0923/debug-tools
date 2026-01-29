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
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

/**
 * 【最终版】Resource Hot Deploy Request Packet
 * 协议结构：
 * [identityLen:int][identity:bytes]
 * [fileCount:int]
 * ├─ [pathLen:int][path:bytes]
 * ├─ [contentLen:int][content:bytes]
 */
@Data
@EqualsAndHashCode(callSuper = true)
public final class ResourceHotDeployRequestPacket extends Packet {

    /**
     * 目标 ClassLoader identity
     */
    private String identity;

    /**
     * 资源路径 -> 资源字节
     */
    private final Map<String, byte[]> filePathByteCodeMap = new HashMap<>();

    @Override
    public byte getCommand() {
        return Command.RESOURCE_HOT_DEPLOY_REQUEST;
    }

    @Override
    public void binarySerialize(ByteBuf out) {
        writeString(out, identity);
        out.writeInt(filePathByteCodeMap.size());
        for (Map.Entry<String, byte[]> entry : filePathByteCodeMap.entrySet()) {
            writeString(out, entry.getKey());
            byte[] bytes = entry.getValue();
            out.writeInt(bytes.length);
            out.writeBytes(bytes);
        }
    }

    @Override
    public void binaryDeserialization(ByteBuf in) {
        this.identity = readString(in);
        int fileCount = in.readInt();
        for (int i = 0; i < fileCount; i++) {
            String path = readString(in);
            int len = in.readInt();
            byte[] bytes = new byte[len];
            in.readBytes(bytes);
            filePathByteCodeMap.put(path, bytes);
        }
    }

    public void add(String filePath, byte[] fileBytes) {
        filePathByteCodeMap.put(filePath, fileBytes);
    }
}
