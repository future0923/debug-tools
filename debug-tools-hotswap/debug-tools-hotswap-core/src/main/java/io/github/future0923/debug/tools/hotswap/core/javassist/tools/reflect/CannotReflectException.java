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
