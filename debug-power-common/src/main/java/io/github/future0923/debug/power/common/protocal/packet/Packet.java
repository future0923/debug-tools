package io.github.future0923.debug.power.common.protocal.packet;

import io.github.future0923.debug.power.common.protocal.buffer.ByteBuf;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 需要加入到{@link PacketCodec}中
 *
 * @author future0923
 */
public abstract class Packet {

    private static final String EMPTY_BYTE = "\u0000";
    private static final String EMPTY_STRING = "";
    private byte version;
    private final byte[] ipBytes = new byte[15];
    private byte resultFlag;
    public static final byte SUCCESS = 1;
    public static final byte FAIL = 0;

    public Packet() {
    }

    public abstract Byte getCommand();

    public abstract byte[] binarySerialize();

    public abstract void binaryDeserialization(byte[] bytes);

    public void setVersion(byte version) {
        this.version = version;
    }

    public Byte getVersion() {
        return this.version;
    }

    public byte[] getIpBytes() {
        return this.ipBytes;
    }

    public void setIpBytes(byte[] bytes) {
        System.arraycopy(bytes, 0, this.ipBytes, 0, bytes.length);
    }

    public String getIp() {
        return (new String(this.ipBytes)).replaceAll(EMPTY_BYTE, EMPTY_STRING);
    }

    public byte getResultFlag() {
        return resultFlag;
    }

    public void setResultFlag(byte resultFlag) {
        this.resultFlag = resultFlag;
    }

    public void writeAndFlush(OutputStream outputStream) throws IOException {
        ByteBuf byteBuf = PacketCodec.INSTANCE.encode(this);
        outputStream.write(byteBuf.toByteArray());
        outputStream.flush();
    }
}
