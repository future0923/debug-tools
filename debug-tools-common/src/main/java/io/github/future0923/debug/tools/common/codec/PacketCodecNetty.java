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

import io.github.future0923.debug.tools.common.protocal.Command;
import io.github.future0923.debug.tools.common.protocal.packet.Packet;
import io.github.future0923.debug.tools.common.protocal.packet.request.ChangeTraceMethodRequestPacket;
import io.github.future0923.debug.tools.common.protocal.packet.request.ClearRunResultRequestPacket;
import io.github.future0923.debug.tools.common.protocal.packet.request.HeartBeatRequestPacket;
import io.github.future0923.debug.tools.common.protocal.packet.request.LocalCompilerHotDeployRequestPacket;
import io.github.future0923.debug.tools.common.protocal.packet.request.RemoteCompilerHotDeployRequestPacket;
import io.github.future0923.debug.tools.common.protocal.packet.request.ResourceHotDeployRequestPacket;
import io.github.future0923.debug.tools.common.protocal.packet.request.RunGroovyScriptRequestPacket;
import io.github.future0923.debug.tools.common.protocal.packet.request.RunTargetMethodRequestPacket;
import io.github.future0923.debug.tools.common.protocal.packet.request.ServerCloseRequestPacket;
import io.github.future0923.debug.tools.common.protocal.packet.response.HeartBeatResponsePacket;
import io.github.future0923.debug.tools.common.protocal.packet.response.HotDeployResponsePacket;
import io.github.future0923.debug.tools.common.protocal.packet.response.RunGroovyScriptResponsePacket;
import io.github.future0923.debug.tools.common.protocal.packet.response.RunTargetMethodResponsePacket;
import io.github.future0923.debug.tools.common.protocal.serializer.BinaryNettySerializer;
import io.github.future0923.debug.tools.common.protocal.serializer.NettySerializer;
import io.github.future0923.debug.tools.common.protocal.serializer.SerializerAlgorithm;
import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.Map;

public final class PacketCodecNetty {

    public static final PacketCodecNetty INSTANCE = new PacketCodecNetty();

    private static final int MAGIC = PacketFrameDecoder.MAGIC_NUMBER;

    private final Map<Byte, Class<? extends Packet>> packetTypeMap = new HashMap<>();
    private final Map<Byte, NettySerializer> serializerMap = new HashMap<>();

    private PacketCodecNetty() {
        serializerMap.put(SerializerAlgorithm.BINARY, new BinaryNettySerializer());
        packetTypeMap.put(Command.HEARTBEAT_REQUEST, HeartBeatRequestPacket.class);
        packetTypeMap.put(Command.HEARTBEAT_RESPONSE, HeartBeatResponsePacket.class);
        packetTypeMap.put(Command.RUN_TARGET_METHOD_REQUEST, RunTargetMethodRequestPacket.class);
        packetTypeMap.put(Command.RUN_TARGET_METHOD_RESPONSE, RunTargetMethodResponsePacket.class);
        packetTypeMap.put(Command.SERVER_CLOSE_REQUEST, ServerCloseRequestPacket.class);
        packetTypeMap.put(Command.CLEAR_RUN_RESULT, ClearRunResultRequestPacket.class);
        packetTypeMap.put(Command.RUN_GROOVY_SCRIPT_REQUEST, RunGroovyScriptRequestPacket.class);
        packetTypeMap.put(Command.RUN_GROOVY_SCRIPT_RESPONSE, RunGroovyScriptResponsePacket.class);
        packetTypeMap.put(Command.LOCAL_COMPILER_HOT_DEPLOY_REQUEST, LocalCompilerHotDeployRequestPacket.class);
        packetTypeMap.put(Command.REMOTE_COMPILER_HOT_DEPLOY_REQUEST, RemoteCompilerHotDeployRequestPacket.class);
        packetTypeMap.put(Command.REMOTE_COMPILER_HOT_DEPLOY_RESPONSE, HotDeployResponsePacket.class);
        packetTypeMap.put(Command.CHANGE_TRACE_METHOD_REQUEST, ChangeTraceMethodRequestPacket.class);
        packetTypeMap.put(Command.RESOURCE_HOT_DEPLOY_REQUEST, ResourceHotDeployRequestPacket.class);
    }

    public Packet decode(ByteBuf in) throws Exception {
        int magic = in.readInt();
        if (magic != MAGIC) {
            throw new IllegalStateException("bad magic");
        }
        byte version = in.readByte();
        byte serializerAlg = in.readByte();
        byte command = in.readByte();
        byte resultFlag = in.readByte();
        int bodyLen = in.readInt();
        Class<? extends Packet> clazz = packetTypeMap.get(command);
        NettySerializer serializer = serializerMap.get(serializerAlg);
        if (clazz == null || serializer == null) {
            in.skipBytes(bodyLen);
            return null;
        }
        Packet packet = clazz.getDeclaredConstructor().newInstance();
        packet.setVersion(version);
        packet.setResultFlag(resultFlag);
        serializer.deserialize(packet, in, bodyLen);
        return packet;
    }

    public void encode(ByteBuf out, Packet packet) throws Exception {
        NettySerializer serializer = serializerMap.get(SerializerAlgorithm.BINARY);
        out.writeInt(MAGIC);
        out.writeByte(packet.getVersion());
        out.writeByte(serializer.getSerializerAlgorithm());
        out.writeByte(packet.getCommand());
        out.writeByte(packet.getResultFlag());
        int bodyLenIndex = out.writerIndex();
        out.writeInt(0);
        int bodyStart = out.writerIndex();
        serializer.serialize(out, packet);
        int bodyEnd = out.writerIndex();
        out.setInt(bodyLenIndex, bodyEnd - bodyStart);
    }
}
