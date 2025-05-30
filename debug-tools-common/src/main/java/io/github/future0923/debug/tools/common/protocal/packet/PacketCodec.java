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
package io.github.future0923.debug.tools.common.protocal.packet;

import io.github.future0923.debug.tools.base.hutool.core.io.IoUtil;
import io.github.future0923.debug.tools.base.hutool.core.util.ObjectUtil;
import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.common.protocal.Command;
import io.github.future0923.debug.tools.common.protocal.buffer.ByteBuf;
import io.github.future0923.debug.tools.common.protocal.packet.request.ClearRunResultRequestPacket;
import io.github.future0923.debug.tools.common.protocal.packet.request.HeartBeatRequestPacket;
import io.github.future0923.debug.tools.common.protocal.packet.request.LocalCompilerHotDeployRequestPacket;
import io.github.future0923.debug.tools.common.protocal.packet.request.RemoteCompilerHotDeployRequestPacket;
import io.github.future0923.debug.tools.common.protocal.packet.request.RunGroovyScriptRequestPacket;
import io.github.future0923.debug.tools.common.protocal.packet.request.RunTargetMethodRequestPacket;
import io.github.future0923.debug.tools.common.protocal.packet.request.ServerCloseRequestPacket;
import io.github.future0923.debug.tools.common.protocal.packet.response.HeartBeatResponsePacket;
import io.github.future0923.debug.tools.common.protocal.packet.response.HotDeployResponsePacket;
import io.github.future0923.debug.tools.common.protocal.packet.response.RunGroovyScriptResponsePacket;
import io.github.future0923.debug.tools.common.protocal.packet.response.RunTargetMethodResponsePacket;
import io.github.future0923.debug.tools.common.protocal.serializer.Serializer;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 对应命令数据的解码器
 *
 * @author future0923
 */
public class PacketCodec {

    private static final Logger logger = Logger.getLogger(PacketCodec.class);

    public static final int MAGIC_NUMBER = 20240508;

    public static final int MAGIC_BYTE_LENGTH = 4;

    public static final int VERSION_LENGTH = 1;

    public static final int COMMAND_LENGTH = 1;

    public static final int SERIALIZER_ALGORITHM_BYTE_LENGTH = 1;

    public static final int BODY_LENGTH = 4;

    public static final int RESULT_FLAG_LENGTH = 1;

    public static final PacketCodec INSTANCE = new PacketCodec();

    private final Map<Byte, Class<? extends Packet>> packetTypeMap = new HashMap<>();

    private final Map<Byte, Serializer> serializerMap = new HashMap<>();

    private PacketCodec() {
        this.packetTypeMap.put(Command.HEARTBEAT_REQUEST, HeartBeatRequestPacket.class);
        this.packetTypeMap.put(Command.HEARTBEAT_RESPONSE, HeartBeatResponsePacket.class);
        this.packetTypeMap.put(Command.RUN_TARGET_METHOD_REQUEST, RunTargetMethodRequestPacket.class);
        this.packetTypeMap.put(Command.RUN_TARGET_METHOD_RESPONSE, RunTargetMethodResponsePacket.class);
        this.packetTypeMap.put(Command.SERVER_CLOSE_REQUEST, ServerCloseRequestPacket.class);
        this.packetTypeMap.put(Command.CLEAR_RUN_RESULT, ClearRunResultRequestPacket.class);
        this.packetTypeMap.put(Command.RUN_GROOVY_SCRIPT_REQUEST, RunGroovyScriptRequestPacket.class);
        this.packetTypeMap.put(Command.RUN_GROOVY_SCRIPT_RESPONSE, RunGroovyScriptResponsePacket.class);
        this.packetTypeMap.put(Command.LOCAL_COMPILER_HOT_DEPLOY_REQUEST, LocalCompilerHotDeployRequestPacket.class);
        this.packetTypeMap.put(Command.REMOTE_COMPILER_HOT_DEPLOY_REQUEST, RemoteCompilerHotDeployRequestPacket.class);
        this.packetTypeMap.put(Command.REMOTE_COMPILER_HOT_DEPLOY_RESPONSE, HotDeployResponsePacket.class);
        this.serializerMap.put(Serializer.DEFAULT.getSerializerAlgorithm(), Serializer.DEFAULT);
    }

    public Packet getPacket(InputStream inputStream) throws IOException {
        int magic = ByteBuf.ByteUtil.toInt(IoUtil.readBytes(inputStream, MAGIC_BYTE_LENGTH));
        if (ObjectUtil.notEqual(MAGIC_NUMBER, magic)) {
            logger.error("magic number not match {}.", magic);
            return null;
        } else {
            byte[] version = IoUtil.readBytes(inputStream, VERSION_LENGTH);
            byte[] serializeAlgorithmByte = IoUtil.readBytes(inputStream, SERIALIZER_ALGORITHM_BYTE_LENGTH);
            byte[] commandByte = IoUtil.readBytes(inputStream, COMMAND_LENGTH);
            Class<? extends Packet> requestType = getRequestType(commandByte[0]);
            if (requestType == null) {
                logger.error("requestType {} not found.", commandByte[0]);
                return null;
            }
            byte[] resultFlagByte = IoUtil.readBytes(inputStream, RESULT_FLAG_LENGTH);
            byte[] lengthBytes = IoUtil.readBytes(inputStream, BODY_LENGTH);
            byte[] contentByte =IoUtil.readBytes(inputStream, ByteBuf.ByteUtil.toInt(lengthBytes));
            Packet packet;
            try {
                packet = requestType.newInstance();
            } catch (Exception e) {
                logger.error("deserialize binary class: {} , serialize happen error : {}", requestType, e);
                return null;
            }
            packet.setVersion(version[0]);
            packet.setResultFlag(resultFlagByte[0]);
            Serializer serializer = this.getSerializer(serializeAlgorithmByte[0]);
            if (serializer != null) {
                serializer.deserialize(packet, contentByte);
                return packet;
            } else {
                return null;
            }
        }
    }

    private Class<? extends Packet> getRequestType(byte command) {
        return this.packetTypeMap.get(command);
    }

    private Serializer getSerializer(byte serializeAlgorithm) {
        return this.serializerMap.get(serializeAlgorithm);
    }

    public ByteBuf encode(Packet packet) {
        ByteBuf byteBuf = new ByteBuf();
        byte[] bodyBytes = Serializer.DEFAULT.serialize(packet);
        byteBuf.writeInt(MAGIC_NUMBER);
        byteBuf.writeByte(packet.getVersion());
        byteBuf.writeByte(Serializer.DEFAULT.getSerializerAlgorithm());
        byteBuf.writeByte(packet.getCommand());
        byteBuf.writeByte(packet.getResultFlag());
        byteBuf.writeInt(bodyBytes.length);
        byteBuf.writeBytes(bodyBytes);
        return byteBuf;
    }
}
