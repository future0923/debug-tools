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
package io.github.future0923.debug.tools.hotswap.core.util.spring.path;

import io.github.future0923.debug.tools.hotswap.core.util.spring.io.loader.ResourceLoader;
import io.github.future0923.debug.tools.hotswap.core.util.spring.io.resource.Resource;

import java.io.IOException;

/**
 * Strategy interface for resolving a location pattern (for example, an
 * Ant-style path pattern) into Resource objects.
 *
 * <p>
 * This is an extension to the
 * {@link ResourceLoader}
 * interface. A passed-in ResourceLoader (for example, an
 * {@link org.springframework.context.ApplicationContext} passed in via
 * {@link org.springframework.context.ResourceLoaderAware} when running in a
 * context) can be checked whether it implements this extended interface too.
 *
 * <p>
 * {@link PathMatchingResourcePatternResolver} is a standalone implementation
 * that is usable outside an ApplicationContext, also used by
 * {@link ResourceArrayPropertyEditor} for populating Resource array bean
 * properties.
 *
 * <p>
 * Can be used with any sort of location pattern (e.g.
 * "/WEB-INF/*-context.xml"): Input patterns have to match the strategy
 * implementation. This interface just specifies the conversion method rather
 * than a specific pattern format.
 *
 * <p>
 * This interface also suggests a new resource prefix "classpath*:" for all
 * matching resources from the class path. Note that the resource location is
 * expected to be a path without placeholders in this case (e.g. "/beans.xml");
 * JAR files or classes directories can contain multiple files of the same name.
 *
 * @author Juergen Hoeller
 * @since 1.0.2
 * @see Resource
 * @see ResourceLoader
 * @see org.springframework.context.ApplicationContext
 * @see org.springframework.context.ResourceLoaderAware
 */
public interface ResourcePatternResolver extends ResourceLoader {

    /**
     * Pseudo URL prefix for all matching resources from the class path:
     * "classpath*:" This differs from ResourceLoader's classpath URL prefix in
     * that it retrieves all matching resources for a given name (e.g.
     * "/beans.xml"), for example in the root of all deployed JAR files.
     * 
     * @see ResourceLoader#CLASSPATH_URL_PREFIX
     */
    String CLASSPATH_ALL_URL_PREFIX = "classpath*:";

    /**
     * Resolve the given location pattern into Resource objects.
     * <p>
     * Overlapping resource entries that point to the same physical resource
     * should be avoided, as far as possible. The result should have set
     * semantics.
     * 
     * @param locationPattern
     *            the location pattern to resolve
     * @return the corresponding Resource objects
     * @throws IOException
     *             in case of I/O errors
     */
    Resource[] getResources(String locationPattern) throws IOException;

}