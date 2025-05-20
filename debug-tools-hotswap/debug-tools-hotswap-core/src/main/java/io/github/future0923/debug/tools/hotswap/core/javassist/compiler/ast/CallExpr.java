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
import io.github.future0923.debug.tools.hotswap.core.javassist.compiler.MemberResolver;
import io.github.future0923.debug.tools.hotswap.core.javassist.compiler.TokenId;

/**
 * Method call expression.
 */
public class CallExpr extends Expr {
    /** default serialVersionUID */
    private static final long serialVersionUID = 1L;
    private MemberResolver.Method method;  // cached result of lookupMethod()

    private CallExpr(ASTree _head, ASTList _tail) {
        super(TokenId.CALL, _head, _tail);
        method = null;
    }

    public void setMethod(MemberResolver.Method m) {
        method = m;
    }

    public MemberResolver.Method getMethod() {
        return method;
    }

    public static CallExpr makeCall(ASTree target, ASTree args) {
        return new CallExpr(target, new ASTList(args));
    }

    @Override
    public void accept(Visitor v) throws CompileError { v.atCallExpr(this); }
}
