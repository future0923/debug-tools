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

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
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
