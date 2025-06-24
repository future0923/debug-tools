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
 * Double floating-point number constant value.
 *
 * @author <a href="mailto:bill@jboss.org">Bill Burke</a>
 * @author Shigeru Chiba
 * @version $Revision: 1.7 $
 */
public class DoubleMemberValue extends MemberValue {
    int valueIndex;

    /**
     * Constructs a double constant value.  The initial value is specified
     * by the constant pool entry at the given index.
     *
     * @param index     the index of a CONSTANT_Double_info structure.
     */
    public DoubleMemberValue(int index, ConstPool cp) {
        super('D', cp);
        this.valueIndex = index;
    }

    /**
     * Constructs a double constant value.
     *
     * @param d     the initial value.
     */
    public DoubleMemberValue(double d, ConstPool cp) {
        super('D', cp);
        setValue(d);
    }

    /**
     * Constructs a double constant value.  The initial value is 0.0.
     */
    public DoubleMemberValue(ConstPool cp) {
        super('D', cp);
        setValue(0.0);
    }

    @Override
    Object getValue(ClassLoader cl, ClassPool cp, Method m) {
        return Double.valueOf(getValue());
    }

    @Override
    Class<?> getType(ClassLoader cl) {
        return double.class;
    }

    /**
     * Obtains the value of the member.
     */
    public double getValue() {
        return cp.getDoubleInfo(valueIndex);
    }

    /**
     * Sets the value of the member.
     */
    public void setValue(double newValue) {
        valueIndex = cp.addDoubleInfo(newValue);
    }

    /**
     * Obtains the string representation of this object.
     */
    @Override
    public String toString() {
        return Double.toString(getValue());
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
        visitor.visitDoubleMemberValue(this);
    }
}
