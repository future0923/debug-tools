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
package io.github.future0923.debug.tools.hotswap.core.plugin.proxy.utils;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.javassist.ClassPool;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.util.signature.ClassSignatureComparerHelper;
import io.github.future0923.debug.tools.hotswap.core.util.signature.ClassSignatureElement;

import java.util.HashMap;
import java.util.Map;

/**
 * @author future0923
 */
public class ProxyClassSignatureHelper {

    private static final Logger LOGGER = Logger.getLogger(ProxyClassSignatureHelper.class);

    private static final ClassSignatureElement[] SIGNATURE_WITH_ANNO_ELEMENTS = {
            ClassSignatureElement.SUPER_CLASS,
            ClassSignatureElement.INTERFACES,
            ClassSignatureElement.METHOD,
            ClassSignatureElement.METHOD_ANNOTATION,
            ClassSignatureElement.METHOD_PARAM_ANNOTATION,
            ClassSignatureElement.METHOD_EXCEPTION,
    };

    private static final ClassSignatureElement[] SIGNATURE_ELEMENTS = {
            ClassSignatureElement.SUPER_CLASS,
            ClassSignatureElement.INTERFACES,
            ClassSignatureElement.METHOD,
            ClassSignatureElement.METHOD_EXCEPTION,
    };

    public static String getJavaClassSignature(Class<?> clazz) throws Exception {
        return ClassSignatureComparerHelper.getJavaClassSignature(clazz, SIGNATURE_WITH_ANNO_ELEMENTS);
    }

    private static void addSignaturesToMap(Class<?> clazz, Map<String, String> signatureMap) {
        if (clazz != null && clazz != Object.class) {
            try {
                String signature = getJavaClassSignature(clazz);
                signatureMap.put(clazz.getName(), signature);
            } catch (Exception e) {
                LOGGER.error("Error reading signature", e);
            }
            for (Class<?> interfaceClazz : clazz.getInterfaces()) {
                addSignaturesToMap(interfaceClazz, signatureMap);
            }
        }
    }

    public static Map<String, String> getNonSyntheticSignatureMap(Class<?> clazz) {
        Map<String, String> signatureMap = new HashMap<>();

        Class<?> parentClass = clazz.getSuperclass();
        while (parentClass.isSynthetic()) {
            parentClass = parentClass.getSuperclass();
        }
        addSignaturesToMap(parentClass, signatureMap);
        for (Class<?> intr : clazz.getInterfaces()) {
            addSignaturesToMap(intr, signatureMap);
        }
        return signatureMap;
    }

    public static boolean isPoolClassDifferent(Class<?> clazz, ClassPool cp) {
        return ClassSignatureComparerHelper.isPoolClassDifferent(clazz, cp, SIGNATURE_ELEMENTS);
    }

    /**
     * Checks if the CtClass or one of its parents signature differs from the one already loaded by Java.
     *
     * @param clazz
     * @param cp
     * @return
     */
    public static boolean isPoolClassOrParentDifferent(Class<?> clazz, ClassPool cp) {
        if (isPoolClassDifferent(clazz, cp))
            return true;
        Class<?> superclass = clazz.getSuperclass();
        if (superclass != null) {
            if (isPoolClassOrParentDifferent(superclass, cp)) {
                return true;
            }
        }
        Class<?>[] interfaces = clazz.getInterfaces();
        for (Class<?> interfaceClazz : interfaces) {
            if (isPoolClassOrParentDifferent(interfaceClazz, cp)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the CtClass or one of its parents signature differs from the one already loaded by Java. Ignores
     * synthetic classes
     *
     * @param classBeingRedefined
     * @param cp
     * @return
     */
    public static boolean isNonSyntheticPoolClassOrParentDifferent(Class<?> classBeingRedefined, ClassPool cp) {
        Class<?> clazz = classBeingRedefined.getSuperclass();
        while (clazz.isSynthetic() || clazz.getName().contains("$Enhancer")) {
            clazz = clazz.getSuperclass();
        }
        if (isPoolClassOrParentDifferent(clazz, cp))
            return true;
        Class<?>[] interfaces = classBeingRedefined.getInterfaces();
        for (Class<?> intr : interfaces) {
            if (isPoolClassOrParentDifferent(intr, cp))
                return true;
        }
        return false;
    }

    /**
     * Checks if the CtClass or one of its parents signature differs from the one already loaded by Java.
     *
     * @param clazz
     * @param cc
     * @return
     */
    public static boolean isPoolClassOrParentDifferent(Class<?> clazz, CtClass cc) {
        return isPoolClassDifferent(clazz, cc.getClassPool());
    }

    /**
     * Class arrays need to be in the same order. Check if a signature of class differs from aonther. Useful for
     * checking difference in different classloaders.
     *
     * @param classesA
     * @param classesB
     * @return
     */
    public static boolean isDifferent(Class<?>[] classesA, Class<?>[] classesB) {
        for (int i = 0; i < classesB.length; i++) {
            Class<?> class1 = classesA[i];
            Class<?> class2 = classesB[i];
            if (ClassSignatureComparerHelper.isDifferent(class1, class2, SIGNATURE_ELEMENTS)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isPoolClassDifferent(Class<?> clazz, ClassLoader cp) {
        try {
            return ClassSignatureComparerHelper.isDifferent(clazz, cp.loadClass(clazz.getName()), SIGNATURE_ELEMENTS);
        } catch (ClassNotFoundException e) {
            LOGGER.error("Error reading signature", e);
            return false;
        }
    }

    /**
     * Checks if the Class or one of its parents signature differs from the one in the classloader.
     *
     * @param clazz
     * @param cp
     * @return
     */
    public static boolean isPoolClassOrParentDifferent(Class<?> clazz, ClassLoader cp) {
        if (isPoolClassDifferent(clazz, cp))
            return true;
        Class<?> superclass = clazz.getSuperclass();
        if (superclass != null) {
            if (isPoolClassOrParentDifferent(superclass, cp)) {
                return true;
            }
        }
        Class<?>[] interfaces = clazz.getInterfaces();
        for (Class<?> interfaceClazz : interfaces) {
            if (isPoolClassOrParentDifferent(interfaceClazz, cp)) {
                return true;
            }
        }
        return false;
    }
}
