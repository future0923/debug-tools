package io.github.future0923.debug.power.common.utils;

import io.github.future0923.debug.power.common.protocal.buffer.ByteBuf;
import io.github.future0923.debug.power.common.protocal.packet.Packet;
import io.github.future0923.debug.power.common.protocal.packet.PacketCodec;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author future0923
 */
public class DebugPowerIOUtils {

    public static byte[] readAllBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int bytesRead;
        byte[] data = new byte[1024];
        while ((bytesRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, bytesRead);
        }
        buffer.flush();
        return buffer.toByteArray();
    }

    public static void writeAndFlush(OutputStream outputStream, Packet packet) throws Exception {
        ByteBuf byteBuf = PacketCodec.INSTANCE.encode(packet);
        outputStream.write(byteBuf.toByteArray());
        outputStream.flush();
    }
}
