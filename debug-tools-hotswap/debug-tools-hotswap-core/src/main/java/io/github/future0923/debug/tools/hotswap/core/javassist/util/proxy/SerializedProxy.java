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

import java.io.InvalidClassException;
import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

/**
 * A proxy object is converted into an instance of this class
 * when it is written to an output stream.
 *
 * @see RuntimeSupport#makeSerializedProxy(Object)
 */
class SerializedProxy implements Serializable {
    /** default serialVersionUID */
    private static final long serialVersionUID = 1L;
    private String superClass;
    private String[] interfaces;
    private byte[] filterSignature;
    private MethodHandler handler;

    SerializedProxy(Class<?> proxy, byte[] sig, MethodHandler h) {
        filterSignature = sig;
        handler = h;
        superClass = proxy.getSuperclass().getName();
        Class<?>[] infs = proxy.getInterfaces();
        int n = infs.length;
        interfaces = new String[n - 1];
        String setterInf = ProxyObject.class.getName();
        String setterInf2 = Proxy.class.getName();
        for (int i = 0; i < n; i++) {
            String name = infs[i].getName();
            if (!name.equals(setterInf) && !name.equals(setterInf2))
                interfaces[i] = name;
        }
    }

    /**
     * Load class.
     *
     * @param className the class name
     * @return loaded class
     * @throws ClassNotFoundException for any error
     */
    protected Class<?> loadClass(final String className) throws ClassNotFoundException {
        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<Class<?>>(){
                @Override
                public Class<?> run() throws Exception{
                    ClassLoader cl = Thread.currentThread().getContextClassLoader();
                    return Class.forName(className, true, cl);
                }
            });
        }
        catch (PrivilegedActionException pae) {
            throw new RuntimeException("cannot load the class: " + className, pae.getException());
        }
    }

    Object readResolve() throws ObjectStreamException {
        try {
            int n = interfaces.length;
            Class<?>[] infs = new Class[n];
            for (int i = 0; i < n; i++)
                infs[i] = loadClass(interfaces[i]);

            ProxyFactory f = new ProxyFactory();
            f.setSuperclass(loadClass(superClass));
            f.setInterfaces(infs);
            Proxy proxy = (Proxy)f.createClass(filterSignature).getConstructor().newInstance();
            proxy.setHandler(handler);
            return proxy;
        }
        catch (NoSuchMethodException e) {
            throw new InvalidClassException(e.getMessage());
        }
        catch (InvocationTargetException e) {
            throw new InvalidClassException(e.getMessage());
        }
        catch (ClassNotFoundException e) {
            throw new InvalidClassException(e.getMessage());
        }
        catch (InstantiationException e2) {
            throw new InvalidObjectException(e2.getMessage());
        }
        catch (IllegalAccessException e3) {
            throw new InvalidClassException(e3.getMessage());
        }
    }
}
