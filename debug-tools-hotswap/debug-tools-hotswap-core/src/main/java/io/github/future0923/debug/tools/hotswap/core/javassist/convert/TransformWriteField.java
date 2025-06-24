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
import io.github.future0923.debug.tools.hotswap.core.javassist.CtField;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.BadBytecode;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.CodeAttribute;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.CodeIterator;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.ConstPool;

final public class TransformWriteField extends TransformReadField {
    public TransformWriteField(Transformer next, CtField field,
                               String methodClassname, String methodName)
    {
        super(next, field, methodClassname, methodName);
    }

    @Override
    public int transform(CtClass tclazz, int pos, CodeIterator iterator,
                         ConstPool cp) throws BadBytecode
    {
        int c = iterator.byteAt(pos);
        if (c == PUTFIELD || c == PUTSTATIC) {
            int index = iterator.u16bitAt(pos + 1);
            String typedesc = isField(tclazz.getClassPool(), cp,
                                fieldClass, fieldname, isPrivate, index);
            if (typedesc != null) {
                if (c == PUTSTATIC) {
                    CodeAttribute ca = iterator.get();
                    iterator.move(pos);
                    char c0 = typedesc.charAt(0);
                    if (c0 == 'J' || c0 == 'D') {       // long or double
                        // insertGap() may insert 4 bytes.
                        pos = iterator.insertGap(3);
                        iterator.writeByte(ACONST_NULL, pos);
                        iterator.writeByte(DUP_X2, pos + 1);
                        iterator.writeByte(POP, pos + 2);
                        ca.setMaxStack(ca.getMaxStack() + 2);
                    }
                    else {
                        // insertGap() may insert 4 bytes.
                        pos = iterator.insertGap(2);
                        iterator.writeByte(ACONST_NULL, pos);
                        iterator.writeByte(SWAP, pos + 1);
                        ca.setMaxStack(ca.getMaxStack() + 1);
                    }

                    pos = iterator.next();
                }

                int mi = cp.addClassInfo(methodClassname);
                String type = "(Ljava/lang/Object;" + typedesc + ")V";
                int methodref = cp.addMethodrefInfo(mi, methodName, type);
                iterator.writeByte(INVOKESTATIC, pos);
                iterator.write16bit(methodref, pos + 1);
            }
        }

        return pos;
    }
}
