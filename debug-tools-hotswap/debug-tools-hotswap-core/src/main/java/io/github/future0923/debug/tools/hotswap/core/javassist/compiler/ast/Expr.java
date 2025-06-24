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
import io.github.future0923.debug.tools.hotswap.core.javassist.compiler.TokenId;

/**
 * Expression.
 */
public class Expr extends ASTList implements TokenId {
    /* operator must be either of:
     * (unary) +, (unary) -, ++, --, !, ~,
     * ARRAY, . (dot), MEMBER (static member access).
     * Otherwise, the object should be an instance of a subclass.
     */

    /** default serialVersionUID */
    private static final long serialVersionUID = 1L;
    protected int operatorId;

    Expr(int op, ASTree _head, ASTList _tail) {
        super(_head, _tail);
        operatorId = op;
    }

    Expr(int op, ASTree _head) {
        super(_head);
        operatorId = op;
    }

    public static Expr make(int op, ASTree oprand1, ASTree oprand2) {
        return new Expr(op, oprand1, new ASTList(oprand2));
    }

    public static Expr make(int op, ASTree oprand1) {
        return new Expr(op, oprand1);
    }

    public int getOperator() { return operatorId; }

    public void setOperator(int op) { operatorId = op; }

    public ASTree oprand1() { return getLeft(); }

    public void setOprand1(ASTree expr) {
        setLeft(expr);
    }

    public ASTree oprand2() { return getRight().getLeft(); }

    public void setOprand2(ASTree expr) {
        getRight().setLeft(expr);
    }

    @Override
    public void accept(Visitor v) throws CompileError { v.atExpr(this); }

    public String getName() {
        int id = operatorId;
        if (id < 128)
            return String.valueOf((char)id);
        else if (NEQ <= id && id <= ARSHIFT_E)
            return opNames[id - NEQ];
        else if (id == INSTANCEOF)
            return "instanceof";
        else
            return String.valueOf(id);
    }

    @Override
    protected String getTag() {
        return "op:" + getName();
    }
}
