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
package io.github.future0923.debug.tools.base.trace;

import io.github.future0923.debug.tools.base.hutool.core.util.BooleanUtil;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

/**
 * 调用方法追踪
 *
 * @author future0923
 */
public class MethodTrace {

    /**
     * 栈
     */
    private static final ThreadLocal<Deque<MethodTreeNode>> stack = ThreadLocal.withInitial(ArrayDeque::new);

    /**
     * 结果
     */
    private static final ThreadLocal<List<MethodTreeNode>> resultList = ThreadLocal.withInitial(ArrayList::new);

    /**
     * sql追踪状态
     */
    private static final ThreadLocal<Boolean> traceSqlStatus = ThreadLocal.withInitial(() -> false);

    /**
     * 方法入栈
     */
    public static void enterMethod(String className, String classSimpleName, String methodName, String methodSignature) {
        enter(MethodTraceType.METHOD, className, classSimpleName, methodName, methodSignature, null);
    }

    /**
     * MyBatis入栈
     */
    public static void enterMyBatis(String className, String classSimpleName, String method, String methodSignature) {
        enter(MethodTraceType.MYBATIS, className, classSimpleName, method, methodSignature, null);
    }

    /**
     * SQL入栈
     */
    public static void enterSql(String sql) {
        enter(MethodTraceType.SQL, null, null, null, null, sql);
    }

    /**
     * 入栈
     */
    private static void enter(MethodTraceType traceType, String className, String classSimpleName, String methodName, String methodSignature, String sql) {
        MethodTreeNode node = new MethodTreeNode();
        node.setStart(System.currentTimeMillis());
        node.setTraceType(traceType);
        node.setClassName(className);
        node.setClassSimpleName(classSimpleName);
        node.setMethodName(methodName);
        node.setMethodSignature(methodSignature);
        node.setSql(sql);
        if (!stack.get().isEmpty()) {
            MethodTreeNode peek = stack.get().peek();
            if (peek != null) {
                peek.getChildren().add(node);
            }
        }
        stack.get().push(node);
    }

    /**
     * 出栈
     */
    public static void exit() {
        MethodTreeNode node = stack.get().pop();
        node.setEnd(System.currentTimeMillis());
        // 如果是顶层方法，构建 MethodTree 树结构并加入结果列表
        if (stack.get().isEmpty()) {
            MethodTreeNode tree = node.toTree();
            resultList.get().add(tree);
        }
    }

    /**
     * 出栈
     */
    public static void exit(long duration) {
        MethodTreeNode node = stack.get().pop();
        node.setDuration(duration);
        // 如果是顶层方法，构建 MethodTree 树结构并加入结果列表
        if (stack.get().isEmpty()) {
            MethodTreeNode tree = node.toTree();
            resultList.get().add(tree);
        }
    }

    /**
     * 获取完整结果
     */
    public static List<MethodTreeNode> getResult() {
        List<MethodTreeNode> result = resultList.get();
        clear();
        return result;
    }

    /**
     * 重置
     */
    private static void clear() {
        stack.remove();
        resultList.remove();
        traceSqlStatus.remove();
    }

    /**
     * 设置当前执行的追踪SQL状态
     */
    public static void setTraceSqlStatus(Boolean sql) {
        traceSqlStatus.set(BooleanUtil.isTrue(sql));
    }

    /**
     * 获取当前执行的追踪SQL状态
     */
    public static Boolean getTraceSqlStatus() {
        return traceSqlStatus.get();
    }

    /**
     * 生成方法签名
     */
    public static String genMethodSignature(Method method) {
        StringBuilder sb = new StringBuilder();
        // 方法名
        sb.append(method.getName());
        // 参数列表（支持泛型）
        sb.append("(");
        Type[] genericParameterTypes = method.getGenericParameterTypes();
        String paramList = Arrays.stream(genericParameterTypes)
                .map(MethodTrace::getTypeName)
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
        sb.append(paramList);
        sb.append(")");
        // 返回值（支持泛型）
        sb.append(": ").append(getTypeName(method.getGenericReturnType()));
        // 异常
        Type[] exceptionTypes = method.getGenericExceptionTypes();
        if (exceptionTypes.length > 0) {
            sb.append(" throws ");
            String exceptions = Arrays.stream(exceptionTypes)
                    .map(MethodTrace::getTypeName)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");
            sb.append(exceptions);
        }
        return sb.toString();
    }

    /**
     * 获取类型名称
     */
    private static String getTypeName(Type type) {
        if (type instanceof Class<?>) {
            return ((Class<?>) type).getSimpleName();
        } else if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            String raw = getTypeName(pt.getRawType());
            String args = Arrays.stream(pt.getActualTypeArguments())
                    .map(MethodTrace::getTypeName)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");
            return raw + "<" + args + ">";
        } else if (type instanceof GenericArrayType) {
            return getTypeName(((GenericArrayType) type).getGenericComponentType()) + "[]";
        } else if (type instanceof TypeVariable) {
            return ((TypeVariable<?>) type).getName();
        } else if (type instanceof WildcardType) {
            return "?";
        } else {
            return type.getTypeName();
        }
    }

}
