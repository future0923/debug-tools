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

import io.github.future0923.debug.tools.hotswap.core.javassist.CannotCompileException;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.BadBytecode;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.CodeAttribute;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.CodeIterator;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.ConstPool;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.MethodInfo;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.Opcode;

/**
 * Transformer and its subclasses are used for executing
 * code transformation specified by CodeConverter.
 *
 * @see javassist.CodeConverter
 */
public abstract class Transformer implements Opcode {
    private Transformer next;

    public Transformer(Transformer t) {
        next = t;
    }

    public Transformer getNext() { return next; }

    public void initialize(ConstPool cp, CodeAttribute attr) {}
    
    public void initialize(ConstPool cp, CtClass clazz, MethodInfo minfo) throws CannotCompileException { 
    	initialize(cp, minfo.getCodeAttribute());
    }

    public void clean() {}

    public abstract int transform(CtClass clazz, int pos, CodeIterator it,
                ConstPool cp) throws CannotCompileException, BadBytecode;

    public int extraLocals() { return 0; }

    public int extraStack() { return 0; }
}
