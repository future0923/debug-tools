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
package io.github.future0923.debug.tools.common.protocal.buffer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author future0923
 */
public class ByteBuf {

    private ByteArrayInputStream inBuffer;

    private final ByteArrayOutputStream outBuffer = new ByteArrayOutputStream();

    public ByteBuf() {
    }

    public static ByteBuf wrap(byte[] bytes) {
        ByteBuf byteBuf = new ByteBuf();
        byteBuf.inBuffer = new ByteArrayInputStream(bytes);
        return byteBuf;
    }

    public synchronized void writeInt(int intVal) {
        try {
            this.outBuffer.write(ByteUtil.toByte(intVal));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void writeBytes(byte[] bytes) {
        try {
            this.outBuffer.write(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void writeByte(Byte b) {
        this.outBuffer.write(b);
    }

    public byte[] toByteArray() {
        return this.outBuffer.toByteArray();
    }

    public synchronized void release() {
        try {
            this.outBuffer.close();
            if (this.inBuffer != null) {
                this.inBuffer.close();
            }
        } catch (Exception ignored) {
        }

    }

    public synchronized int readInt() {
        byte[] intBytes = new byte[4];
        try {
            this.inBuffer.read(intBytes);
            return ByteUtil.toInt(intBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void readBytes(byte[] bytes) {
        try {
            this.inBuffer.read(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public byte readByte() {
        return (byte)this.inBuffer.read();
    }

    public static class ByteUtil {

        public static byte[] toByte(int intVal) {
            return new byte[]{(byte)(255 & intVal >> 24), (byte)(255 & intVal >> 16), (byte)(255 & intVal >> 8), (byte)(255 & intVal)};
        }

        public static int toInt(byte[] buf) {
            return (buf[0] & 255) << 24 | (buf[1] & 255) << 16 | (buf[2] & 255) << 8 | buf[3] & 255;
        }
    }
}
