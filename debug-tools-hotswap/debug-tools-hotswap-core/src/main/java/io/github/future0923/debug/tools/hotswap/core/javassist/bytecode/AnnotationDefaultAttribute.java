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

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Map;

import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.annotation.AnnotationsWriter;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.annotation.MemberValue;

/**
 * A class representing <code>AnnotationDefault_attribute</code>.
 *
 * <p>For example, if you declare the following annotation type:
 *
 * <pre>
 * &#64;interface Author {
 *   String name() default "Shakespeare";
 *   int age() default 99;
 * }
 * </pre>
 *
 * <p>The defautl values of <code>name</code> and <code>age</code>
 * are stored as annotation default attributes in <code>Author.class</code>.
 * The following code snippet obtains the default value of <code>name</code>:
 * 
 * <pre>
 * ClassPool pool = ...
 * CtClass cc = pool.get("Author");
 * CtMethod cm = cc.getDeclaredMethod("age");
 * MethodInfo minfo = cm.getMethodInfo();
 * AnnotationDefaultAttribute ada
 *         = (AnnotationDefaultAttribute)
 *           minfo.getAttribute(AnnotationDefaultAttribute.tag);
 * MemberValue value = ada.getDefaultValue());    // default value of age
 * </pre>
 *
 * <p>If the following statement is executed after the code above,
 * the default value of age is set to 80:
 *
 * <pre>
 * ada.setDefaultValue(new IntegerMemberValue(minfo.getConstPool(), 80));
 * </pre>
 *
 * @see AnnotationsAttribute
 * @see javassist.bytecode.annotation.MemberValue
 */

public class AnnotationDefaultAttribute extends AttributeInfo {
    /**
     * The name of the <code>AnnotationDefault</code> attribute.
     */
    public static final String tag = "AnnotationDefault";

    /**
     * Constructs an <code>AnnotationDefault_attribute</code>.
     *
     * @param cp            constant pool
     * @param info          the contents of this attribute.  It does not
     *                      include <code>attribute_name_index</code> or
     *                      <code>attribute_length</code>.
     */
    public AnnotationDefaultAttribute(ConstPool cp, byte[] info) {
        super(cp, tag, info);
    }

    /**
     * Constructs an empty <code>AnnotationDefault_attribute</code>.
     * The default value can be set by <code>setDefaultValue()</code>.
     *
     * @param cp            constant pool
     * @see #setDefaultValue(javassist.bytecode.annotation.MemberValue)
     */
    public AnnotationDefaultAttribute(ConstPool cp) {
        this(cp, new byte[] { 0, 0 });
    }

    /**
     * @param n     the attribute name.
     */
    AnnotationDefaultAttribute(ConstPool cp, int n, DataInputStream in)
        throws IOException
    {
        super(cp, n, in);
    }

    /**
     * Copies this attribute and returns a new copy.
     */
    @Override
    public AttributeInfo copy(ConstPool newCp, Map<String,String> classnames) {
        AnnotationsAttribute.Copier copier
            = new AnnotationsAttribute.Copier(info, constPool, newCp, classnames);
        try {
            copier.memberValue(0);
            return new AnnotationDefaultAttribute(newCp, copier.close());
        }
        catch (Exception e) {
            throw new RuntimeException(e.toString());
        }
    }

    /**
     * Obtains the default value represented by this attribute.
     */
    public MemberValue getDefaultValue()
    {
       try {
           return new AnnotationsAttribute.Parser(info, constPool)
                                          .parseMemberValue();
       }
       catch (Exception e) {
           throw new RuntimeException(e.toString());
       }
    }

    /**
     * Changes the default value represented by this attribute.
     *
     * @param value         the new value.
     * @see javassist.bytecode.annotation.Annotation#createMemberValue(ConstPool, CtClass)
     */
    public void setDefaultValue(MemberValue value) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        AnnotationsWriter writer = new AnnotationsWriter(output, constPool);
        try {
            value.write(writer);
            writer.close();
        }
        catch (IOException e) {
            throw new RuntimeException(e);      // should never reach here.
        }

        set(output.toByteArray());
        
    }

    /**
     * Returns a string representation of this object.
     */
    @Override
    public String toString() {
        return getDefaultValue().toString();
    }
}
