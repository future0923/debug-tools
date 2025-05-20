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
            fileHeaderInfo.append(filePath).append(":").append(byteCode.length).append(";");
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
        String[] split = headerInfo.split(";");
        for (String item : split) {
            String[] split1 = item.split(":");
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
