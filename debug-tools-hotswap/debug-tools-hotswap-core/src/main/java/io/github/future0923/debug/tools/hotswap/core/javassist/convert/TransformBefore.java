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

import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtMethod;
import io.github.future0923.debug.tools.hotswap.core.javassist.NotFoundException;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.BadBytecode;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.Bytecode;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.CodeAttribute;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.CodeIterator;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.ConstPool;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.Descriptor;

public class TransformBefore extends TransformCall {
    protected CtClass[] parameterTypes;
    protected int locals;
    protected int maxLocals;
    protected byte[] saveCode, loadCode;

    public TransformBefore(Transformer next,
                           CtMethod origMethod, CtMethod beforeMethod)
        throws NotFoundException
    {
        super(next, origMethod, beforeMethod);

        // override
        methodDescriptor = origMethod.getMethodInfo2().getDescriptor();

        parameterTypes = origMethod.getParameterTypes();
        locals = 0;
        maxLocals = 0;
        saveCode = loadCode = null;
    }

    @Override
    public void initialize(ConstPool cp, CodeAttribute attr) {
        super.initialize(cp, attr);
        locals = 0;
        maxLocals = attr.getMaxLocals();
        saveCode = loadCode = null;
    }

    @Override
    protected int match(int c, int pos, CodeIterator iterator,
                        int typedesc, ConstPool cp) throws BadBytecode
    {
        if (newIndex == 0) {
            String desc = Descriptor.ofParameters(parameterTypes) + 'V';
            desc = Descriptor.insertParameter(classname, desc);
            int nt = cp.addNameAndTypeInfo(newMethodname, desc);
            int ci = cp.addClassInfo(newClassname);
            newIndex = cp.addMethodrefInfo(ci, nt);
            constPool = cp;
        }

        if (saveCode == null)
            makeCode(parameterTypes, cp);

        return match2(pos, iterator);
    }

    protected int match2(int pos, CodeIterator iterator) throws BadBytecode {
        iterator.move(pos);
        iterator.insert(saveCode);
        iterator.insert(loadCode);
        int p = iterator.insertGap(3);
        iterator.writeByte(INVOKESTATIC, p);
        iterator.write16bit(newIndex, p + 1);
        iterator.insert(loadCode);
        return iterator.next();
    }

    @Override
    public int extraLocals() { return locals; }

    protected void makeCode(CtClass[] paramTypes, ConstPool cp) {
        Bytecode save = new Bytecode(cp, 0, 0);
        Bytecode load = new Bytecode(cp, 0, 0);

        int var = maxLocals;
        int len = (paramTypes == null) ? 0 : paramTypes.length;
        load.addAload(var);
        makeCode2(save, load, 0, len, paramTypes, var + 1);
        save.addAstore(var);

        saveCode = save.get();
        loadCode = load.get();
    }

    private void makeCode2(Bytecode save, Bytecode load,
                           int i, int n, CtClass[] paramTypes, int var)
    {
        if (i < n) {
            int size = load.addLoad(var, paramTypes[i]);
            makeCode2(save, load, i + 1, n, paramTypes, var + size);
            save.addStore(var, paramTypes[i]);
        }
        else
            locals = var - maxLocals;
    }
}
