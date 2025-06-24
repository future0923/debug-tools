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
 * Integer constant.
 */
public class IntConst extends ASTree {
    /** default serialVersionUID */
    private static final long serialVersionUID = 1L;
    protected long value;
    protected int type;

    public IntConst(long v, int tokenId) { value = v; type = tokenId; }

    public long get() { return value; }

    public void set(long v) { value = v; }

    /* Returns IntConstant, CharConstant, or LongConstant.
     */
    public int getType() { return type; }

    @Override
    public String toString() { return Long.toString(value); }

    @Override
    public void accept(Visitor v) throws CompileError {
        v.atIntConst(this);
    }

    public ASTree compute(int op, ASTree right) {
        if (right instanceof IntConst)
            return compute0(op, (IntConst)right);
        else if (right instanceof DoubleConst)
            return compute0(op, (DoubleConst)right);
        else
            return null;
    }

    private IntConst compute0(int op, IntConst right) {
        int type1 = this.type;
        int type2 = right.type;
        int newType;
        if (type1 == TokenId.LongConstant || type2 == TokenId.LongConstant)
            newType = TokenId.LongConstant;
        else if (type1 == TokenId.CharConstant
                 && type2 == TokenId.CharConstant)
            newType = TokenId.CharConstant;
        else
            newType = TokenId.IntConstant;

        long value1 = this.value;
        long value2 = right.value;
        long newValue;
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
        case '|' :
            newValue = value1 | value2;
            break;
        case '^' :
            newValue = value1 ^ value2;
            break;
        case '&' :
            newValue = value1 & value2;
            break;
        case TokenId.LSHIFT :
            newValue = value << (int)value2;
            newType = type1;
            break;
        case TokenId.RSHIFT :
            newValue = value >> (int)value2;
            newType = type1;
            break;
        case TokenId.ARSHIFT :
            newValue = value >>> (int)value2;
            newType = type1;
            break;
        default :
            return null;
        }

        return new IntConst(newValue, newType);
    }

    private DoubleConst compute0(int op, DoubleConst right) {
        double value1 = this.value;
        double value2 = right.value;
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

        return new DoubleConst(newValue, right.type);
    }
}
