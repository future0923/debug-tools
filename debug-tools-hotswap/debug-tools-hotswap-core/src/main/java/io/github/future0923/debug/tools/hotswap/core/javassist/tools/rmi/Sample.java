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
