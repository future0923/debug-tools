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
package io.github.future0923.debug.tools.hotswap.core.javassist;

import java.io.DataOutputStream;
import java.io.IOException;

import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.ClassFile;

class CtNewClass extends CtClassType {
    /* true if the class is an interface.
     */
    protected boolean hasConstructor;

    CtNewClass(String name, ClassPool cp,
               boolean isInterface, CtClass superclass) {
        super(name, cp);
        wasChanged = true;
        String superName;
        if (isInterface || superclass == null)
            superName = null;
        else
            superName = superclass.getName();

        classfile = new ClassFile(isInterface, name, superName);
        if (isInterface && superclass != null)
            classfile.setInterfaces(new String[] { superclass.getName() });

        setModifiers(Modifier.setPublic(getModifiers()));
        hasConstructor = isInterface;
    }

    @Override
    protected void extendToString(StringBuffer buffer) {
        if (hasConstructor)
            buffer.append("hasConstructor ");

        super.extendToString(buffer);
    }

    @Override
    public void addConstructor(CtConstructor c)
        throws CannotCompileException
    {
        hasConstructor = true;
        super.addConstructor(c);
    }

    @Override
    public void toBytecode(DataOutputStream out)
        throws CannotCompileException, IOException
    {
        if (!hasConstructor)
            try {
                inheritAllConstructors();
                hasConstructor = true;
            }
            catch (NotFoundException e) {
                throw new CannotCompileException(e);
            }

        super.toBytecode(out);
    }

    /**
     * Adds constructors inhrited from the super class.
     *
     * <p>After this method is called, the class inherits all the
     * constructors from the super class.  The added constructor
     * calls the super's constructor with the same signature.
     */
    public void inheritAllConstructors()
        throws CannotCompileException, NotFoundException
    {
        CtClass superclazz;
        CtConstructor[] cs;

        superclazz = getSuperclass();
        cs = superclazz.getDeclaredConstructors();

        int n = 0;
        for (int i = 0; i < cs.length; ++i) {
            CtConstructor c = cs[i];
            int mod = c.getModifiers();
            if (isInheritable(mod, superclazz)) {
                CtConstructor cons
                    = CtNewConstructor.make(c.getParameterTypes(),
                                            c.getExceptionTypes(), this);
                cons.setModifiers(mod & (Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE));
                addConstructor(cons);
                ++n;
            }
        }

        if (n < 1)
            throw new CannotCompileException(
                        "no inheritable constructor in " + superclazz.getName());

    }

    private boolean isInheritable(int mod, CtClass superclazz) {
        if (Modifier.isPrivate(mod))
            return false;

        if (Modifier.isPackage(mod)) {
            String pname = getPackageName();
            String pname2 = superclazz.getPackageName();
            if (pname == null)
                return pname2 == null;
            return pname.equals(pname2);
        }

        return true;
    }
}
