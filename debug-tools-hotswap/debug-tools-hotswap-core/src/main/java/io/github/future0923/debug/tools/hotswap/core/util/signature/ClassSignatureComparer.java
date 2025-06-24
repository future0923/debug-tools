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
package io.github.future0923.debug.tools.hotswap.core.util.signature;


import io.github.future0923.debug.tools.hotswap.core.javassist.ClassPool;

/**
 * 插件类签名是否发生变化，变化了Spring需要重新加载类
 */
public class ClassSignatureComparer {

    private static final ClassSignatureElement[] SIGNATURE_ELEMENTS=  {
            ClassSignatureElement.SUPER_CLASS,
            ClassSignatureElement.INTERFACES,
            ClassSignatureElement.CLASS_ANNOTATION,
            ClassSignatureElement.CONSTRUCTOR,
            ClassSignatureElement.METHOD,
            ClassSignatureElement.METHOD_STATIC,
            ClassSignatureElement.METHOD_ANNOTATION,
            ClassSignatureElement.METHOD_PARAM_ANNOTATION,
            ClassSignatureElement.METHOD_EXCEPTION,
            ClassSignatureElement.FIELD,
            ClassSignatureElement.FIELD_STATIC,
            ClassSignatureElement.FIELD_ANNOTATION
    };

    /**
     * 在ClassPool中是否变化
     *
     * @param classBeingRedefined 老Class definition
     * @param classPool           新的CtClass所在的ClassPool中的definition
     * @return 是否变化
     */
    public static boolean isPoolClassDifferent(Class<?> classBeingRedefined, ClassPool classPool) {
        return ClassSignatureComparerHelper.isPoolClassDifferent(classBeingRedefined, classPool, SIGNATURE_ELEMENTS);
    }
}