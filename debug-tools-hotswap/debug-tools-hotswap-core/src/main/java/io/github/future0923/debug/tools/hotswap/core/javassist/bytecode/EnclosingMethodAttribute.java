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
 * <code>EnclosingMethod_attribute</code>.
 */
public class EnclosingMethodAttribute extends AttributeInfo {
    /**
     * The name of this attribute <code>"EnclosingMethod"</code>.
     */
    public static final String tag = "EnclosingMethod";

    EnclosingMethodAttribute(ConstPool cp, int n, DataInputStream in)
        throws IOException
    {
        super(cp, n, in);
    }

    /**
     * Constructs an EnclosingMethod attribute.
     *
     * @param cp                a constant pool table.
     * @param className         the name of the innermost enclosing class.
     * @param methodName        the name of the enclosing method.
     * @param methodDesc        the descriptor of the enclosing method.
     */
    public EnclosingMethodAttribute(ConstPool cp, String className,
                                    String methodName, String methodDesc) {
        super(cp, tag);
        int ci = cp.addClassInfo(className);
        int ni = cp.addNameAndTypeInfo(methodName, methodDesc);
        byte[] bvalue = new byte[4];
        bvalue[0] = (byte)(ci >>> 8);
        bvalue[1] = (byte)ci;
        bvalue[2] = (byte)(ni >>> 8);
        bvalue[3] = (byte)ni;
        set(bvalue);
    }

    /**
     * Constructs an EnclosingMethod attribute.
     * The value of <code>method_index</code> is set to 0.
     *
     * @param cp                a constant pool table.
     * @param className         the name of the innermost enclosing class.
     */
    public EnclosingMethodAttribute(ConstPool cp, String className) {
        super(cp, tag);
        int ci = cp.addClassInfo(className);
        int ni = 0;
        byte[] bvalue = new byte[4];
        bvalue[0] = (byte)(ci >>> 8);
        bvalue[1] = (byte)ci;
        bvalue[2] = (byte)(ni >>> 8);
        bvalue[3] = (byte)ni;
        set(bvalue);
    }

    /**
     * Returns the value of <code>class_index</code>.
     */
    public int classIndex() {
        return ByteArray.readU16bit(get(), 0);
    }

    /**
     * Returns the value of <code>method_index</code>.
     */
    public int methodIndex() {
        return ByteArray.readU16bit(get(), 2);
    }

    /**
     * Returns the name of the class specified by <code>class_index</code>.
     */
    public String className() {
        return getConstPool().getClassInfo(classIndex());
    }

    /**
     * Returns the method name specified by <code>method_index</code>.
     * If the method is a class initializer (static constructor),
     * {@link MethodInfo#nameClinit} is returned.
     */
    public String methodName() {
        ConstPool cp = getConstPool();
        int mi = methodIndex();
        if (mi == 0)
            return MethodInfo.nameClinit;
        int ni = cp.getNameAndTypeName(mi);
        return cp.getUtf8Info(ni);
    }

    /**
     * Returns the method descriptor specified by <code>method_index</code>.
     */
    public String methodDescriptor() {
        ConstPool cp = getConstPool();
        int mi = methodIndex();
        int ti = cp.getNameAndTypeDescriptor(mi);
        return cp.getUtf8Info(ti);
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
        if (methodIndex() == 0)
            return new EnclosingMethodAttribute(newCp, className());
        return new EnclosingMethodAttribute(newCp, className(),
                                        methodName(), methodDescriptor());
    }
}
