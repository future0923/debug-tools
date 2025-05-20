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
