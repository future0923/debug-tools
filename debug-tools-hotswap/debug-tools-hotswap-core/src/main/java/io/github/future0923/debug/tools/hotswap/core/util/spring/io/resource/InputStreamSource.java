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
 * Simple interface for objects that are sources for an {@link InputStream}.
 *
 * <p>
 * This is the base interface for Spring's more extensive {@link Resource}
 * interface.
 *
 * <p>
 * For single-use streams, {@link InputStreamResource} can be used for any given
 * {@code InputStream}. Spring's {@link ByteArrayResource} or any file-based
 * {@code Resource} implementation can be used as a concrete instance, allowing
 * one to read the underlying content stream multiple times. This makes this
 * interface useful as an abstract content source for mail attachments, for
 * example.
 *
 * @author Juergen Hoeller
 * @since 20.01.2004
 * @see InputStream
 * @see Resource
 * @see InputStreamResource
 * @see ByteArrayResource
 */
public interface InputStreamSource {

    /**
     * Return an {@link InputStream}.
     * <p>
     * It is expected that each call creates a <i>fresh</i> stream.
     * <p>
     * This requirement is particularly important when you consider an API such
     * as JavaMail, which needs to be able to read the stream multiple times
     * when creating mail attachments. For such a use case, it is
     * <i>required</i> that each {@code getInputStream()} call returns a fresh
     * stream.
     * 
     * @return the input stream for the underlying resource (must not be
     *         {@code null})
     * @throws IOException
     *             if the stream could not be opened
     * @see org.springframework.mail.javamail.MimeMessageHelper#addAttachment(String,
     *      InputStreamSource)
     */
    InputStream getInputStream() throws IOException;

}