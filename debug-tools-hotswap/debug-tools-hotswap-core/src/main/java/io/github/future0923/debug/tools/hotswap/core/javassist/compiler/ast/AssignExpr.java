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
