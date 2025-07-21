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

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 方法树节点
 *
 * @author future0923
 */
@Data
public class MethodTreeNode {

    /**
     * 追踪类型
     */
    private MethodTraceType traceType;

    /**
     * 全类名
     */
    private String className;

    /**
     * 简类名
     */
    private String classSimpleName;

    /**
     * 方法名
     */
    private String methodName;

    /**
     * 方法签名
     */
    private String methodSignature;

    /**
     * sql
     */
    private String sql;

    /**
     * 耗时
     */
    private Long duration;

    /**
     * 方法开始时间
     */
    private Long start;

    /**
     * 方法结束时间
     */
    private Long end;

    /**
     * 子节点
     */
    private List<MethodTreeNode> children = new ArrayList<>();

    /**
     * 转换成树结构
     *
     * @return 树结构
     */
    public MethodTreeNode toTree() {
        MethodTreeNode tree = new MethodTreeNode();
        tree.setStart(start);
        tree.setTraceType(traceType);
        tree.setClassName(className);
        tree.setClassSimpleName(classSimpleName);
        tree.setMethodName(methodName);
        tree.setMethodSignature(methodSignature);
        tree.setSql(sql);
        if (duration == null) {
            duration = end - start;
        }
        tree.setDuration(duration);
        for (MethodTreeNode child : children) {
            tree.getChildren().add(child.toTree());
        }
        return tree;
    }
}
