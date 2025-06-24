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
 * <code>LocalVariableTypeTable_attribute</code>.
 *
 * @since 3.11
 */
public class LocalVariableTypeAttribute extends LocalVariableAttribute {
    /**
     * The name of the attribute <code>"LocalVariableTypeTable"</code>.
     */
    public static final String tag = LocalVariableAttribute.typeTag;

    /**
     * Constructs an empty LocalVariableTypeTable.
     */
    public LocalVariableTypeAttribute(ConstPool cp) {
        super(cp, tag, new byte[2]);
        ByteArray.write16bit(0, info, 0);
    }

    LocalVariableTypeAttribute(ConstPool cp, int n, DataInputStream in)
        throws IOException
    {
        super(cp, n, in);
    }

    private LocalVariableTypeAttribute(ConstPool cp, byte[] dest) {
        super(cp, tag, dest);
    }

    @Override
    String renameEntry(String desc, String oldname, String newname) {
        return SignatureAttribute.renameClass(desc, oldname, newname);
    }

    @Override
    String renameEntry(String desc, Map<String,String> classnames) {
        return SignatureAttribute.renameClass(desc, classnames);
    }

    @Override
    LocalVariableAttribute makeThisAttr(ConstPool cp, byte[] dest) {
        return new LocalVariableTypeAttribute(cp, dest);
    }
}
