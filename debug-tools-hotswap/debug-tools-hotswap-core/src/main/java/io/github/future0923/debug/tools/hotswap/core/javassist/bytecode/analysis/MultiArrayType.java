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
