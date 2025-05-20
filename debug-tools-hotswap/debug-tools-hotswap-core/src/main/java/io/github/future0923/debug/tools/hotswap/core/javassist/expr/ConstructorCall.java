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
package io.github.future0923.debug.tools.hotswap.core.javassist.expr;

import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtConstructor;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtMethod;
import io.github.future0923.debug.tools.hotswap.core.javassist.NotFoundException;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.CodeIterator;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.MethodInfo;

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
