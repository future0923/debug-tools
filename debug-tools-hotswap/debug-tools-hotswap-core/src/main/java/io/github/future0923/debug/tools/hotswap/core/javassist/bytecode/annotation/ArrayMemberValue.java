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
package io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.annotation;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;

import io.github.future0923.debug.tools.hotswap.core.javassist.ClassPool;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.ConstPool;

/**
 * Array member.
 *
 * @author <a href="mailto:bill@jboss.org">Bill Burke</a>
 * @author Shigeru Chiba
 */
public class ArrayMemberValue extends MemberValue {
    MemberValue type;
    MemberValue[] values;

    /**
     * Constructs an array.  The initial value or type are not specified.
     */
    public ArrayMemberValue(ConstPool cp) {
        super('[', cp);
        type = null;
        values = null;
    }

    /**
     * Constructs an array.  The initial value is not specified.
     *
     * @param t         the type of the array elements.
     */
    public ArrayMemberValue(MemberValue t, ConstPool cp) {
        super('[', cp);
        type = t;
        values = null;
    }

    @Override
    Object getValue(ClassLoader cl, ClassPool cp, Method method)
        throws ClassNotFoundException
    {
        if (values == null)
            throw new ClassNotFoundException(
                        "no array elements found: " + method.getName());

        int size = values.length;
        Class<?> clazz;
        if (type == null) {
            clazz = method.getReturnType().getComponentType();
            if (clazz == null || size > 0)
                throw new ClassNotFoundException("broken array type: "
                                                 + method.getName());
        }
        else
            clazz = type.getType(cl);

        Object a = Array.newInstance(clazz, size);
        for (int i = 0; i < size; i++)
            Array.set(a, i, values[i].getValue(cl, cp, method));

        return a;
    }

    @Override
    Class<?> getType(ClassLoader cl) throws ClassNotFoundException {
        if (type == null)
            throw new ClassNotFoundException("no array type specified");

        Object a = Array.newInstance(type.getType(cl), 0);
        return a.getClass();
    }

    /**
     * Obtains the type of the elements.
     *
     * @return null if the type is not specified.
     */
    public MemberValue getType() {
        return type;
    }

    /**
     * Obtains the elements of the array.
     */
    public MemberValue[] getValue() {
        return values;
    }

    /**
     * Sets the elements of the array.
     */
    public void setValue(MemberValue[] elements) {
        values = elements;
        if (elements != null && elements.length > 0)
            type = elements[0];
    }

    /**
     * Obtains the string representation of this object.
     */
    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer("{");
        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                buf.append(values[i].toString());
                if (i + 1 < values.length)
                    buf.append(", ");
                }
        }

        buf.append("}");
        return buf.toString();
    }

    /**
     * Writes the value.
     */
    @Override
    public void write(AnnotationsWriter writer) throws IOException {
        int num = values == null ? 0 : values.length;
        writer.arrayValue(num);
        for (int i = 0; i < num; ++i)
            values[i].write(writer);
    }

    /**
     * Accepts a visitor.
     */
    @Override
    public void accept(MemberValueVisitor visitor) {
        visitor.visitArrayMemberValue(this);
    }
}
