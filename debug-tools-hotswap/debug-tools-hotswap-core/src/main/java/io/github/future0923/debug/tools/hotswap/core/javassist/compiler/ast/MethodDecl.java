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

public class MethodDecl extends ASTList {
    /** default serialVersionUID */
    private static final long serialVersionUID = 1L;
    public static final String initName = "<init>";

    public MethodDecl(ASTree _head, ASTList _tail) {
        super(_head, _tail);
    }

    public boolean isConstructor() {
        Symbol sym = getReturn().getVariable();
        return sym != null && initName.equals(sym.get());
    }

    public ASTList getModifiers() { return (ASTList)getLeft(); }

    public Declarator getReturn() { return (Declarator)tail().head(); }

    public ASTList getParams() { return (ASTList)sublist(2).head(); }

    public ASTList getThrows() { return (ASTList)sublist(3).head(); }

    public Stmnt getBody() { return (Stmnt)sublist(4).head(); }

    @Override
    public void accept(Visitor v) throws CompileError {
        v.atMethodDecl(this);
    }
}
