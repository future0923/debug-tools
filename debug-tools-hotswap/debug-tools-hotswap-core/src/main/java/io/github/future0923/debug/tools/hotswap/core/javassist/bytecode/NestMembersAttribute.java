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
package io.github.future0923.debug.tools.hotswap.core.javassist.bytecode;


import java.io.DataInputStream;
import java.io.IOException;
import java.util.Map;

/**
 * <code>NestMembers_attribute</code>.
 * It was introduced by JEP-181.  See JVMS 4.7.29 for the specification.
 *
 * @since 3.24
 */
public class NestMembersAttribute extends AttributeInfo {
    /**
     * The name of this attribute <code>"NestMembers"</code>.
     */
    public static final String tag = "NestMembers";

    NestMembersAttribute(ConstPool cp, int n, DataInputStream in) throws IOException {
        super(cp, n, in);
    }

    private NestMembersAttribute(ConstPool cp, byte[] info) {
        super(cp, tag, info);
    }

    /**
     * Makes a copy.  Class names are replaced according to the
     * given <code>Map</code> object.
     *
     * @param newCp     the constant pool table used by the new copy.
     * @param classnames        pairs of replaced and substituted
     *                          class names.
     */
    @Override
    public AttributeInfo copy(ConstPool newCp, Map<String, String> classnames) {
        byte[] src = get();
        byte[] dest = new byte[src.length];
        ConstPool cp = getConstPool();

        int n = ByteArray.readU16bit(src, 0);
        ByteArray.write16bit(n, dest, 0);

        for (int i = 0, j = 2; i < n; ++i, j += 2) {
            int index = ByteArray.readU16bit(src, j);
            int newIndex = cp.copy(index, newCp, classnames);
            ByteArray.write16bit(newIndex, dest, j);
        }

        return new NestMembersAttribute(newCp, dest);
    }

    /**
     * Returns <code>number_of_classes</code>.
     * @return the number of the classes recorded in this attribute.
     */
    public int numberOfClasses() {
        return ByteArray.readU16bit(info, 0);
    }

    /** Returns <code>classes[index]</code>.
     * 
     * @param index   the index into <code>classes</code>.
     * @return the value at the given index in the <code>classes</code> array.
     *   It is an index into the constant pool.
     *   The constant pool entry at the returned index is a
     *   <code>CONSTANT_Class_info</code> structure.
     */
    public int memberClass(int index) {
        return ByteArray.readU16bit(info, index * 2 + 2);
    }
}
