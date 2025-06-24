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
package io.github.future0923.debug.tools.hotswap.core.javassist.tools.reflect;

import java.lang.reflect.InvocationTargetException;

/**
 * Thrown when method invocation using the reflection API has thrown
 * an exception.
 *
 * @see javassist.tools.reflect.Metaobject#trapMethodcall(int, Object[])
 * @see javassist.tools.reflect.ClassMetaobject#trapMethodcall(int, Object[])
 * @see javassist.tools.reflect.ClassMetaobject#invoke(Object, int, Object[])
 */
public class CannotInvokeException extends RuntimeException {

    /** default serialVersionUID */
    private static final long serialVersionUID = 1L;
    private Throwable err = null;

    /**
     * Returns the cause of this exception.  It may return null.
     */
    public Throwable getReason() { return err; }

    /**
     * Constructs a CannotInvokeException with an error message.
     */
    public CannotInvokeException(String reason) {
        super(reason);
    }

    /**
     * Constructs a CannotInvokeException with an InvocationTargetException.
     */
    public CannotInvokeException(InvocationTargetException e) {
        super("by " + e.getTargetException().toString());
        err = e.getTargetException();
    }

    /**
     * Constructs a CannotInvokeException with an IllegalAccessException.
     */
    public CannotInvokeException(IllegalAccessException e) {
        super("by " + e.toString());
        err = e;
    }

    /**
     * Constructs a CannotInvokeException with an ClassNotFoundException.
     */
    public CannotInvokeException(ClassNotFoundException e) {
        super("by " + e.toString());
        err = e;
    }
}
