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
package io.github.future0923.debug.tools.hotswap.core.util;

import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.AnnotationsAttribute;

import java.lang.annotation.Annotation;

/**
 * 注解工具
 */
public class AnnotationHelper {

    /**
     * 类中是否有指定注解
     *
     * @param clazz           验证的类
     * @param annotationClass 注解Class全类名
     */
    public static boolean hasAnnotation(Class<?> clazz, String annotationClass) {
        for (Annotation annot : clazz.getDeclaredAnnotations()) {
            if (annot.annotationType().getName().equals(annotationClass)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 类中是否有指定注解
     *
     * @param clazz           验证的类
     * @param annotationClass 注解Class全类名
     */
    public static boolean hasAnnotation(CtClass clazz, String annotationClass) {
        AnnotationsAttribute attribute = (AnnotationsAttribute) clazz.getClassFile2().
                getAttribute(AnnotationsAttribute.visibleTag);
        if (attribute != null) {
            for (io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.annotation.Annotation annot : attribute.getAnnotations()) {
                if (annot.getTypeName().equals(annotationClass)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 类中是否有指定注解
     *
     * @param clazz             验证的类
     * @param annotationClasses 注解Class全类名集合
     */
    public static boolean hasAnnotation(Class<?> clazz, Iterable<String> annotationClasses) {
        for (String pathAnnotation : annotationClasses) {
            if (AnnotationHelper.hasAnnotation(clazz, pathAnnotation)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 类中是否有指定注解
     *
     * @param clazz             验证的类
     * @param annotationClasses 注解Class全类名集合
     */
    public static boolean hasAnnotation(CtClass clazz, Iterable<String> annotationClasses) {
        for (String pathAnnotation : annotationClasses) {
            if (AnnotationHelper.hasAnnotation(clazz, pathAnnotation)) {
                return true;
            }
        }
        return false;
    }

}
