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
package io.github.future0923.debug.tools.hotswap.core.javassist.tools.rmi;


/**
 * A template used for defining a proxy class.
 * The class file of this class is read by the <code>StubGenerator</code>
 * class.
 */
public class Sample {
    private ObjectImporter importer;
    private int objectId;

    public Object forward(Object[] args, int identifier) {
        return importer.call(objectId, identifier, args);
    }

    public static Object forwardStatic(Object[] args, int identifier)
        throws RemoteException
    {
        throw new RemoteException("cannot call a static method.");
    }
}
