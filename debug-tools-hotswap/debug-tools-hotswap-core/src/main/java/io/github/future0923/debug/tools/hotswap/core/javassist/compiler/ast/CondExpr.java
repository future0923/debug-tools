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
 * Conditional expression.
 */
public class CondExpr extends ASTList {
    /** default serialVersionUID */
    private static final long serialVersionUID = 1L;

    public CondExpr(ASTree cond, ASTree thenp, ASTree elsep) {
        super(cond, new ASTList(thenp, new ASTList(elsep)));
    }

    public ASTree condExpr() { return head(); }

    public void setCond(ASTree t) { setHead(t); }

    public ASTree thenExpr() { return tail().head(); }

    public void setThen(ASTree t) { tail().setHead(t); } 

    public ASTree elseExpr() { return tail().tail().head(); }

    public void setElse(ASTree t) { tail().tail().setHead(t); } 

    @Override
    public String getTag() { return "?:"; }

    @Override
    public void accept(Visitor v) throws CompileError { v.atCondExpr(this); }
}
