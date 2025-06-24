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
package io.github.future0923.debug.tools.hotswap.core.javassist.util.proxy;

/**
 * The interface implemented by proxy classes.
 * This interface is available only if the super class of the proxy object
 * does not have a <code>getHandler()</code> method.  If the super class
 * has <code>getHandler</code>, then <code>Proxy</code> interface is
 * available.  
 *
 * @see ProxyFactory
 * @see Proxy
 */
public interface ProxyObject extends Proxy {
    /**
     * Sets a handler.  It can be used for changing handlers
     * during runtime.
     */
    @Override
    void setHandler(MethodHandler mi);

    /**
     * Get the handler.
     * This can be used to access the underlying MethodHandler
     * or to serialize it properly.
     *
     * @see ProxyFactory#getHandler(Proxy)
     */
    MethodHandler getHandler();
}
