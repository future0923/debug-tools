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
public class RemoteCompilerHotDeployRequestPacket extends Packet {

    private Map<String, String> filePathByteCodeMap = new HashMap<>();

    @Override
    public Byte getCommand() {
        return Command.REMOTE_COMPILER_HOT_DEPLOY_REQUEST;
    }

    @Override
    public byte[] binarySerialize() {
        StringBuilder fileHeaderInfo = new StringBuilder();
        List<String> fileContentList = new ArrayList<>();
        filePathByteCodeMap.forEach((filePath, fileContent) -> {
            fileHeaderInfo.append(filePath).append(":").append(fileContent.length()).append(";");
            fileContentList.add(fileContent);
        });
        ByteBuf byteBuf = new ByteBuf();
        byte[] headerInfo = fileHeaderInfo.toString().getBytes(StandardCharsets.UTF_8);
        byteBuf.writeInt(headerInfo.length);
        byteBuf.writeBytes(headerInfo);
        fileContentList.forEach(fileContent -> byteBuf.writeBytes(fileContent.getBytes(StandardCharsets.UTF_8)));
        return byteBuf.toByteArray();
    }

    @Override
    public void binaryDeserialization(byte[] bytes) {
        ByteBuf byteBuf = ByteBuf.wrap(bytes);
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
            filePathByteCodeMap.put(filePath, new String(fileByteCode, StandardCharsets.UTF_8));
        }
    }

    public void add(String fileName, String fileContent) {
        filePathByteCodeMap.put(fileName, fileContent);
    }
}
