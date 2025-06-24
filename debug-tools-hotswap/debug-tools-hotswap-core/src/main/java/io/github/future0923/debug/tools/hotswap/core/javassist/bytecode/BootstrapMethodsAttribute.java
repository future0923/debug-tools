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

public class BootstrapMethodsAttribute extends AttributeInfo {
    /**
     * The name of this attribute <code>"BootstrapMethods"</code>.
     */
    public static final String tag = "BootstrapMethods";

    /**
     * An element of <code>bootstrap_methods</code>.
     */
    public static class BootstrapMethod {
        /**
         * Constructs an element of <code>bootstrap_methods</code>.
         *
         * @param method        <code>bootstrap_method_ref</code>.
         * @param args          <code>bootstrap_arguments</code>.
         */
        public BootstrapMethod(int method, int[] args) {
            methodRef = method;
            arguments = args;
        }

        /**
         * <code>bootstrap_method_ref</code>.
         * The value at this index must be a <code>CONSTANT_MethodHandle_info</code>.
         */
        public int methodRef;

        /**
         * <code>bootstrap_arguments</code>.
         */
        public int[] arguments;
    }

    BootstrapMethodsAttribute(ConstPool cp, int n, DataInputStream in)
        throws IOException
    {
        super(cp, n, in);
    }

    /**
     * Constructs a BootstrapMethods attribute.
     *
     * @param cp                a constant pool table.
     * @param methods           the contents.
     */
    public BootstrapMethodsAttribute(ConstPool cp, BootstrapMethod[] methods) {
        super(cp, tag);
        int size = 2;
        for (int i = 0; i < methods.length; i++)
            size += 4 + methods[i].arguments.length * 2;

        byte[] data = new byte[size];
        ByteArray.write16bit(methods.length, data, 0);    // num_bootstrap_methods
        int pos = 2;
        for (int i = 0; i < methods.length; i++) {
            ByteArray.write16bit(methods[i].methodRef, data, pos);
            ByteArray.write16bit(methods[i].arguments.length, data, pos + 2);
            int[] args = methods[i].arguments;
            pos += 4;
            for (int k = 0; k < args.length; k++) {
                ByteArray.write16bit(args[k], data, pos);
                pos += 2;
            }
        }

        set(data);
    }

    /**
     * Obtains <code>bootstrap_methods</code> in this attribute.
     *
     * @return an array of <code>BootstrapMethod</code>.  Since it
     *          is a fresh copy, modifying the returned array does not
     *          affect the original contents of this attribute.
     */
    public BootstrapMethod[] getMethods() {
        byte[] data = this.get();
        int num = ByteArray.readU16bit(data, 0);
        BootstrapMethod[] methods = new BootstrapMethod[num];
        int pos = 2;
        for (int i = 0; i < num; i++) {
            int ref = ByteArray.readU16bit(data, pos);
            int len = ByteArray.readU16bit(data, pos + 2);
            int[] args = new int[len];
            pos += 4;
            for (int k = 0; k < len; k++) {
                args[k] = ByteArray.readU16bit(data, pos);
                pos += 2;
            }

            methods[i] = new BootstrapMethod(ref, args);
        }

        return methods;
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
        BootstrapMethod[] methods = getMethods();
        ConstPool thisCp = getConstPool();
        for (int i = 0; i < methods.length; i++) {
            BootstrapMethod m = methods[i];
            m.methodRef = thisCp.copy(m.methodRef, newCp, classnames);
            for (int k = 0; k < m.arguments.length; k++)
                m.arguments[k] = thisCp.copy(m.arguments[k], newCp, classnames);
        }

        return new BootstrapMethodsAttribute(newCp, methods);
    }
}
