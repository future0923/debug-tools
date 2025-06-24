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

import io.github.future0923.debug.tools.hotswap.core.util.spring.io.resource.ContextResource;
import io.github.future0923.debug.tools.hotswap.core.util.spring.io.resource.FileSystemResource;
import io.github.future0923.debug.tools.hotswap.core.util.spring.io.resource.Resource;

/**
 * {@link ResourceLoader} implementation that resolves plain paths as file
 * system resources rather than as class path resources (the latter is
 * {@link DefaultResourceLoader}'s default strategy).
 *
 * <p>
 * <b>NOTE:</b> Plain paths will always be interpreted as relative to the
 * current VM working directory, even if they start with a slash. (This is
 * consistent with the semantics in a Servlet container.) <b>Use an explicit
 * "file:" prefix to enforce an absolute file path.</b>
 *
 * <p>
 * {@link org.springframework.context.support.FileSystemXmlApplicationContext}
 * is a full-fledged ApplicationContext implementation that provides the same
 * resource path resolution strategy.
 *
 * @author Juergen Hoeller
 * @since 1.1.3
 * @see DefaultResourceLoader
 * @see org.springframework.context.support.FileSystemXmlApplicationContext
 */
public class FileSystemResourceLoader extends DefaultResourceLoader {

    /**
     * Resolve resource paths as file system paths.
     * <p>
     * Note: Even if a given path starts with a slash, it will get interpreted
     * as relative to the current VM working directory.
     * 
     * @param path
     *            the path to the resource
     * @return the corresponding Resource handle
     * @see FileSystemResource
     * @see org.springframework.web.context.support.ServletContextResourceLoader#getResourceByPath
     */
    @Override
    protected Resource getResourceByPath(String path) {
        if (path != null && path.startsWith("/")) {
            path = path.substring(1);
        }
        return new FileSystemContextResource(path);
    }

    /**
     * FileSystemResource that explicitly expresses a context-relative path
     * through implementing the ContextResource interface.
     */
    private static class FileSystemContextResource extends FileSystemResource implements ContextResource {

        public FileSystemContextResource(String path) {
            super(path);
        }

        @Override
        public String getPathWithinContext() {
            return getPath();
        }
    }

}