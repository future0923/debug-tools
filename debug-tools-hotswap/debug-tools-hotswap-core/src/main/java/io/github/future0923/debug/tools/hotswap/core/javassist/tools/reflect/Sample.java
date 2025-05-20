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
package io.github.future0923.debug.tools.hotswap.core.javassist.tools.reflect;

/**
 * A template used for defining a reflective class.
 */
public class Sample {
    private Metaobject _metaobject;
    private static ClassMetaobject _classobject;

    public Object trap(Object[] args, int identifier) throws Throwable {
        Metaobject mobj;
        mobj = _metaobject;
        if (mobj == null)
            return ClassMetaobject.invoke(this, identifier, args);
        return mobj.trapMethodcall(identifier, args);
    }

    public static Object trapStatic(Object[] args, int identifier)
        throws Throwable
    {
        return _classobject.trapMethodcall(identifier, args);
    }

    public static Object trapRead(Object[] args, String name) {
        if (args[0] == null)
            return _classobject.trapFieldRead(name);
        return ((Metalevel)args[0])._getMetaobject().trapFieldRead(name);
    }

    public static Object trapWrite(Object[] args, String name) {
        Metalevel base = (Metalevel)args[0];
        if (base == null)
            _classobject.trapFieldWrite(name, args[1]);
        else
            base._getMetaobject().trapFieldWrite(name, args[1]);

        return null;
    }
}
