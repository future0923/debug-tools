package io.github.future0923.debug.tools.common.protocal.packet;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.common.protocal.Command;
import io.github.future0923.debug.tools.common.protocal.buffer.ByteBuf;
import io.github.future0923.debug.tools.common.protocal.packet.request.ClearRunResultRequestPacket;
import io.github.future0923.debug.tools.common.protocal.packet.request.HeartBeatRequestPacket;
import io.github.future0923.debug.tools.common.protocal.packet.request.HotSwapRequestPacket;
import io.github.future0923.debug.tools.common.protocal.packet.request.RunGroovyScriptRequestPacket;
import io.github.future0923.debug.tools.common.protocal.packet.request.RunTargetMethodRequestPacket;
import io.github.future0923.debug.tools.common.protocal.packet.request.ServerCloseRequestPacket;
import io.github.future0923.debug.tools.common.protocal.packet.response.HeartBeatResponsePacket;
import io.github.future0923.debug.tools.common.protocal.packet.response.RunGroovyScriptResponsePacket;
import io.github.future0923.debug.tools.common.protocal.packet.response.RunTargetMethodResponsePacket;
import io.github.future0923.debug.tools.common.protocal.serializer.Serializer;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * 对应命令数据的解码器
 *
 * @author future0923
 */
public class PacketCodec {

    private static final Logger logger = Logger.getLogger(PacketCodec.class);

    public static final int MAGIC_NUMBER = 305419896;

    public static final int MAGIC_BYTE_LENGTH = 4;

    public static final int VERSION_LENGTH = 1;

    public static final int COMMAND_LENGTH = 1;

    public static final int IP_LENGTH = 15;

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
        this.packetTypeMap.put(Command.HOTSWAP_REQUEST, HotSwapRequestPacket.class);
        this.serializerMap.put(Serializer.DEFAULT.getSerializerAlgorithm(), Serializer.DEFAULT);
    }

    public Packet getPacket(InputStream inputStream, Socket socket) throws IOException {
        int totalBytes = inputStream.read(new byte[MAGIC_BYTE_LENGTH]);
        if (totalBytes == -1) {
            throw new RuntimeException("EOF socket close " + socket.toString());
        } else {
            byte[] version = new byte[VERSION_LENGTH];
            inputStream.read(version);
            byte[] ipBytes = new byte[IP_LENGTH];
            inputStream.read(ipBytes);
            byte[] serializeAlgorithmByte = new byte[SERIALIZER_ALGORITHM_BYTE_LENGTH];
            inputStream.read(serializeAlgorithmByte);
            byte[] commandByte = new byte[COMMAND_LENGTH];
            inputStream.read(commandByte);
            Class<? extends Packet> requestType = getRequestType(commandByte[0]);
            if (requestType == null) {
                logger.error("requestType {} not found.", commandByte[0]);
                return null;
            }
            byte[] resultFlagByte = new byte[RESULT_FLAG_LENGTH];
            inputStream.read(resultFlagByte);
            byte[] lengthBytes = new byte[BODY_LENGTH];
            inputStream.read(lengthBytes);
            int length = ByteBuf.ByteUtil.toInt(lengthBytes);
            byte[] contentByte = new byte[length];
            inputStream.read(contentByte);
            Packet packet;
            try {
                packet = requestType.newInstance();
            } catch (Exception e) {
                logger.error("deserialize binary class: {} , serialize happen error : {}", requestType, e);
                return null;
            }
            packet.setVersion(version[0]);
            packet.setIpBytes(ipBytes);
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
        String localIP = getLocalIP();
        packet.setIpBytes(localIP.getBytes());
        byteBuf.writeBytes(packet.getIpBytes());
        byteBuf.writeByte(Serializer.DEFAULT.getSerializerAlgorithm());
        byteBuf.writeByte(packet.getCommand());
        byteBuf.writeByte(packet.getResultFlag());
        byteBuf.writeInt(bodyBytes.length);
        byteBuf.writeBytes(bodyBytes);
        return byteBuf;
    }

    public static String getLocalIP() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            logger.error("get local ip error ", e);
            return "";
        }
    }
}
