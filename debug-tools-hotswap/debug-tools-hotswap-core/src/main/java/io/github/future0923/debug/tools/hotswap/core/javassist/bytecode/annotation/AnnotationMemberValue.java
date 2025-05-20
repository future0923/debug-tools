/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
