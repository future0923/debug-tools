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
