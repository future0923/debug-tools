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
package io.github.future0923.debug.tools.hotswap.core.plugin.classes.dto;

import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtField;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtMethod;
import io.github.future0923.debug.tools.hotswap.core.javassist.NotFoundException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 获取匿名类的签名以比较两个版本（重新加载前后）
 */
public class AnonymousClassInfo {
    String className;
    String classSignature;
    String methodSignature;
    String fieldsSignature;
    String enclosingMethodSignature;

    public AnonymousClassInfo(Class<?> c) {
        this.className = c.getName();
        StringBuilder classSignature = new StringBuilder(c.getSuperclass().getName());
        for (Class<?> interfaceClass : c.getInterfaces()) {
            classSignature.append(";");
            classSignature.append(interfaceClass.getName());
        }
        this.classSignature = classSignature.toString();
        StringBuilder methodsSignature = new StringBuilder();
        for (Method m : c.getDeclaredMethods()) {
            getMethodSignature(methodsSignature, m);
        }
        this.methodSignature = methodsSignature.toString();
        StringBuilder fieldsSignature = new StringBuilder();
        for (Field f : c.getDeclaredFields()) {
            fieldsSignature.append(f.getType().getName());
            fieldsSignature.append(" ");
            fieldsSignature.append(f.getName());
            fieldsSignature.append(";");
        }
        this.fieldsSignature = fieldsSignature.toString();
        StringBuilder enclosingMethodSignature = new StringBuilder();
        Method enclosingMethod = c.getEnclosingMethod();
        if (enclosingMethod != null) {
            getMethodSignature(enclosingMethodSignature, enclosingMethod);
        }
        this.enclosingMethodSignature = enclosingMethodSignature.toString();

    }

    public AnonymousClassInfo(String className) {
        this.className = className;
    }

    private void getMethodSignature(StringBuilder methodsSignature, Method m) {
        methodsSignature.append(m.getReturnType().getName());
        methodsSignature.append(" ");
        methodsSignature.append(m.getName());
        methodsSignature.append("(");
        for (Class<?> paramType : m.getParameterTypes()) {
            methodsSignature.append(paramType.getName());
        }
        methodsSignature.append(")");
        methodsSignature.append(";");
    }

    public AnonymousClassInfo(CtClass c) {
        try {
            this.className = c.getName();
            StringBuilder classSignature = new StringBuilder(c.getSuperclassName());
            for (CtClass interfaceCtClass : c.getInterfaces()) {
                classSignature.append(";");
                classSignature.append(interfaceCtClass.getName());
            }
            this.classSignature = classSignature.toString();
            StringBuilder methodsSignature = new StringBuilder();
            for (CtMethod m : c.getDeclaredMethods()) {
                getMethodSignature(methodsSignature, m);
            }
            this.methodSignature = methodsSignature.toString();
            StringBuilder fieldsSignature = new StringBuilder();
            for (CtField f : c.getDeclaredFields()) {
                fieldsSignature.append(f.getType().getName());
                fieldsSignature.append(" ");
                fieldsSignature.append(f.getName());
                fieldsSignature.append(";");
            }
            this.fieldsSignature = fieldsSignature.toString();

            StringBuilder enclosingMethodSignature = new StringBuilder();
            try {
                CtMethod enclosingMethod = c.getEnclosingMethod();
                if (enclosingMethod != null) {
                    getMethodSignature(enclosingMethodSignature, enclosingMethod);
                }
            } catch (Exception e) {
                // OK, enclosing method not defined or out of scope
            }
            this.enclosingMethodSignature = enclosingMethodSignature.toString();

        } catch (Throwable t) {
            throw new IllegalStateException("Error creating AnonymousClassInfo from " + c.getName(), t);
        }
    }

    private void getMethodSignature(StringBuilder methodsSignature, CtMethod m) throws NotFoundException {
        methodsSignature.append(m.getReturnType().getName());
        methodsSignature.append(" ");
        methodsSignature.append(m.getName());
        methodsSignature.append("(");
        for (CtClass paramType : m.getParameterTypes()) {
            methodsSignature.append(paramType.getName());
        }
        methodsSignature.append(")");
        methodsSignature.append(";");
    }

    public boolean matchExact(AnonymousClassInfo other) {
        return getClassSignature().equals(other.getClassSignature()) &&
                getMethodSignature().equals(other.getMethodSignature()) &&
                getFieldsSignature().equals(other.getFieldsSignature()) &&
                getEnclosingMethodSignature().equals(other.getEnclosingMethodSignature());
    }

    public boolean matchSignatures(AnonymousClassInfo other) {
        return getClassSignature().equals(other.getClassSignature()) &&
                getMethodSignature().equals(other.getMethodSignature()) &&
                getFieldsSignature().equals(other.getFieldsSignature());
    }

    public boolean matchClassSignature(AnonymousClassInfo other) {
        return getClassSignature().equals(other.getClassSignature());
    }

    public String getClassName() {
        return className;
    }

    public String getClassSignature() {
        return classSignature;
    }

    public String getMethodSignature() {
        return methodSignature;
    }

    public String getFieldsSignature() {
        return fieldsSignature;
    }

    public String getEnclosingMethodSignature() {
        return enclosingMethodSignature;
    }
}

