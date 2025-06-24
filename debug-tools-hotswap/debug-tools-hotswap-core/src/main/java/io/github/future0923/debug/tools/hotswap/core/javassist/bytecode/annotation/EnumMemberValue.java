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
import java.lang.reflect.Method;

import io.github.future0923.debug.tools.hotswap.core.javassist.ClassPool;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.ConstPool;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.Descriptor;

/**
 * Enum constant value.
 *
 * @author <a href="mailto:bill@jboss.org">Bill Burke</a>
 * @author Shigeru Chiba
 */
public class EnumMemberValue extends MemberValue {
    int typeIndex, valueIndex;

    /**
     * Constructs an enum constant value.  The initial value is specified
     * by the constant pool entries at the given indexes.
     *
     * @param type      the index of a CONSTANT_Utf8_info structure
     *                  representing the enum type.
     * @param value     the index of a CONSTANT_Utf8_info structure.
     *                  representing the enum value.
     */
    public EnumMemberValue(int type, int value, ConstPool cp) {
        super('e', cp);
        this.typeIndex = type;
        this.valueIndex = value;
    }

    /**
     * Constructs an enum constant value.
     * The initial value is not specified.
     */
    public EnumMemberValue(ConstPool cp) {
        super('e', cp);
        typeIndex = valueIndex = 0;
    }

    @Override
    Object getValue(ClassLoader cl, ClassPool cp, Method m)
        throws ClassNotFoundException
    {
        try {
            return getType(cl).getField(getValue()).get(null);
        }
        catch (NoSuchFieldException e) {
            throw new ClassNotFoundException(getType() + "." + getValue());
        }
        catch (IllegalAccessException e) {
            throw new ClassNotFoundException(getType() + "." + getValue());
        }
    }

    @Override
    Class<?> getType(ClassLoader cl) throws ClassNotFoundException {
        return loadClass(cl, getType());
    }

    /**
     * Obtains the enum type name.
     *
     * @return a fully-qualified type name.
     */
    public String getType() {
        return Descriptor.toClassName(cp.getUtf8Info(typeIndex));
    }

    /**
     * Changes the enum type name.
     *
     * @param typename a fully-qualified type name.
     */
    public void setType(String typename) {
        typeIndex = cp.addUtf8Info(Descriptor.of(typename));
    }

    /**
     * Obtains the name of the enum constant value.
     */
    public String getValue() {
        return cp.getUtf8Info(valueIndex);
    }

    /**
     * Changes the name of the enum constant value.
     */
    public void setValue(String name) {
        valueIndex = cp.addUtf8Info(name);
    }

    @Override
    public String toString() {
        return getType() + "." + getValue();
    }

    /**
     * Writes the value.
     */
    @Override
    public void write(AnnotationsWriter writer) throws IOException {
        writer.enumConstValue(cp.getUtf8Info(typeIndex), getValue());
    }

    /**
     * Accepts a visitor.
     */
    @Override
    public void accept(MemberValueVisitor visitor) {
        visitor.visitEnumMemberValue(this);
    }
}
