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
package io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.analysis;

import io.github.future0923.debug.tools.hotswap.core.javassist.ClassPool;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.javassist.NotFoundException;

/**
 * Represents an array of {@link MultiType} instances.
 *
 * @author Jason T. Greene
 */
public class MultiArrayType extends Type {
    private MultiType component;
    private int dims;

    public MultiArrayType(MultiType component, int dims) {
        super(null);
        this.component = component;
        this.dims = dims;
    }

    @Override
    public CtClass getCtClass() {
        CtClass clazz = component.getCtClass();
        if (clazz == null)
            return null;

        ClassPool pool = clazz.getClassPool();
        if (pool == null)
            pool = ClassPool.getDefault();

        String name = arrayName(clazz.getName(), dims);

        try {
            return pool.get(name);
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    boolean popChanged() {
        return component.popChanged();
    }

    @Override
    public int getDimensions() {
        return dims;
    }

    @Override
    public Type getComponent() {
       return dims == 1 ? (Type)component : new MultiArrayType(component, dims - 1);
    }

    @Override
    public int getSize() {
        return 1;
    }

    @Override
    public boolean isArray() {
        return true;
    }

    @Override
    public boolean isAssignableFrom(Type type) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean isReference() {
       return true;
    }

    public boolean isAssignableTo(Type type) {
        if (eq(type.getCtClass(), Type.OBJECT.getCtClass()))
            return true;

        if (eq(type.getCtClass(), Type.CLONEABLE.getCtClass()))
            return true;

        if (eq(type.getCtClass(), Type.SERIALIZABLE.getCtClass()))
            return true;

        if (! type.isArray())
            return false;

        Type typeRoot = getRootComponent(type);
        int typeDims = type.getDimensions();

        if (typeDims > dims)
            return false;

        if (typeDims < dims) {
            if (eq(typeRoot.getCtClass(), Type.OBJECT.getCtClass()))
                return true;

            if (eq(typeRoot.getCtClass(), Type.CLONEABLE.getCtClass()))
                return true;

            if (eq(typeRoot.getCtClass(), Type.SERIALIZABLE.getCtClass()))
                return true;

            return false;
        }

        return component.isAssignableTo(typeRoot);
    }


    @Override
    public int hashCode() {
        return component.hashCode() + dims;
    }

    @Override
    public boolean equals(Object o) {
        if (! (o instanceof MultiArrayType))
            return false;
        MultiArrayType multi = (MultiArrayType)o;

        return component.equals(multi.component) && dims == multi.dims;
    }

    @Override
    public String toString() {
        // follows the same detailed formating scheme as component
        return arrayName(component.toString(), dims);
    }
}
