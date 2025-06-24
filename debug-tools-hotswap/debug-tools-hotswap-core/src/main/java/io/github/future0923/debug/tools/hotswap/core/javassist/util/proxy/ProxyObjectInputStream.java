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

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

/**
 * An input stream class which knows how to deserialize proxies created via {@link ProxyFactory} and
 * serializedo via a {@link ProxyObjectOutputStream}. It must be used when deserialising proxies created
 * from a proxy factory configured with {@link ProxyFactory#useWriteReplace} set to false.
 *
 * @author Andrew Dinn
 */
public class ProxyObjectInputStream extends ObjectInputStream
{
    /**
     * create an input stream which can be used to deserialize an object graph which includes proxies created
     * using class ProxyFactory. the classloader used to resolve proxy superclass and interface names
     * read from the input stream will default to the current thread's context class loader or the system
     * classloader if the context class loader is null.
     * @param in
     * @throws java.io.StreamCorruptedException whenever ObjectInputStream would also do so
     * @throws	IOException whenever ObjectInputStream would also do so
     * @throws	SecurityException whenever ObjectInputStream would also do so
     * @throws NullPointerException if in is null
     */
    public ProxyObjectInputStream(InputStream in) throws IOException
    {
        super(in);
        loader = Thread.currentThread().getContextClassLoader();
        if (loader == null) {
            loader = ClassLoader.getSystemClassLoader();
        }
    }

    /**
     * Reset the loader to be
     * @param loader
     */
    public void setClassLoader(ClassLoader loader)
    {
        if (loader != null) {
            this.loader = loader;
        } else {
            loader = ClassLoader.getSystemClassLoader();
        }
    }

    @Override
    protected ObjectStreamClass readClassDescriptor() throws IOException, ClassNotFoundException {
        boolean isProxy = readBoolean();
        if (isProxy) {
            String name = (String)readObject();
            Class<?> superClass = loader.loadClass(name);
            int length = readInt();
            Class<?>[] interfaces = new Class[length];
            for (int i = 0; i < length; i++) {
                name = (String)readObject();
                interfaces[i] = loader.loadClass(name);
            }
            length = readInt();
            byte[] signature = new byte[length];
            read(signature);
            ProxyFactory factory = new ProxyFactory();
            // we must always use the cache and never use writeReplace when using
            // ProxyObjectOutputStream and ProxyObjectInputStream
            factory.setUseCache(true);
            factory.setUseWriteReplace(false);
            factory.setSuperclass(superClass);
            factory.setInterfaces(interfaces);
            Class<?> proxyClass = factory.createClass(signature);
            return ObjectStreamClass.lookup(proxyClass);
        }
        return super.readClassDescriptor();
    }

    /**
     * the loader to use to resolve classes for proxy superclass and interface names read
     * from the stream. defaults to the context class loader of the thread which creates
     * the input stream or the system class loader if the context class loader is null.
     */
    private ClassLoader loader;
}
