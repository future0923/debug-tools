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

import io.github.future0923.debug.tools.hotswap.core.javassist.CannotCompileException;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;

/**
 * Thrown by <code>makeReflective()</code> in <code>Reflection</code>
 * when there is an attempt to reflect
 * a class that is either an interface or a subclass of
 * either ClassMetaobject or Metaobject.
 *
 * @author Brett Randall
 * @see javassist.tools.reflect.Reflection#makeReflective(CtClass,CtClass,CtClass)
 * @see javassist.CannotCompileException
 */
public class CannotReflectException extends CannotCompileException {
    /** default serialVersionUID */
    private static final long serialVersionUID = 1L;

    public CannotReflectException(String msg) {
        super(msg);
    }
}
