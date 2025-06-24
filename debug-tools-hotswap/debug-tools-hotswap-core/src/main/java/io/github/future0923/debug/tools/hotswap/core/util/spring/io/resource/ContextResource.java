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

import io.github.future0923.debug.tools.hotswap.core.util.spring.io.loader.ResourceLoader;

/**
 * Extended interface for a resource that is loaded from an enclosing 'context',
 * e.g. from a {@link javax.servlet.ServletContext} or a
 * {@link javax.portlet.PortletContext} but also from plain classpath paths or
 * relative file system paths (specified without an explicit prefix, hence
 * applying relative to the local {@link ResourceLoader}'s context).
 *
 * @author Juergen Hoeller
 * @since 2.5
 * @see org.springframework.web.context.support.ServletContextResource
 * @see org.springframework.web.portlet.context.PortletContextResource
 */
public interface ContextResource extends Resource {

    /**
     * Return the path within the enclosing 'context'.
     * <p>
     * This is typically path relative to a context-specific root directory,
     * e.g. a ServletContext root or a PortletContext root.
     */
    String getPathWithinContext();

}