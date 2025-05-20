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
