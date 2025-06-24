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
package io.github.future0923.debug.tools.hotswap.core.javassist.compiler;

import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.Bytecode;
import io.github.future0923.debug.tools.hotswap.core.javassist.compiler.ast.ASTList;

/**
 * An interface to an object for implementing $proceed().
 *
 * @see javassist.compiler.JvstCodeGen#setProceedHandler(ProceedHandler, String)
 */
public interface ProceedHandler {
    void doit(JvstCodeGen gen, Bytecode b, ASTList args) throws CompileError;
    void setReturnType(JvstTypeChecker c, ASTList args) throws CompileError;
}
