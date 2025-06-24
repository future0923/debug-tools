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
 * Cast expression.
 */
public class CastExpr extends ASTList implements TokenId {
    /** default serialVersionUID */
    private static final long serialVersionUID = 1L;
    protected int castType;
    protected int arrayDim;

    public CastExpr(ASTList className, int dim, ASTree expr) {
        super(className, new ASTList(expr));
        castType = CLASS;
        arrayDim = dim;
    }

    public CastExpr(int type, int dim, ASTree expr) {
        super(null, new ASTList(expr));
        castType = type;
        arrayDim = dim;
    }

    /* Returns CLASS, BOOLEAN, INT, or ...
     */
    public int getType() { return castType; }

    public int getArrayDim() { return arrayDim; }

    public ASTList getClassName() { return (ASTList)getLeft(); }

    public ASTree getOprand() { return getRight().getLeft(); }

    public void setOprand(ASTree t) { getRight().setLeft(t); }

    @Override
    public String getTag() { return "cast:" + castType + ":" + arrayDim; }

    @Override
    public void accept(Visitor v) throws CompileError { v.atCastExpr(this); }
}
