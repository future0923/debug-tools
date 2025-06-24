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
package io.github.future0923.debug.tools.hotswap.core.util.spring.io.resource;


import java.io.IOException;

/**
 * Subclass of {@link IOException} that properly handles a root cause, exposing
 * the root cause just like NestedChecked/RuntimeException does.
 *
 * <p>
 * Proper root cause handling has not been added to standard IOException before
 * Java 6, which is why we need to do it ourselves for Java 5 compatibility
 * purposes.
 *
 * <p>
 * The similarity between this class and the NestedChecked/RuntimeException
 * class is unavoidable, as this class needs to derive from IOException.
 *
 * @author Juergen Hoeller
 * @since 2.0
 * @see #getMessage
 * @see #printStackTrace
 * @see org.springframework.core.NestedCheckedException
 * @see org.springframework.core.NestedRuntimeException
 */
@SuppressWarnings("serial")
public class NestedIOException extends IOException {

    static {
        // Eagerly load the NestedExceptionUtils class to avoid classloader
        // deadlock
        // issues on OSGi when calling getMessage(). Reported by Don Brown;
        // SPR-5607.
        NestedExceptionUtils.class.getName();
    }

    /**
     * Construct a {@code NestedIOException} with the specified detail message.
     * 
     * @param msg
     *            the detail message
     */
    public NestedIOException(String msg) {
        super(msg);
    }

    /**
     * Construct a {@code NestedIOException} with the specified detail message
     * and nested exception.
     * 
     * @param msg
     *            the detail message
     * @param cause
     *            the nested exception
     */
    public NestedIOException(String msg, Throwable cause) {
        super(msg);
        initCause(cause);
    }

    /**
     * Return the detail message, including the message from the nested
     * exception if there is one.
     */
    @Override
    public String getMessage() {
        return NestedExceptionUtils.buildMessage(super.getMessage(), getCause());
    }

}