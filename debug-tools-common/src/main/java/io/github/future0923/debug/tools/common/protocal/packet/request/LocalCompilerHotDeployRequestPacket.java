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
import io.github.future0923.debug.tools.common.protocal.buffer.ByteBuf;
import io.github.future0923.debug.tools.common.protocal.packet.Packet;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author future0923
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LocalCompilerHotDeployRequestPacket extends Packet {

    private Map<String, byte[]> filePathByteCodeMap = new HashMap<>();

    private static final String CLASS_SEPARATOR = ";;";

    private static final String CLASS_INFO_SEPARATOR = "::";

    /**
     * 类加载器
     */
    private String identity;

    @Override
    public Byte getCommand() {
        return Command.LOCAL_COMPILER_HOT_DEPLOY_REQUEST;
    }

    @Override
    public byte[] binarySerialize() {
        ByteBuf byteBuf = new ByteBuf();
        byte[] identityInfo = identity.getBytes(StandardCharsets.UTF_8);
        byteBuf.writeInt(identityInfo.length);
        byteBuf.writeBytes(identityInfo);
        StringBuilder fileHeaderInfo = new StringBuilder();
        List<byte[]> fileContent = new ArrayList<>();
        filePathByteCodeMap.forEach((filePath, byteCode) -> {
            fileHeaderInfo.append(filePath).append(CLASS_INFO_SEPARATOR).append(byteCode.length).append(CLASS_SEPARATOR);
            fileContent.add(byteCode);
        });
        byte[] headerInfo = fileHeaderInfo.toString().getBytes(StandardCharsets.UTF_8);
        byteBuf.writeInt(headerInfo.length);
        byteBuf.writeBytes(headerInfo);
        fileContent.forEach(byteBuf::writeBytes);
        return byteBuf.toByteArray();
    }

    @Override
    public void binaryDeserialization(byte[] bytes) {
        ByteBuf byteBuf = ByteBuf.wrap(bytes);
        int identityLength = byteBuf.readInt();
        byte[] identityByte = new byte[identityLength];
        byteBuf.readBytes(identityByte);
        identity = new String(identityByte, StandardCharsets.UTF_8);
        int headerLength = byteBuf.readInt();
        byte[] headerByte = new byte[headerLength];
        byteBuf.readBytes(headerByte);
        String headerInfo = new String(headerByte, StandardCharsets.UTF_8);
        String[] split = headerInfo.split(CLASS_SEPARATOR);
        for (String item : split) {
            String[] split1 = item.split(CLASS_INFO_SEPARATOR);
            if (split1.length != 2) {
                continue;
            }
            String filePath = split1[0];
            int fileLength = Integer.parseInt(split1[1]);
            byte[] fileByteCode = new byte[fileLength];
            byteBuf.readBytes(fileByteCode);
            filePathByteCodeMap.put(filePath, fileByteCode);
        }
    }

    public void add(String fileName, byte[] fileByteCode) {
        filePathByteCodeMap.put(fileName, fileByteCode);
    }
}
