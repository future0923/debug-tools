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
