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
import io.github.future0923.debug.tools.hotswap.core.javassist.Modifier;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.CodeAttribute;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.CodeIterator;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.ConstPool;

final public class TransformFieldAccess extends Transformer {
    private String newClassname, newFieldname;
    private String fieldname;
    private CtClass fieldClass;
    private boolean isPrivate;

    /* cache */
    private int newIndex;
    private ConstPool constPool;

    public TransformFieldAccess(Transformer next, CtField field,
                                String newClassname, String newFieldname)
    {
        super(next);
        this.fieldClass = field.getDeclaringClass();
        this.fieldname = field.getName();
        this.isPrivate = Modifier.isPrivate(field.getModifiers());
        this.newClassname = newClassname;
        this.newFieldname = newFieldname;
        this.constPool = null;
    }

    @Override
    public void initialize(ConstPool cp, CodeAttribute attr) {
        if (constPool != cp)
            newIndex = 0;
    }

    /**
     * Modify GETFIELD, GETSTATIC, PUTFIELD, and PUTSTATIC so that
     * a different field is accessed.  The new field must be declared
     * in a superclass of the class in which the original field is
     * declared.
     */
    @Override
    public int transform(CtClass clazz, int pos,
                         CodeIterator iterator, ConstPool cp)
    {
        int c = iterator.byteAt(pos);
        if (c == GETFIELD || c == GETSTATIC
                                || c == PUTFIELD || c == PUTSTATIC) {
            int index = iterator.u16bitAt(pos + 1);
            String typedesc
                = TransformReadField.isField(clazz.getClassPool(), cp,
                                fieldClass, fieldname, isPrivate, index);
            if (typedesc != null) {
                if (newIndex == 0) {
                    int nt = cp.addNameAndTypeInfo(newFieldname,
                                                   typedesc);
                    newIndex = cp.addFieldrefInfo(
                                        cp.addClassInfo(newClassname), nt);
                    constPool = cp;
                }

                iterator.write16bit(newIndex, pos + 1);
            }
        }

        return pos;
    }
}
