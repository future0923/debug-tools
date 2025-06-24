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

import io.github.future0923.debug.tools.hotswap.core.javassist.ClassPool;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.ConstPool;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * Floating-point number constant value.
 *
 * @author <a href="mailto:bill@jboss.org">Bill Burke</a>
 * @author Shigeru Chiba
 * @version $Revision: 1.7 $
 */
public class FloatMemberValue extends MemberValue {
    int valueIndex;

    /**
     * Constructs a float constant value.  The initial value is specified
     * by the constant pool entry at the given index.
     *
     * @param index     the index of a CONSTANT_Float_info structure.
     */
    public FloatMemberValue(int index, ConstPool cp) {
        super('F', cp);
        this.valueIndex = index;
    }

    /**
     * Constructs a float constant value.
     *
     * @param f         the initial value.
     */
    public FloatMemberValue(float f, ConstPool cp) {
        super('F', cp);
        setValue(f);
    }

    /**
     * Constructs a float constant value.  The initial value is 0.0.
     */
    public FloatMemberValue(ConstPool cp) {
        super('F', cp);
        setValue(0.0F);
    }

    @Override
    Object getValue(ClassLoader cl, ClassPool cp, Method m) {
        return Float.valueOf(getValue());
    }

    @Override
    Class<?> getType(ClassLoader cl) {
        return float.class;
    }

    /**
     * Obtains the value of the member.
     */
    public float getValue() {
        return cp.getFloatInfo(valueIndex);
    }

    /**
     * Sets the value of the member.
     */
    public void setValue(float newValue) {
        valueIndex = cp.addFloatInfo(newValue);
    }

    /**
     * Obtains the string representation of this object.
     */
    @Override
    public String toString() {
        return Float.toString(getValue());
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
        visitor.visitFloatMemberValue(this);
    }
}
