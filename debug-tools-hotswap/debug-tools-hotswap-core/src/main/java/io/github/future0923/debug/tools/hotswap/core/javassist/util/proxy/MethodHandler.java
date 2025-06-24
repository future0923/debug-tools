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

import java.lang.reflect.Method;

/**
 * The interface implemented by the invocation handler of a proxy
 * instance.
 *
 * @see Proxy#setHandler(MethodHandler)
 */
public interface MethodHandler {
    /**
     * Is called when a method is invoked on a proxy instance associated
     * with this handler.  This method must process that method invocation.
     *
     * @param self          the proxy instance.
     * @param thisMethod    the overridden method declared in the super
     *                      class or interface.
     * @param proceed       the forwarder method for invoking the overridden 
     *                      method.  It is null if the overridden method is
     *                      abstract or declared in the interface.
     * @param args          an array of objects containing the values of
     *                      the arguments passed in the method invocation
     *                      on the proxy instance.  If a parameter type is
     *                      a primitive type, the type of the array element
     *                      is a wrapper class.
     * @return              the resulting value of the method invocation.
     *
     * @throws Throwable    if the method invocation fails.
     */
    Object invoke(Object self, Method thisMethod, Method proceed,
                  Object[] args) throws Throwable;
}
