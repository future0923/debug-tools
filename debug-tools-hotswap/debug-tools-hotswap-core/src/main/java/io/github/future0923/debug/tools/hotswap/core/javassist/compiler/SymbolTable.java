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

import java.util.HashMap;

import io.github.future0923.debug.tools.hotswap.core.javassist.compiler.ast.Declarator;

public final class SymbolTable extends HashMap<String,Declarator> {
    /** default serialVersionUID */
    private static final long serialVersionUID = 1L;
    private SymbolTable parent;

    public SymbolTable() { this(null); }

    public SymbolTable(SymbolTable p) {
        super();
        parent = p;
    }

    public SymbolTable getParent() { return parent; }

    public Declarator lookup(String name) {
        Declarator found = get(name);
        if (found == null && parent != null)
            return parent.lookup(name);
        return found;
    }

    public void append(String name, Declarator value) {
        put(name, value);
    }
}
