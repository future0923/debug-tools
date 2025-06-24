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
 * Long integer constant value.
 *
 * @author <a href="mailto:bill@jboss.org">Bill Burke</a>
 * @author Shigeru Chiba
 */
public class LongMemberValue extends MemberValue {
    int valueIndex;

    /**
     * Constructs a long constant value.  The initial value is specified
     * by the constant pool entry at the given index.
     *
     * @param index     the index of a CONSTANT_Long_info structure.
     */
    public LongMemberValue(int index, ConstPool cp) {
        super('J', cp);
        this.valueIndex = index;
    }

    /**
     * Constructs a long constant value.
     *
     * @param j         the initial value.
     */
    public LongMemberValue(long j, ConstPool cp) {
        super('J', cp);
        setValue(j);
    }

    /**
     * Constructs a long constant value.  The initial value is 0.
     */
    public LongMemberValue(ConstPool cp) {
        super('J', cp);
        setValue(0L);
    }

    @Override
    Object getValue(ClassLoader cl, ClassPool cp, Method m) {
        return Long.valueOf(getValue());
    }

    @Override
    Class<?> getType(ClassLoader cl) {
        return long.class;
    }

    /**
     * Obtains the value of the member.
     */
    public long getValue() {
        return cp.getLongInfo(valueIndex);
    }

    /**
     * Sets the value of the member.
     */
    public void setValue(long newValue) {
        valueIndex = cp.addLongInfo(newValue);
    }

    /**
     * Obtains the string representation of this object.
     */
    @Override
    public String toString() {
        return Long.toString(getValue());
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
        visitor.visitLongMemberValue(this);
    }
}
