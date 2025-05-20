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
