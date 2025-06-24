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
package io.github.future0923.debug.tools.hotswap.core.util.spring.io.loader;

import io.github.future0923.debug.tools.hotswap.core.util.spring.io.resource.Resource;
import io.github.future0923.debug.tools.hotswap.core.util.spring.util.ResourceUtils;

public interface ResourceLoader {

    /** Pseudo URL prefix for loading from the class path: "classpath:" */
    String CLASSPATH_URL_PREFIX = ResourceUtils.CLASSPATH_URL_PREFIX;

    /**
     * Return a Resource handle for the specified resource. The handle should
     * always be a reusable resource descriptor, allowing for multiple
     * {@link Resource#getInputStream()} calls.
     * <p>
     * <ul>
     * <li>Must support fully qualified URLs, e.g. "file:C:/test.dat".
     * <li>Must support classpath pseudo-URLs, e.g. "classpath:test.dat".
     * <li>Should support relative file paths, e.g. "WEB-INF/test.dat". (This
     * will be implementation-specific, typically provided by an
     * ApplicationContext implementation.)
     * </ul>
     * <p>
     * Note that a Resource handle does not imply an existing resource; you need
     * to invoke {@link Resource#exists} to check for existence.
     * 
     * @param location
     *            the resource location
     * @return a corresponding Resource handle
     * @see #CLASSPATH_URL_PREFIX
     */
    Resource getResource(String location);

    /**
     * Expose the ClassLoader used by this ResourceLoader.
     * <p>
     * Clients which need to access the ClassLoader directly can do so in a
     * uniform manner with the ResourceLoader, rather than relying on the thread
     * context ClassLoader.
     * 
     * @return the ClassLoader (only {@code null} if even the system ClassLoader
     *         isn't accessible)
     */
    ClassLoader getClassLoader();

}