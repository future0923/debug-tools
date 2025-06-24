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
import java.io.OutputStream;

/**
 * Extended interface for a resource that supports writing to it. Provides an
 * {@link #getOutputStream() OutputStream accessor}.
 *
 * @author Juergen Hoeller
 * @since 3.1
 * @see OutputStream
 */
public interface WritableResource extends Resource {

    /**
     * Return whether the contents of this resource can be modified, e.g. via
     * {@link #getOutputStream()} or {@link #getFile()}.
     * <p>
     * Will be {@code true} for typical resource descriptors; note that actual
     * content writing may still fail when attempted. However, a value of
     * {@code false} is a definitive indication that the resource content cannot
     * be modified.
     * 
     * @see #getOutputStream()
     * @see #isReadable()
     */
    boolean isWritable();

    /**
     * Return an {@link OutputStream} for the underlying resource, allowing to
     * (over-)write its content.
     * 
     * @throws IOException
     *             if the stream could not be opened
     * @see #getInputStream()
     */
    OutputStream getOutputStream() throws IOException;

}