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
 * <code>ConstantValue_attribute</code>.
 */
public class ConstantAttribute extends AttributeInfo {
    /**
     * The name of this attribute <code>"ConstantValue"</code>.
     */
    public static final String tag = "ConstantValue";

    ConstantAttribute(ConstPool cp, int n, DataInputStream in)
        throws IOException
    {
        super(cp, n, in);
    }

    /**
     * Constructs a ConstantValue attribute.
     *
     * @param cp                a constant pool table.
     * @param index             <code>constantvalue_index</code>
     *                          of <code>ConstantValue_attribute</code>.
     */
    public ConstantAttribute(ConstPool cp, int index) {
        super(cp, tag);
        byte[] bvalue = new byte[2];
        bvalue[0] = (byte)(index >>> 8);
        bvalue[1] = (byte)index;
        set(bvalue);
    }

    /**
     * Returns <code>constantvalue_index</code>.
     */
    public int getConstantValue() {
        return ByteArray.readU16bit(get(), 0);
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
    public AttributeInfo copy(ConstPool newCp, Map<String,String> classnames) {
        int index = getConstPool().copy(getConstantValue(), newCp,
                                        classnames);
        return new ConstantAttribute(newCp, index);
    }
}
