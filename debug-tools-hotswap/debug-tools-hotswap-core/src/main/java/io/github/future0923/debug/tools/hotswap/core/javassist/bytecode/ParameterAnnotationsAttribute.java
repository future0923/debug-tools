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
import java.util.HashMap;
import java.util.Map;

import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.AnnotationsAttribute.Copier;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.AnnotationsAttribute.Parser;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.AnnotationsAttribute.Renamer;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.annotation.Annotation;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.annotation.AnnotationsWriter;

/**
 * A class representing <code>RuntimeVisibleAnnotations_attribute</code> and
 * <code>RuntimeInvisibleAnnotations_attribute</code>.
 *
 * <p>To obtain an ParameterAnnotationAttribute object, invoke
 * <code>getAttribute(ParameterAnnotationsAttribute.invisibleTag)</code>
 * in <code>MethodInfo</code>.
 * The obtained attribute is a
 * runtime invisible annotations attribute.  
 * If the parameter is
 * <code>ParameterAnnotationAttribute.visibleTag</code>, then the obtained
 * attribute is a runtime visible one.
 */
public class ParameterAnnotationsAttribute extends AttributeInfo {
    /**
     * The name of the <code>RuntimeVisibleParameterAnnotations</code>
     * attribute.
     */
    public static final String visibleTag
        = "RuntimeVisibleParameterAnnotations";

    /**
     * The name of the <code>RuntimeInvisibleParameterAnnotations</code>
     * attribute.
     */
    public static final String invisibleTag
        = "RuntimeInvisibleParameterAnnotations";
    /**
     * Constructs
     * a <code>Runtime(In)VisibleParameterAnnotations_attribute</code>.
     *
     * @param cp            constant pool
     * @param attrname      attribute name (<code>visibleTag</code> or
     *                      <code>invisibleTag</code>).
     * @param info          the contents of this attribute.  It does not
     *                      include <code>attribute_name_index</code> or
     *                      <code>attribute_length</code>.
     */
    public ParameterAnnotationsAttribute(ConstPool cp, String attrname,
                                         byte[] info) {
        super(cp, attrname, info);
    }

    /**
     * Constructs an empty
     * <code>Runtime(In)VisibleParameterAnnotations_attribute</code>.
     * A new annotation can be later added to the created attribute
     * by <code>setAnnotations()</code>.
     *
     * @param cp            constant pool
     * @param attrname      attribute name (<code>visibleTag</code> or
     *                      <code>invisibleTag</code>).
     * @see #setAnnotations(Annotation[][])
     */
    public ParameterAnnotationsAttribute(ConstPool cp, String attrname) {
        this(cp, attrname, new byte[] { 0 });
    }

    /**
     * @param n     the attribute name.
     */
    ParameterAnnotationsAttribute(ConstPool cp, int n, DataInputStream in)
        throws IOException
    {
        super(cp, n, in);
    }

    /**
     * Returns <code>num_parameters</code>. 
     */
    public int numParameters() {
        return info[0] & 0xff;
    }

    /**
     * Copies this attribute and returns a new copy.
     */
    @Override
    public AttributeInfo copy(ConstPool newCp, Map<String,String> classnames) {
        Copier copier = new Copier(info, constPool, newCp, classnames);
        try {
            copier.parameters();
            return new ParameterAnnotationsAttribute(newCp, getName(),
                                                     copier.close());
        }
        catch (Exception e) {
            throw new RuntimeException(e.toString());
        }
    }

    /**
     * Parses the annotations and returns a data structure representing
     * that parsed annotations.  Note that changes of the node values of the
     * returned tree are not reflected on the annotations represented by
     * this object unless the tree is copied back to this object by
     * <code>setAnnotations()</code>.
     *
     * @return Each element of the returned array represents an array of
     * annotations that are associated with each method parameter.
     *      
     * @see #setAnnotations(Annotation[][])
     */
    public Annotation[][] getAnnotations() {
        try {
            return new Parser(info, constPool).parseParameters();
        }
        catch (Exception e) {
            throw new RuntimeException(e.toString());
        }
    }

    /**
     * Changes the annotations represented by this object according to
     * the given array of <code>Annotation</code> objects.
     *
     * @param params        the data structure representing the
     *                      new annotations. Every element of this array
     *                      is an array of <code>Annotation</code> and
     *                      it represens annotations of each method parameter.
     */
    public void setAnnotations(Annotation[][] params) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        AnnotationsWriter writer = new AnnotationsWriter(output, constPool);
        try {
            writer.numParameters(params.length);
            for (Annotation[] anno:params) {
                writer.numAnnotations(anno.length);
                for (int j = 0; j < anno.length; ++j)
                    anno[j].write(writer);
            }

            writer.close();
        }
        catch (IOException e) {
            throw new RuntimeException(e);      // should never reach here.
        }

        set(output.toByteArray());
    }

    /**
     * @param oldname       a JVM class name.
     * @param newname       a JVM class name.
     */
    @Override
    void renameClass(String oldname, String newname) {
        Map<String,String> map = new HashMap<String,String>();
        map.put(oldname, newname);
        renameClass(map);
    }

    @Override
    void renameClass(Map<String,String> classnames) {
        Renamer renamer = new Renamer(info, getConstPool(), classnames);
        try {
            renamer.parameters();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    void getRefClasses(Map<String,String> classnames) { renameClass(classnames); }

    /**
     * Returns a string representation of this object.
     */
    @Override
    public String toString() {
        Annotation[][] aa = getAnnotations();
        StringBuilder sbuf = new StringBuilder();
        for (Annotation[] a : aa) {
            for (Annotation i : a)
                sbuf.append(i.toString()).append(" ");

            sbuf.append(", ");
        }

        return sbuf.toString().replaceAll(" (?=,)|, $","");
    }
}
