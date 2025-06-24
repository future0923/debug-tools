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
import java.io.InputStream;

/**
 * {@link Resource} implementation for a given {@link InputStream}.
 * <p>
 * Should only be used if no other specific {@code Resource} implementation is
 * applicable. In particular, prefer {@link ByteArrayResource} or any of the
 * file-based {@code Resource} implementations where possible.
 *
 * <p>
 * In contrast to other {@code Resource} implementations, this is a descriptor
 * for an <i>already opened</i> resource - therefore returning {@code true} from
 * {@link #isOpen()}. Do not use an {@code InputStreamResource} if you need to
 * keep the resource descriptor somewhere, or if you need to read from a stream
 * multiple times.
 *
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @since 28.12.2003
 * @see ByteArrayResource
 * @see ClassPathResource
 * @see FileSystemResource
 * @see UrlResource
 */
public class InputStreamResource extends AbstractResource {

    private final InputStream inputStream;

    private final String description;

    private boolean read = false;

    /**
     * Create a new InputStreamResource.
     * 
     * @param inputStream
     *            the InputStream to use
     */
    public InputStreamResource(InputStream inputStream) {
        this(inputStream, "resource loaded through InputStream");
    }

    /**
     * Create a new InputStreamResource.
     * 
     * @param inputStream
     *            the InputStream to use
     * @param description
     *            where the InputStream comes from
     */
    public InputStreamResource(InputStream inputStream, String description) {
        if (inputStream == null) {
            throw new IllegalArgumentException("InputStream must not be null");
        }
        this.inputStream = inputStream;
        this.description = (description != null ? description : "");
    }

    /**
     * This implementation always returns {@code true}.
     */
    @Override
    public boolean exists() {
        return true;
    }

    /**
     * This implementation always returns {@code true}.
     */
    @Override
    public boolean isOpen() {
        return true;
    }

    /**
     * This implementation throws IllegalStateException if attempting to read
     * the underlying stream multiple times.
     */
    @Override
    public InputStream getInputStream() throws IOException, IllegalStateException {
        if (this.read) {
            throw new IllegalStateException("InputStream has already been read - " + "do not use InputStreamResource if a stream needs to be read multiple times");
        }
        this.read = true;
        return this.inputStream;
    }

    /**
     * This implementation returns a description that includes the passed-in
     * description, if any.
     */
    @Override
    public String getDescription() {
        return "InputStream resource [" + this.description + "]";
    }

    /**
     * This implementation compares the underlying InputStream.
     */
    @Override
    public boolean equals(Object obj) {
        return (obj == this || (obj instanceof InputStreamResource && ((InputStreamResource) obj).inputStream.equals(this.inputStream)));
    }

    /**
     * This implementation returns the hash code of the underlying InputStream.
     */
    @Override
    public int hashCode() {
        return this.inputStream.hashCode();
    }

}