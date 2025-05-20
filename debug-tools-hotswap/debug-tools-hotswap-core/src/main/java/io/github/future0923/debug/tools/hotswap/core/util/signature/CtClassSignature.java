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

import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtConstructor;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtField;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtMethod;
import io.github.future0923.debug.tools.hotswap.core.javassist.NotFoundException;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * javassist类签名
 */
public class CtClassSignature extends ClassSignatureBase {

    private final CtClass ctClass;

    public CtClassSignature(CtClass ctClass) {
        this.ctClass = ctClass;
    }

    @Override
    public String getValue() throws Exception {
        List<String> strings = new ArrayList<>();

        if (hasElement(ClassSignatureElement.METHOD)) {
            boolean usePrivateMethod = hasElement(ClassSignatureElement.METHOD_PRIVATE);
            boolean useStaticMethod = hasElement(ClassSignatureElement.METHOD_STATIC);
            for (CtMethod method : ctClass.getDeclaredMethods()) {
                if (!usePrivateMethod && Modifier.isPrivate(method.getModifiers())) {
                    continue;
                }
                if (!useStaticMethod && Modifier.isStatic(method.getModifiers())) {
                    continue;
                }
                if (method.getName().startsWith(SWITCH_TABLE_METHOD_PREFIX)
                        || method.getName().startsWith(CLASS_CLINIT_METHOD_NAME)) {
                    continue;
                }
                strings.add(getMethodString(method));
            }
        }

        if (hasElement(ClassSignatureElement.CONSTRUCTOR)) {
            boolean usePrivateConstructor = hasElement(ClassSignatureElement.CONSTRUCTOR_PRIVATE);
            for (CtConstructor method : ctClass.getDeclaredConstructors()) {
                if (!usePrivateConstructor && Modifier.isPrivate(method.getModifiers())) {
                    continue;
                }
                strings.add(getConstructorString(method));
            }
        }

        if (hasElement(ClassSignatureElement.CLASS_ANNOTATION)) {
            strings.add(annotationToString(ctClass.getAvailableAnnotations()));
        }

        if (hasElement(ClassSignatureElement.INTERFACES)) {
            for (CtClass iClass : ctClass.getInterfaces()) {
                strings.add(iClass.getName());
            }
        }

        if (hasElement(ClassSignatureElement.SUPER_CLASS)) {
            String superclassName = ctClass.getSuperclassName();
            if (superclassName != null && !superclassName.equals(Object.class.getName()))
                strings.add(superclassName);
        }

        if (hasElement(ClassSignatureElement.FIELD)) {
            boolean useStaticField = hasElement(ClassSignatureElement.FIELD_STATIC);
            boolean useFieldAnnotation = hasElement(ClassSignatureElement.FIELD_ANNOTATION);
            for (CtField field : ctClass.getDeclaredFields()) {
                if (!useStaticField && Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                if (field.getName().startsWith(SWITCH_TABLE_METHOD_PREFIX)) {
                    continue;
                }
                String fieldSignature = field.getType().getName() + " " + field.getName();
                if (useFieldAnnotation) {
                    fieldSignature += annotationToString(field.getAvailableAnnotations());
                }

                strings.add(fieldSignature + ";");
            }
        }
        Collections.sort(strings);
        StringBuilder strBuilder = new StringBuilder();
        for (String methodString : strings) {
            strBuilder.append(methodString);
        }
        return strBuilder.toString();
    }

    private String getName(CtClass ctClass) {
        return ctClass.getName();
    }

    private String getConstructorString(CtConstructor method) throws ClassNotFoundException, NotFoundException {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(Modifier.toString(method.getModifiers())).append(" ");
        strBuilder.append(method.getDeclaringClass().getName());
        strBuilder.append(getParams(method.getParameterTypes()));
        if (hasElement(ClassSignatureElement.METHOD_ANNOTATION)) {
            strBuilder.append(annotationToString(method.getAvailableAnnotations()));
        }
        if (hasElement(ClassSignatureElement.METHOD_PARAM_ANNOTATION)) {
            strBuilder.append(annotationToString(method.getAvailableParameterAnnotations()));
        }
        if (hasElement(ClassSignatureElement.METHOD_EXCEPTION)) {
            strBuilder.append(toStringException(method.getExceptionTypes()));
        }
        strBuilder.append(";");
        return strBuilder.toString();
    }

    private String getMethodString(CtMethod method) throws NotFoundException, ClassNotFoundException {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(Modifier.toString(method.getModifiers()) + " ");
        strBuilder.append(getName(method.getReturnType()) + " " + method.getName());
        strBuilder.append(getParams(method.getParameterTypes()));
        if (hasElement(ClassSignatureElement.METHOD_ANNOTATION))
            strBuilder.append(annotationToString(method.getAvailableAnnotations()));
        if (hasElement(ClassSignatureElement.METHOD_PARAM_ANNOTATION))
            strBuilder.append(annotationToString(method.getAvailableParameterAnnotations()));
        if (hasElement(ClassSignatureElement.METHOD_EXCEPTION))
            strBuilder.append(toStringException(method.getExceptionTypes()));
        strBuilder.append(";");
        return strBuilder.toString();
    }

    private String getParams(CtClass[] ctClasses) {
        StringBuilder strBuilder = new StringBuilder("(");
        boolean first = true;
        for (CtClass ctClass : ctClasses) {
            if (!first)
                strBuilder.append(",");
            else
                first = false;
            strBuilder.append(getName(ctClass));
        }
        strBuilder.append(")");
        return strBuilder.toString();
    }

    private String toStringException(CtClass[] a) {
        if (a == null)
            return "null";
        int iMax = a.length - 1;
        if (iMax == -1)
            return "[]";
        a = sort(a);

        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; ; i++) {
            b.append("class " + a[i].getName());
            if (i == iMax)
                return b.append(']').toString();
            b.append(", ");
        }
    }

    private CtClass[] sort(CtClass[] a) {
        a = Arrays.copyOf(a, a.length);
        Arrays.sort(a, CtClassComparator.INSTANCE);
        return a;
    }

    private static class CtClassComparator implements Comparator<CtClass> {
        public static final CtClassComparator INSTANCE = new CtClassComparator();

        @Override
        public int compare(CtClass o1, CtClass o2) {
            return o1.getName().compareTo(o2.getName());
        }
    }
}
