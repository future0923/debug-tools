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

import java.io.Serializable;

import io.github.future0923.debug.tools.hotswap.core.javassist.compiler.CompileError;

/**
 * Abstract Syntax Tree.  An ASTree object represents a node of
 * a binary tree.  If the node is a leaf node, both <code>getLeft()</code>
 * and <code>getRight()</code> returns null.
 */
public abstract class ASTree implements Serializable {
    /** default serialVersionUID */
    private static final long serialVersionUID = 1L;

    public ASTree getLeft() { return null; }

    public ASTree getRight() { return null; }

    public void setLeft(ASTree _left) {}

    public void setRight(ASTree _right) {}

    /**
     * Is a method for the visitor pattern.  It calls
     * <code>atXXX()</code> on the given visitor, where
     * <code>XXX</code> is the class name of the node object.
     */
    public abstract void accept(Visitor v) throws CompileError;

    @Override
    public String toString() {
        StringBuffer sbuf = new StringBuffer();
        sbuf.append('<');
        sbuf.append(getTag());
        sbuf.append('>');
        return sbuf.toString();
    }

    /**
     * Returns the type of this node.  This method is used by
     * <code>toString()</code>.
     */
    protected String getTag() {
        String name = getClass().getName();
        return name.substring(name.lastIndexOf('.') + 1);
    }
}
