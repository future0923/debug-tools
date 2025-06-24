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

import io.github.future0923.debug.tools.hotswap.core.util.spring.io.resource.ClassPathResource;
import io.github.future0923.debug.tools.hotswap.core.util.spring.io.resource.ContextResource;
import io.github.future0923.debug.tools.hotswap.core.util.spring.io.resource.Resource;
import io.github.future0923.debug.tools.hotswap.core.util.spring.util.Assert;
import io.github.future0923.debug.tools.hotswap.core.util.spring.util.StringUtils;

/**
 * {@link ResourceLoader} implementation that interprets plain resource paths as
 * relative to a given {@code java.lang.Class}.
 *
 * @author Juergen Hoeller
 * @since 3.0
 * @see Class#getResource(String)
 * @see ClassPathResource#ClassPathResource(String, Class)
 */
public class ClassRelativeResourceLoader extends DefaultResourceLoader {

    private final Class<?> clazz;

    /**
     * Create a new ClassRelativeResourceLoader for the given class.
     * 
     * @param clazz
     *            the class to load resources through
     */
    public ClassRelativeResourceLoader(Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        this.clazz = clazz;
        setClassLoader(clazz.getClassLoader());
    }

    @Override
    protected Resource getResourceByPath(String path) {
        return new ClassRelativeContextResource(path, this.clazz);
    }

    /**
     * ClassPathResource that explicitly expresses a context-relative path
     * through implementing the ContextResource interface.
     */
    private static class ClassRelativeContextResource extends ClassPathResource implements ContextResource {

        private final Class<?> clazz;

        public ClassRelativeContextResource(String path, Class<?> clazz) {
            super(path, clazz);
            this.clazz = clazz;
        }

        @Override
        public String getPathWithinContext() {
            return getPath();
        }

        @Override
        public Resource createRelative(String relativePath) {
            String pathToUse = StringUtils.applyRelativePath(getPath(), relativePath);
            return new ClassRelativeContextResource(pathToUse, this.clazz);
        }
    }

}