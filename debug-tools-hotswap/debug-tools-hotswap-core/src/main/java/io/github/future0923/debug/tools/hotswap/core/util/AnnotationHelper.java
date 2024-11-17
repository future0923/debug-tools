/*
 * Copyright 2013-2024 the HotswapAgent authors.
 *
 * This file is part of HotswapAgent.
 *
 * HotswapAgent is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 2 of the License, or (at your
 * option) any later version.
 *
 * HotswapAgent is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with HotswapAgent. If not, see http://www.gnu.org/licenses/.
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
