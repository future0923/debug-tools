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

import java.util.ArrayDeque;
import java.util.ArrayList;
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

    public static void enterMethod(String className, String classSimpleName, String method) {
        enter(MethodTraceType.METHOD, className, classSimpleName, method, null);
    }

    public static void enterMyBatis(String className, String classSimpleName, String method) {
        enter(MethodTraceType.MYBATIS, className, classSimpleName, method, null);
    }

    public static void enterSql(String sql) {
        enter(MethodTraceType.SQL, null, null, null, sql);
    }

    /**
     * 入栈
     */
    private static void enter(MethodTraceType traceType, String className, String classSimpleName, String method, String sql) {
        MethodTreeNode node = new MethodTreeNode();
        node.setStart(System.currentTimeMillis());
        node.setTraceType(traceType);
        node.setClassName(className);
        node.setClassSimpleName(classSimpleName);
        node.setMethodName(method);
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
    }

}
