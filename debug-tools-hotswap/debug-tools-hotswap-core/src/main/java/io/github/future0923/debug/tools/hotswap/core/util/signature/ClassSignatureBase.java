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

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

/**
 * 类签名
 */
public abstract class ClassSignatureBase {

    private static final String[] IGNORED_METHODS = new String[]{"annotationType", "equals", "hashCode", "toString"};

    private final Set<ClassSignatureElement> elements = new HashSet<>();

    // java stores switch table to class field, signature should ingore it
    protected static final String SWITCH_TABLE_METHOD_PREFIX = "$SWITCH_TABLE$";

    protected static final String CLASS_CLINIT_METHOD_NAME = "$$ha$clinit";

    /**
     * 返回类的签名
     */
    public abstract String getValue() throws Exception;

    /**
     * 添加签名元素
     */
    public void addSignatureElements(ClassSignatureElement[] elems) {
        elements.addAll(Arrays.asList(elems));
    }

    /**
     * 是否设置了该签名元素
     */
    public boolean hasElement(ClassSignatureElement element) {
        return elements.contains(element);
    }

    /**
     * 将注解转为string
     */
    protected String annotationToString(Object[] anno) {
        if (anno == null) {
            return "null";
        }
        int iMax = anno.length - 1;
        if (iMax == -1) {
            return "[]";
        }
        anno = sort(anno);
        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; i < anno.length; i++) {
            Annotation object = (Annotation) anno[i];
            Method[] declaredMethods = object.getClass().getDeclaredMethods();
            b.append("(");
            boolean printComma = false;
            for (Method method : declaredMethods) {
                if (Arrays.binarySearch(IGNORED_METHODS, method.getName()) < 0) {
                    Object value = getAnnotationValue(object, method.getName());
                    if (value != null) {
                        if (printComma) {
                            b.append(",");
                        } else {
                            printComma = true;
                        }
                        if (value.getClass().isArray()) {
                            value = arrayToString(value);
                        }
                        b.append(method.getName()).append("=").append(value.getClass()).append(":").append(value);
                    }
                }
            }
            b.append(")");
            b.append(object.annotationType().getName());
            if (i < anno.length - 1) {
                b.append(",");
            }
        }
        b.append(']');
        return b.toString();
    }

    /**
     * 注解转string
     */
    protected String annotationToString(Object[][] anno) {
        if (anno == null) {
            return "null";
        }
        int iMax = anno.length - 1;
        if (iMax == -1) {
            return "[]";
        }
        anno = sort(anno);
        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; ; i++) {
            Object[] object = anno[i];
            b.append(annotationToString(object));
            if (i == iMax) {
                return b.append(']').toString();
            }
            b.append(", ");
        }
    }

    /**
     * 数组转为string
     */
    private Object arrayToString(Object value) {
        Object result = value;
        try {
            try {
                Method toStringMethod = Arrays.class.getMethod("toString", value.getClass());
                result = toStringMethod.invoke(null, value);
            } catch (NoSuchMethodException e) {
                if (value instanceof Object[]) {
                    Method toStringMethod = Arrays.class.getMethod("toString", Object[].class);
                    result = toStringMethod.invoke(null, value);
                }
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {
        }
        return result;
    }

    /**
     * 用ToString排序
     */
    private <T> T[] sort(T[] a) {
        a = Arrays.copyOf(a, a.length);
        Arrays.sort(a, ToStringComparator.INSTANCE);
        return a;
    }

    /**
     * 用ToString排序
     */
    private <T> T[][] sort(T[][] a) {
        a = Arrays.copyOf(a, a.length);
        Arrays.sort(a, ToStringComparator.INSTANCE);
        for (Object[] objects : a) {
            Arrays.sort(objects, ToStringComparator.INSTANCE);
        }
        return a;
    }

    /**
     * 获取注解的值
     *
     * @param annotation    注解
     * @param attributeName 方法
     * @return 值
     */
    private Object getAnnotationValue(Annotation annotation, String attributeName) {
        Method method = null;
        boolean acessibleSet = false;
        try {
            method = annotation.annotationType().getDeclaredMethod(attributeName);
            acessibleSet = makeAccessible(method);
            return method.invoke(annotation);
        } catch (Exception ex) {
            return null;
        } finally {
            if (method != null && acessibleSet) {
                method.setAccessible(false);
            }
        }
    }

    private boolean makeAccessible(Method method) {
        if ((!Modifier.isPublic(method.getModifiers()) || !Modifier.isPublic(method.getDeclaringClass().getModifiers()))
                && !method.isAccessible()) {
            method.setAccessible(true);
            return true;
        }
        return false;
    }

    protected static class ToStringComparator implements Comparator<Object> {

        public static final ToStringComparator INSTANCE = new ToStringComparator();

        @Override
        public int compare(Object o1, Object o2) {
            return o1.toString().compareTo(o2.toString());
        }
    }
}
