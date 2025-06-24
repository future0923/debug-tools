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
package io.github.future0923.debug.tools.hotswap.core.javassist.convert;

import io.github.future0923.debug.tools.hotswap.core.javassist.ClassPool;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtMethod;
import io.github.future0923.debug.tools.hotswap.core.javassist.Modifier;
import io.github.future0923.debug.tools.hotswap.core.javassist.NotFoundException;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.BadBytecode;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.CodeAttribute;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.CodeIterator;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.ConstPool;

public class TransformCall extends Transformer {
    protected String classname, methodname, methodDescriptor;
    protected String newClassname, newMethodname;
    protected boolean newMethodIsPrivate;

    /* cache */
    protected int newIndex;
    protected ConstPool constPool;

    public TransformCall(Transformer next, CtMethod origMethod,
                         CtMethod substMethod)
    {
        this(next, origMethod.getName(), substMethod);
        classname = origMethod.getDeclaringClass().getName();
    }

    public TransformCall(Transformer next, String oldMethodName,
                         CtMethod substMethod)
    {
        super(next);
        methodname = oldMethodName;
        methodDescriptor = substMethod.getMethodInfo2().getDescriptor();
        classname = newClassname = substMethod.getDeclaringClass().getName(); 
        newMethodname = substMethod.getName();
        constPool = null;
        newMethodIsPrivate = Modifier.isPrivate(substMethod.getModifiers());
    }

    @Override
    public void initialize(ConstPool cp, CodeAttribute attr) {
        if (constPool != cp)
            newIndex = 0;
    }

    /**
     * Modify INVOKEINTERFACE, INVOKESPECIAL, INVOKESTATIC and INVOKEVIRTUAL
     * so that a different method is invoked.  The class name in the operand
     * of these instructions might be a subclass of the target class specified
     * by <code>classname</code>.   This method transforms the instruction
     * in that case unless the subclass overrides the target method.
     */
    @Override
    public int transform(CtClass clazz, int pos, CodeIterator iterator,
                         ConstPool cp) throws BadBytecode
    {
        int c = iterator.byteAt(pos);
        if (c == INVOKEINTERFACE || c == INVOKESPECIAL
                        || c == INVOKESTATIC || c == INVOKEVIRTUAL) {
            int index = iterator.u16bitAt(pos + 1);
            String cname = cp.eqMember(methodname, methodDescriptor, index);
            if (cname != null && matchClass(cname, clazz.getClassPool())) {
                int ntinfo = cp.getMemberNameAndType(index);
                pos = match(c, pos, iterator,
                            cp.getNameAndTypeDescriptor(ntinfo), cp);
            }
        }

        return pos;
    }

    private boolean matchClass(String name, ClassPool pool) {
        if (classname.equals(name))
            return true;

        try {
            CtClass clazz = pool.get(name);
            CtClass declClazz = pool.get(classname);
            if (clazz.subtypeOf(declClazz))
                try {
                    CtMethod m = clazz.getMethod(methodname, methodDescriptor);
                    return m.getDeclaringClass().getName().equals(classname);
                }
                catch (NotFoundException e) {
                    // maybe the original method has been removed.
                    return true;
                }
        }
        catch (NotFoundException e) {
            return false;
        }

        return false;
    }

    protected int match(int c, int pos, CodeIterator iterator,
                        int typedesc, ConstPool cp) throws BadBytecode
    {
        if (newIndex == 0) {
            int nt = cp.addNameAndTypeInfo(cp.addUtf8Info(newMethodname),
                                           typedesc);
            int ci = cp.addClassInfo(newClassname);
            if (c == INVOKEINTERFACE)
                newIndex = cp.addInterfaceMethodrefInfo(ci, nt);
            else {
                if (newMethodIsPrivate && c == INVOKEVIRTUAL)
                    iterator.writeByte(INVOKESPECIAL, pos);

                newIndex = cp.addMethodrefInfo(ci, nt);
            }

            constPool = cp;
        }

        iterator.write16bit(newIndex, pos + 1);
        return pos;
    }
}
