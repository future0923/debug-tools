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

/**
 * Short integer constant value.
 *
 * @author <a href="mailto:bill@jboss.org">Bill Burke</a>
 * @author Shigeru Chiba
 */
public class ShortMemberValue extends MemberValue {
    int valueIndex;

    /**
     * Constructs a short constant value.  The initial value is specified
     * by the constant pool entry at the given index.
     *
     * @param index     the index of a CONSTANT_Integer_info structure.
     */
    public ShortMemberValue(int index, ConstPool cp) {
        super('S', cp);
        this.valueIndex = index;
    }

    /**
     * Constructs a short constant value.
     *
     * @param s         the initial value.
     */
    public ShortMemberValue(short s, ConstPool cp) {
        super('S', cp);
        setValue(s);
    }

    /**
     * Constructs a short constant value.  The initial value is 0.
     */
    public ShortMemberValue(ConstPool cp) {
        super('S', cp);
        setValue((short)0);
    }

    @Override
    Object getValue(ClassLoader cl, ClassPool cp, Method m) {
        return Short.valueOf(getValue());
    }

    @Override
    Class<?> getType(ClassLoader cl) {
        return short.class;
    }

    /**
     * Obtains the value of the member.
     */
    public short getValue() {
        return (short)cp.getIntegerInfo(valueIndex);
    }

    /**
     * Sets the value of the member.
     */
    public void setValue(short newValue) {
        valueIndex = cp.addIntegerInfo(newValue);
    }

    /**
     * Obtains the string representation of this object.
     */
    @Override
    public String toString() {
        return Short.toString(getValue());
    }

    /**
     * Writes the value.
     */
    @Override
    public void write(AnnotationsWriter writer) throws IOException {
        writer.constValueIndex(getValue());
    }

    /**
     * Accepts a visitor.
     */
    @Override
    public void accept(MemberValueVisitor visitor) {
        visitor.visitShortMemberValue(this);
    }
}
