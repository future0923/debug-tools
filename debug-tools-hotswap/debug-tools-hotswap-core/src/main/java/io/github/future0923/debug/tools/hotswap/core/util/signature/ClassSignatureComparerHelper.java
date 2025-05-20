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
import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.javassist.NotFoundException;
import io.github.future0923.debug.tools.base.logging.Logger;

/**
 * 检查类签名是否发生变化
 */
public class ClassSignatureComparerHelper {

    private static final Logger LOGGER = Logger.getLogger(ClassSignatureComparerHelper.class);

    /**
     * 获取javassist类签名
     */
    public static String getCtClassSignature(CtClass ctClass, ClassSignatureElement[] signatureElements) throws Exception {
        CtClassSignature signature = new CtClassSignature(ctClass);
        signature.addSignatureElements(signatureElements);
        return signature.getValue();
    }

    /**
     * 获取java类签名
     */
    public static String getJavaClassSignature(Class<?> clazz, ClassSignatureElement[] signatureElements) throws Exception {
        JavaClassSignature signature = new JavaClassSignature(clazz);
        signature.addSignatureElements(signatureElements);
        return signature.getValue();
    }

    /**
     * 是否有变化
     *
     * @param ctClass 新的CtClass definition
     * @param clazz   老的Class definition
     * @return 是否不同
     */
    public static boolean isDifferent(CtClass ctClass, Class<?> clazz, ClassSignatureElement[] signatureElements) {
        try {
            String sig1 = getCtClassSignature(ctClass, signatureElements);
            String sig2 = getJavaClassSignature(clazz, signatureElements);
            return !sig1.equals(sig2);
        } catch (Exception e) {
            LOGGER.error("Error reading signature", e);
            return false;
        }
    }

    public static boolean isDifferent(Class<?> clazz1, Class<?> clazz2, ClassSignatureElement[] signatureElements) {
        try {
            String sig1 = getJavaClassSignature(clazz1, signatureElements);
            String sig2 = getJavaClassSignature(clazz2, signatureElements);
            return !sig1.equals(sig2);
        } catch (Exception e) {
            LOGGER.error("Error reading signature", e);
            return false;
        }
    }

    /**
     * 在ClassPool中是否变化
     *
     * @param clazz 老Class definition
     * @param cp    新的CtClass所在的ClassPool中的definition
     * @return 是否变化
     */
    public static boolean isPoolClassDifferent(Class<?> clazz, ClassPool cp, ClassSignatureElement[] signatureElements) {
        try {
            return isDifferent(cp.get(clazz.getName()), clazz, signatureElements);
        } catch (NotFoundException e) {
            LOGGER.error("Class not found ", e);
            return false;
        }
    }

}
