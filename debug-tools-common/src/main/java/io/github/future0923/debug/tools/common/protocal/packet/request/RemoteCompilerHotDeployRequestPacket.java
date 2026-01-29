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

@Data
@EqualsAndHashCode(callSuper = true)
public final class RemoteCompilerHotDeployRequestPacket extends Packet {

    /**
     * 目标 ClassLoader identity
     */
    private String identity;

    /**
     * 文件路径 -> 源码内容
     */
    private final Map<String, String> filePathContentMap = new HashMap<>();

    @Override
    public byte getCommand() {
        return Command.REMOTE_COMPILER_HOT_DEPLOY_REQUEST;
    }

    @Override
    public void binarySerialize(ByteBuf out) {
        writeString(out, identity);
        out.writeInt(filePathContentMap.size());
        for (Map.Entry<String, String> entry : filePathContentMap.entrySet()) {
            writeString(out, entry.getKey());
            writeString(out, entry.getValue());
        }
    }

    @Override
    public void binaryDeserialization(ByteBuf in) {
        this.identity = readString(in);
        int count = in.readInt();
        for (int i = 0; i < count; i++) {
            String path = readString(in);
            String content = readString(in);
            filePathContentMap.put(path, content);
        }
    }

    public void add(String filePath, String fileContent) {
        filePathContentMap.put(filePath, fileContent);
    }
}
