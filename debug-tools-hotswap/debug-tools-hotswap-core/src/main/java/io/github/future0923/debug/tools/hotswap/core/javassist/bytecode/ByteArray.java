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
package io.github.future0923.debug.tools.hotswap.core.javassist.bytecode;

/**
 * A collection of static methods for reading and writing a byte array.
 */
public class ByteArray {
    /**
     * Reads an unsigned 16bit integer at the index.
     */
    public static int readU16bit(byte[] code, int index) {
        return ((code[index] & 0xff) << 8) | (code[index + 1] & 0xff);
    }

    /**
     * Reads a signed 16bit integer at the index.
     */
    public static int readS16bit(byte[] code, int index) {
        return (code[index] << 8) | (code[index + 1] & 0xff);
    }

    /**
     * Writes a 16bit integer at the index.
     */
    public static void write16bit(int value, byte[] code, int index) {
        code[index] = (byte)(value >>> 8);
        code[index + 1] = (byte)value;
    }

    /**
     * Reads a 32bit integer at the index.
     */
    public static int read32bit(byte[] code, int index) {
        return (code[index] << 24) | ((code[index + 1] & 0xff) << 16)
               | ((code[index + 2] & 0xff) << 8) | (code[index + 3] & 0xff);
    }

    /**
     * Writes a 32bit integer at the index.
     */
    public static void write32bit(int value, byte[] code, int index) {
        code[index] = (byte)(value >>> 24);
        code[index + 1] = (byte)(value >>> 16);
        code[index + 2] = (byte)(value >>> 8);
        code[index + 3] = (byte)value;
    }

    /**
     * Copies a 32bit integer.
     *
     * @param src       the source byte array.
     * @param isrc      the index into the source byte array.
     * @param dest      the destination byte array.
     * @param idest     the index into the destination byte array.
     */
    static void copy32bit(byte[] src, int isrc, byte[] dest, int idest) {
        dest[idest] = src[isrc];
        dest[idest + 1] = src[isrc + 1];
        dest[idest + 2] = src[isrc + 2];
        dest[idest + 3] = src[isrc + 3];
    }
}
