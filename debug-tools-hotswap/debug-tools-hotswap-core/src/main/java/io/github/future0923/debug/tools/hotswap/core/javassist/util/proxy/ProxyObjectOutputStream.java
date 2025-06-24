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
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;

/**
 * An input stream class which knows how to serialize proxies created via {@link ProxyFactory}. It must
 * be used when serialising proxies created from a proxy factory configured with
 * {@link ProxyFactory#useWriteReplace} set to false. Subsequent deserialization of the serialized data
 * must employ a {@link ProxyObjectInputStream}
 *
 * @author Andrew Dinn
 */
public class ProxyObjectOutputStream extends ObjectOutputStream
{
    /**
     * create an output stream which can be used to serialize an object graph which includes proxies created
     * using class ProxyFactory
     * @param out
     * @throws IOException whenever ObjectOutputStream would also do so
     * @throws SecurityException whenever ObjectOutputStream would also do so
     * @throws NullPointerException if out is null
     */
    public ProxyObjectOutputStream(OutputStream out) throws IOException
    {
        super(out);
    }

    @Override
    protected void writeClassDescriptor(ObjectStreamClass desc) throws IOException {
        Class<?> cl = desc.forClass();
        if (ProxyFactory.isProxyClass(cl)) {
            writeBoolean(true);
            Class<?> superClass = cl.getSuperclass();
            Class<?>[] interfaces = cl.getInterfaces();
            byte[] signature = ProxyFactory.getFilterSignature(cl);
            String name = superClass.getName();
            writeObject(name);
            // we don't write the marker interface ProxyObject
            writeInt(interfaces.length - 1);
            for (int i = 0; i < interfaces.length; i++) {
                Class<?> interfaze = interfaces[i];
                if (interfaze != ProxyObject.class && interfaze != Proxy.class) {
                    name = interfaces[i].getName();
                    writeObject(name);
                }
            }
            writeInt(signature.length);
            write(signature);
        } else {
            writeBoolean(false);
            super.writeClassDescriptor(desc);
        }
    }
}
