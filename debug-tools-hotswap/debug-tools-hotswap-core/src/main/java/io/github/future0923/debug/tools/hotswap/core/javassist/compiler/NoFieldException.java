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
package io.github.future0923.debug.tools.hotswap.core.javassist.compiler;

import io.github.future0923.debug.tools.hotswap.core.javassist.compiler.ast.ASTree;

public class NoFieldException extends CompileError {
    /** default serialVersionUID */
    private static final long serialVersionUID = 1L;
    private String fieldName;
    private ASTree expr;

    /* NAME must be JVM-internal representation.
     */
    public NoFieldException(String name, ASTree e) {
        super("no such field: " + name);
        fieldName = name;
        expr = e;
    }

    /* The returned name should be JVM-internal representation.
     */
    public String getField() { return fieldName; }

    /* Returns the expression where this exception is thrown.
     */
    public ASTree getExpr() { return expr; }
}
