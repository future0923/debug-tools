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
package io.github.future0923.debug.tools.hotswap.core.javassist;


/**
 * Array types.
 */
final class CtArray extends CtClass
{
    protected ClassPool pool;

    // the name of array type ends with "[]".
    CtArray(String name, ClassPool cp)
    {
        super(name);
        pool = cp;
    }

    @Override
    public ClassPool getClassPool()
    {
        return pool;
    }

    @Override
    public boolean isArray()
    {
        return true;
    }

    private CtClass[] interfaces = null;

    @Override
    public int getModifiers()
    {
        int mod = Modifier.FINAL;
        try {
            mod |= getComponentType().getModifiers()
                   & (Modifier.PROTECTED | Modifier.PUBLIC | Modifier.PRIVATE);
        }
        catch (NotFoundException e) {}
        return mod;
    }

    @Override
    public CtClass[] getInterfaces() throws NotFoundException
    {
        if (interfaces == null) {
            Class<?>[] intfs = Object[].class.getInterfaces();
            // java.lang.Cloneable and java.io.Serializable.
            // If the JVM is CLDC, intfs is empty.
            interfaces = new CtClass[intfs.length];
            for (int i = 0; i < intfs.length; i++)
                interfaces[i] = pool.get(intfs[i].getName());
        }

        return interfaces;
    }

    @Override
    public boolean subtypeOf(CtClass clazz) throws NotFoundException
    {
        if (super.subtypeOf(clazz))
            return true;

        String cname = clazz.getName();
        if (cname.equals(javaLangObject))
            return true;

        CtClass[] intfs = getInterfaces();
        for (int i = 0; i < intfs.length; i++)
            if (intfs[i].subtypeOf(clazz))
                return true;

        return clazz.isArray()
            && getComponentType().subtypeOf(clazz.getComponentType());
    }

    @Override
    public CtClass getComponentType() throws NotFoundException
    {
        String name = getName();
        return pool.get(name.substring(0, name.length() - 2));
    }

    @Override
    public CtClass getSuperclass() throws NotFoundException
    {
        return pool.get(javaLangObject);
    }

    public String getSuperclassName() throws NotFoundException
    {
        CtClass superclass = getSuperclass();
        return superclass != null ? superclass.getName() : null;
    }

    @Override
    public CtMethod[] getMethods()
    {
        try {
            return getSuperclass().getMethods();
        }
        catch (NotFoundException e) {
            return super.getMethods();
        }
    }

    @Override
    public CtMethod getMethod(String name, String desc)
        throws NotFoundException
    {
        return getSuperclass().getMethod(name, desc);
    }

    @Override
    public CtConstructor[] getConstructors()
    {
        try {
            return getSuperclass().getConstructors();
        }
        catch (NotFoundException e) {
            return super.getConstructors();
        }
    }
}
