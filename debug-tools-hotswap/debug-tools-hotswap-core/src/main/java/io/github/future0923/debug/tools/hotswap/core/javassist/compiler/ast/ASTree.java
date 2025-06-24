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
