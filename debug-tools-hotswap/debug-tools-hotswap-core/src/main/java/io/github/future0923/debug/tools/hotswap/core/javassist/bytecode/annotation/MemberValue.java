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
 * The value of a member declared in an annotation.
 *
 * @see Annotation#getMemberValue(String)
 * @author <a href="mailto:bill@jboss.org">Bill Burke</a>
 * @author Shigeru Chiba
 */
public abstract class MemberValue {
    ConstPool cp;
    char tag;

    MemberValue(char tag, ConstPool cp) {
        this.cp = cp;
        this.tag = tag;
    }

    /**
     * Returns the value.  If the value type is a primitive type, the
     * returned value is boxed.
     */
    abstract Object getValue(ClassLoader cl, ClassPool cp, Method m)
        throws ClassNotFoundException;

    abstract Class<?> getType(ClassLoader cl) throws ClassNotFoundException;

    static Class<?> loadClass(ClassLoader cl, String classname)
        throws ClassNotFoundException, NoSuchClassError
    {
        try {
            return Class.forName(convertFromArray(classname), true, cl);
        }
        catch (LinkageError e) {
            throw new NoSuchClassError(classname, e);
        }
    }

    private static String convertFromArray(String classname)
    {
        int index = classname.indexOf("[]");
        if (index != -1) {
            String rawType = classname.substring(0, index);
            StringBuffer sb = new StringBuffer(Descriptor.of(rawType));
            while (index != -1) {
                sb.insert(0, "[");
                index = classname.indexOf("[]", index + 1);
            }
            return sb.toString().replace('/', '.');
        }
        return classname;
    }

    /**
     * Accepts a visitor.
     */
    public abstract void accept(MemberValueVisitor visitor);

    /**
     * Writes the value.
     */
    public abstract void write(AnnotationsWriter w) throws IOException;
}


