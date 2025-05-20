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
