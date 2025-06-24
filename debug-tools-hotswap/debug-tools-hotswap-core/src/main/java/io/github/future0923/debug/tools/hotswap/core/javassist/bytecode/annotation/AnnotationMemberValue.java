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
 * Nested annotation.
 *
 * @author <a href="mailto:bill@jboss.org">Bill Burke</a>
 * @author Shigeru Chiba
 */
public class AnnotationMemberValue extends MemberValue {
    Annotation value;

    /**
     * Constructs an annotation member.  The initial value is not specified.
     */
    public AnnotationMemberValue(ConstPool cp) {
        this(null, cp);
    }

    /**
     * Constructs an annotation member.  The initial value is specified by
     * the first parameter.
     */
    public AnnotationMemberValue(Annotation a, ConstPool cp) {
        super('@', cp);
        value = a;
    }

    @Override
    Object getValue(ClassLoader cl, ClassPool cp, Method m)
        throws ClassNotFoundException
    {
        return AnnotationImpl.make(cl, getType(cl), cp, value);
    }

    @Override
    Class<?> getType(ClassLoader cl) throws ClassNotFoundException {
        if (value == null)
            throw new ClassNotFoundException("no type specified");
        return loadClass(cl, value.getTypeName());
    }

    /**
     * Obtains the value.
     */
    public Annotation getValue() {
        return value;
    }

    /**
     * Sets the value of this member.
     */
    public void setValue(Annotation newValue) {
        value = newValue;
    }

    /**
     * Obtains the string representation of this object.
     */
    @Override
    public String toString() {
        return value.toString();
    }

    /**
     * Writes the value.
     */
    @Override
    public void write(AnnotationsWriter writer) throws IOException {
        writer.annotationValue();
        value.write(writer);
    }

    /**
     * Accepts a visitor.
     */
    @Override
    public void accept(MemberValueVisitor visitor) {
        visitor.visitAnnotationMemberValue(this);
    }
}
