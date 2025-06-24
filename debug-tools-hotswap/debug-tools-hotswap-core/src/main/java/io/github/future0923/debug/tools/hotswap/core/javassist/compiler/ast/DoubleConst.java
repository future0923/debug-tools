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
 * Double constant.
 */
public class DoubleConst extends ASTree {
    /** default serialVersionUID */
    private static final long serialVersionUID = 1L;
    protected double value;
    protected int type;

    public DoubleConst(double v, int tokenId) { value = v; type = tokenId; }

    public double get() { return value; }

    public void set(double v) { value = v; }

    /* Returns DoubleConstant or FloatConstant
     */
    public int getType() { return type; }

    @Override
    public String toString() { return Double.toString(value); }

    @Override
    public void accept(Visitor v) throws CompileError {
        v.atDoubleConst(this);
    }

    public ASTree compute(int op, ASTree right) {
        if (right instanceof IntConst)
            return compute0(op, (IntConst)right);
        else if (right instanceof DoubleConst)
            return compute0(op, (DoubleConst)right);
        else
            return null;
    }

    private DoubleConst compute0(int op, DoubleConst right) {
        int newType;
        if (this.type == TokenId.DoubleConstant
            || right.type == TokenId.DoubleConstant)
            newType = TokenId.DoubleConstant;
        else
            newType = TokenId.FloatConstant;

        return compute(op, this.value, right.value, newType);
    }

    private DoubleConst compute0(int op, IntConst right) {
        return compute(op, this.value, right.value, this.type);
    }

    private static DoubleConst compute(int op, double value1, double value2,
                                       int newType)
    {
        double newValue;
        switch (op) {
        case '+' :
            newValue = value1 + value2;
            break;
        case '-' :
            newValue = value1 - value2;
            break;
        case '*' :
            newValue = value1 * value2;
            break;
        case '/' :
            newValue = value1 / value2;
            break;
        case '%' :
            newValue = value1 % value2;
            break;
        default :
            return null;
        }

        return new DoubleConst(newValue, newType);
    }
}
