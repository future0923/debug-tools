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
package io.github.future0923.debug.tools.hotswap.core.javassist;

import io.github.future0923.debug.tools.hotswap.core.javassist.compiler.CompileError;

/**
 * Thrown when bytecode transformation has failed.
 */
public class CannotCompileException extends Exception {
    /** default serialVersionUID */
    private static final long serialVersionUID = 1L;
    private Throwable myCause;

    /**
     * Gets the cause of this throwable.
     * It is for JDK 1.3 compatibility.
     */
    @Override
    public synchronized Throwable getCause() {
        return (myCause == this ? null : myCause);
    }

    /**
     * Initializes the cause of this throwable.
     * It is for JDK 1.3 compatibility.
     */
    @Override
    public synchronized Throwable initCause(Throwable cause) {
        myCause = cause;
        return this;
    }

    private String message;

    /**
     * Gets a long message if it is available.
     */
    public String getReason() {
        if (message != null)
            return message;
        return this.toString();
    }

    /**
     * Constructs a CannotCompileException with a message.
     *
     * @param msg       the message.
     */
    public CannotCompileException(String msg) {
        super(msg);
        message = msg;
        initCause(null);
    }

    /**
     * Constructs a CannotCompileException with an <code>Exception</code>
     * representing the cause.
     *
     * @param e     the cause.
     */
    public CannotCompileException(Throwable e) {
        super("by " + e.toString());
        message = null;
        initCause(e);
    }

    /**
     * Constructs a CannotCompileException with a detailed message
     * and an <code>Exception</code> representing the cause.
     *
     * @param msg   the message.
     * @param e     the cause.
     */
    public CannotCompileException(String msg, Throwable e) {
        this(msg);
        initCause(e);
    }

    /**
     * Constructs a CannotCompileException with a
     * <code>NotFoundException</code>.
     */
    public CannotCompileException(NotFoundException e) {
        this("cannot find " + e.getMessage(), e);
    }

    /**
     * Constructs a CannotCompileException with an <code>CompileError</code>.
     */
    public CannotCompileException(CompileError e) {
        this("[source error] " + e.getMessage(), e);
    }

    /**
     * Constructs a CannotCompileException
     * with a <code>ClassNotFoundException</code>.
     */
    public CannotCompileException(ClassNotFoundException e, String name) {
        this("cannot find " + name, e);
    }

    /**
     * Constructs a CannotCompileException with a ClassFormatError.
     */
    public CannotCompileException(ClassFormatError e, String name) {
        this("invalid class format: " + name, e);
    }
}
