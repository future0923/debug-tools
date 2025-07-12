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
package io.github.future0923.debug.tools.hotswap.core.util;

import javassist.CtClass;
import javassist.bytecode.AnnotationsAttribute;

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
            for (javassist.bytecode.annotation.Annotation annot : attribute.getAnnotations()) {
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
