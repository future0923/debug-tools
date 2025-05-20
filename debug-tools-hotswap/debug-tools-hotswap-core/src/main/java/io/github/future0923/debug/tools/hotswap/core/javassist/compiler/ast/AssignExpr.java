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
package io.github.future0923.debug.tools.hotswap.core.javassist.compiler.ast;

import io.github.future0923.debug.tools.hotswap.core.javassist.compiler.CompileError;

/**
 * Assignment expression.
 */
public class AssignExpr extends Expr {
    /* operator must be either of:
     * =, %=, &=, *=, +=, -=, /=, ^=, |=, <<=, >>=, >>>=
     */

    /** default serialVersionUID */
    private static final long serialVersionUID = 1L;

    private AssignExpr(int op, ASTree _head, ASTList _tail) {
        super(op, _head, _tail);
    }

    public static AssignExpr makeAssign(int op, ASTree oprand1,
                                        ASTree oprand2) {
        return new AssignExpr(op, oprand1, new ASTList(oprand2));
    }

    @Override
    public void accept(Visitor v) throws CompileError {
        v.atAssignExpr(this);
    }
}
