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

import io.github.future0923.debug.tools.hotswap.core.util.spring.util.VfsUtils;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.URL;

//import org.springframework.core.io.VfsUtils;

/**
 * Artificial class used for accessing the {@link VfsUtils} methods without
 * exposing them to the entire world.
 *
 * @author Costin Leau
 * @since 3.0.3
 */
abstract class VfsPatternUtils extends VfsUtils {

    static Object getVisitorAttribute() {
        return doGetVisitorAttribute();
    }

    static String getPath(Object resource) {
        return doGetPath(resource);
    }

    static Object findRoot(URL url) throws IOException {
        return getRoot(url);
    }

    static void visit(Object resource, InvocationHandler visitor) throws IOException {
        Object visitorProxy = Proxy.newProxyInstance(VIRTUAL_FILE_VISITOR_INTERFACE.getClassLoader(), new Class<?>[] { VIRTUAL_FILE_VISITOR_INTERFACE }, visitor);
        invokeVfsMethod(VIRTUAL_FILE_METHOD_VISIT, resource, visitorProxy);
    }

}