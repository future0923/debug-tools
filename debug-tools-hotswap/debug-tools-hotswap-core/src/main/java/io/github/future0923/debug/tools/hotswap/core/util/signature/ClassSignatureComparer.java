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