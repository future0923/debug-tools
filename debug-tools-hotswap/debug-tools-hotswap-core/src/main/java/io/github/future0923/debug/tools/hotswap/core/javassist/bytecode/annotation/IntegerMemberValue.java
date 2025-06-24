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
 * Integer constant value.
 *
 * @author <a href="mailto:bill@jboss.org">Bill Burke</a>
 * @author Shigeru Chiba
 */
public class IntegerMemberValue extends MemberValue {
    int valueIndex;

    /**
     * Constructs an int constant value.  The initial value is specified
     * by the constant pool entry at the given index.
     *
     * @param index     the index of a CONSTANT_Integer_info structure.
     */
    public IntegerMemberValue(int index, ConstPool cp) {
        super('I', cp);
        this.valueIndex = index;
    }

    /**
     * Constructs an int constant value.
     * Note that this constructor receives <b>the initial value
     * as the second parameter</b>
     * unlike the corresponding constructors in the sibling classes.
     * This is for making a difference from the constructor that receives
     * an index into the constant pool table as the first parameter.
     * Note that the index is also int type.
     *
     * @param value         the initial value.
     */
    public IntegerMemberValue(ConstPool cp, int value) {
        super('I', cp);
        setValue(value);
    }

    /**
     * Constructs an int constant value.  The initial value is 0.
     */
    public IntegerMemberValue(ConstPool cp) {
        super('I', cp);
        setValue(0);
    }

    @Override
    Object getValue(ClassLoader cl, ClassPool cp, Method m) {
        return Integer.valueOf(getValue());
    }

    @Override
    Class<?> getType(ClassLoader cl) {
        return int.class;
    }

    /**
     * Obtains the value of the member.
     */
    public int getValue() {
        return cp.getIntegerInfo(valueIndex);
    }

    /**
     * Sets the value of the member.
     */
    public void setValue(int newValue) {
        valueIndex = cp.addIntegerInfo(newValue);
    }

    /**
     * Obtains the string representation of this object.
     */
    @Override
    public String toString() {
        return Integer.toString(getValue());
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
        visitor.visitIntegerMemberValue(this);
    }
}
