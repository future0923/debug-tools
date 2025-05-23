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