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
 * Statement.
 */
public class Stmnt extends ASTList implements TokenId {
    /** default serialVersionUID */
    private static final long serialVersionUID = 1L;
    protected int operatorId;

    public Stmnt(int op, ASTree _head, ASTList _tail) {
        super(_head, _tail);
        operatorId = op;
    }

    public Stmnt(int op, ASTree _head) {
        super(_head);
        operatorId = op;
    }

    public Stmnt(int op) {
        this(op, null);
    }

    public static Stmnt make(int op, ASTree oprand1, ASTree oprand2) {
        return new Stmnt(op, oprand1, new ASTList(oprand2));
    }

    public static Stmnt make(int op, ASTree op1, ASTree op2, ASTree op3) {
        return new Stmnt(op, op1, new ASTList(op2, new ASTList(op3)));
    }

    @Override
    public void accept(Visitor v) throws CompileError { v.atStmnt(this); }

    public int getOperator() { return operatorId; }

    @Override
    protected String getTag() {
        if (operatorId < 128)
            return "stmnt:" + (char)operatorId;
        return "stmnt:" + operatorId;
    }
}
