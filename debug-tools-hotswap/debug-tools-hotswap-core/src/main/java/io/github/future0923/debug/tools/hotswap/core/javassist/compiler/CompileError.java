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

import io.github.future0923.debug.tools.hotswap.core.javassist.CannotCompileException;
import io.github.future0923.debug.tools.hotswap.core.javassist.NotFoundException;

public class CompileError extends Exception {
    /** default serialVersionUID */
    private static final long serialVersionUID = 1L;
    private Lex lex;
    private String reason;

    public CompileError(String s, Lex l) {
        reason = s;
        lex = l;
    }

    public CompileError(String s) {
        reason = s;
        lex = null;
    }

    public CompileError(CannotCompileException e) {
        this(e.getReason());
    }

    public CompileError(NotFoundException e) {
        this("cannot find " + e.getMessage());
    }

    public Lex getLex() { return lex; }

    @Override
    public String getMessage() {
        return reason;
    }

    @Override
    public String toString() {
        return "compile error: " + reason;
    }
}
