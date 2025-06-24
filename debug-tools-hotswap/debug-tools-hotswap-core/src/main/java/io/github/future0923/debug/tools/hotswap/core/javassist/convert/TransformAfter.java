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

import io.github.future0923.debug.tools.hotswap.core.javassist.CtMethod;
import io.github.future0923.debug.tools.hotswap.core.javassist.NotFoundException;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.BadBytecode;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.CodeIterator;

public class TransformAfter extends TransformBefore {
    public TransformAfter(Transformer next,
                          CtMethod origMethod, CtMethod afterMethod)
        throws NotFoundException
    {
        super(next, origMethod, afterMethod);
    }

    @Override
    protected int match2(int pos, CodeIterator iterator) throws BadBytecode {
        iterator.move(pos);
        iterator.insert(saveCode);
        iterator.insert(loadCode);
        int p = iterator.insertGap(3);
        iterator.setMark(p);
        iterator.insert(loadCode);
        pos = iterator.next();
        p = iterator.getMark();
        iterator.writeByte(iterator.byteAt(pos), p);
        iterator.write16bit(iterator.u16bitAt(pos + 1), p + 1);
        iterator.writeByte(INVOKESTATIC, pos);
        iterator.write16bit(newIndex, pos + 1);
        iterator.move(p);
        return iterator.next();
    }
}
