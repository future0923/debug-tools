/*
 * Javassist, a Java-bytecode translator toolkit.
 * Copyright (C) 1999- Shigeru Chiba. All Rights Reserved.
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License.  Alternatively, the contents of this file may be used under
 * the terms of the GNU Lesser General Public License Version 2.1 or later,
 * or the Apache License Version 2.0.
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 */

package io.github.future0923.debug.tools.hotswap.core.javassist.expr;

import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtConstructor;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtMethod;
import io.github.future0923.debug.tools.hotswap.core.javassist.NotFoundException;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.CodeIterator;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.MethodInfo;
import io.github.future0923.debug.tools.hotswap.core.javassist.expr.NewExpr;

/**
 * Constructor call such as <code>this()</code> and <code>super()</code>
 * within a constructor body.
 *
 * @see NewExpr
 */
public class ConstructorCall extends MethodCall {
    /**
     * Undocumented constructor.  Do not use; internal-use only.
     */
    protected ConstructorCall(int pos, CodeIterator i, CtClass decl, MethodInfo m) {
        super(pos, i, decl, m);
    }

    /**
     * Returns <code>"super"</code> or "<code>"this"</code>.
     */
    @Override
    public String getMethodName() {
        return isSuper() ? "super" : "this";
    }

    /**
     * Always throws a <code>NotFoundException</code>.
     *
     * @see #getConstructor()
     */
    @Override
    public CtMethod getMethod() throws NotFoundException {
        throw new NotFoundException("this is a constructor call.  Call getConstructor().");
    }

    /**
     * Returns the called constructor.
     */
    public CtConstructor getConstructor() throws NotFoundException {
        return getCtClass().getConstructor(getSignature());
    }

    /**
     * Returns true if the called constructor is not <code>this()</code>
     * but <code>super()</code> (a constructor declared in the super class).
     */
    @Override
    public boolean isSuper() {
        return super.isSuper();
    }
}
