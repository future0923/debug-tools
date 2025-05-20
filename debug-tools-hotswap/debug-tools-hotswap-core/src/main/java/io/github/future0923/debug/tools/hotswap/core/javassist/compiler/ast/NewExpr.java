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
 * New Expression.
 */
public class NewExpr extends ASTList implements TokenId {
    /** default serialVersionUID */
    private static final long serialVersionUID = 1L;
    protected boolean newArray;
    protected int arrayType;

    public NewExpr(ASTList className, ASTList args) {
        super(className, new ASTList(args));
        newArray = false;
        arrayType = CLASS;
    }

    public NewExpr(int type, ASTList arraySize, ArrayInit init) {
        super(null, new ASTList(arraySize));
        newArray = true;
        arrayType = type;
        if (init != null)
            append(this, init);
    }

    public static NewExpr makeObjectArray(ASTList className,
                                          ASTList arraySize, ArrayInit init) {
        NewExpr e = new NewExpr(className, arraySize);
        e.newArray = true;
        if (init != null)
            append(e, init);

        return e;
    }

    public boolean isArray() { return newArray; }

    /* TokenId.CLASS, TokenId.INT, ...
     */
    public int getArrayType() { return arrayType; }

    public ASTList getClassName() { return (ASTList)getLeft(); }

    public ASTList getArguments() { return (ASTList)getRight().getLeft(); }

    public ASTList getArraySize() { return getArguments(); }

    public ArrayInit getInitializer() {
        ASTree t = getRight().getRight();
        if (t == null)
            return null;
        return (ArrayInit)t.getLeft();
    }

    @Override
    public void accept(Visitor v) throws CompileError { v.atNewExpr(this); }

    @Override
    protected String getTag() {
        return newArray ? "new[]" : "new";
    }
}
