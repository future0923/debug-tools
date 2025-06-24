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

/**
 * A node of a a binary tree.  This class provides concrete methods
 * overriding abstract methods in ASTree.
 */
public class Pair extends ASTree {
    /** default serialVersionUID */
    private static final long serialVersionUID = 1L;
    protected ASTree left, right;

    public Pair(ASTree _left, ASTree _right) {
        left = _left;
        right = _right;
    }

    @Override
    public void accept(Visitor v) throws CompileError { v.atPair(this); }

    @Override
    public String toString() {
        StringBuffer sbuf = new StringBuffer();
        sbuf.append("(<Pair> ");
        sbuf.append(left == null ? "<null>" : left.toString());
        sbuf.append(" . ");
        sbuf.append(right == null ? "<null>" : right.toString());
        sbuf.append(')');
        return sbuf.toString();
    }

    @Override
    public ASTree getLeft() { return left; }

    @Override
    public ASTree getRight() { return right; }

    @Override
    public void setLeft(ASTree _left) { left = _left; }

    @Override
    public void setRight(ASTree _right) { right = _right; }
}
