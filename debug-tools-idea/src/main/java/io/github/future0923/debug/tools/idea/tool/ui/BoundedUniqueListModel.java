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
package io.github.future0923.debug.tools.idea.tool.ui;

import io.github.future0923.debug.tools.base.hutool.core.map.MapUtil;
import lombok.Getter;

import javax.swing.*;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 受限容量、去重、置顶（LRU）语义的 ListModel（通过 String key 判重）：
 * - addFirst(element, key): 若 key 已存在则置顶并更新元素；否则加入最前；
 * - 超过容量时自动淘汰最老的（显示层最后一行）；
 * - JBList 显示顺序：索引 0 = 最新元素。
 */
public class BoundedUniqueListModel<T> extends AbstractListModel<T> {

    private final int capacity = 50;

    @Getter
    private final LinkedHashMap<String, T> map;

    public BoundedUniqueListModel() {
        this.map = new LinkedHashMap<>(50, 0.75f, true);
    }

    public BoundedUniqueListModel(LinkedHashMap<String, T> map) {
        this.map = map;
    }

    @Override
    public int getSize() {
        return map.size();
    }

    /**
     * 为了让 index=0 是“最新”，需要反向读取：
     * 目标 = 迭代的倒数第 (index+1) 个。
     */
    @Override
    public T getElementAt(int index) {
        int size = map.size();
        if (index < 0 || index >= size) return null;
        int target = size - 1 - index;
        Iterator<T> it = map.values().iterator();
        for (int i = 0; it.hasNext(); i++) {
            T v = it.next();
            if (i == target) return v;
        }
        return null;
    }

    /**
     * 头插（置顶）：按 key 判重；存在则置顶并更新元素；超容量淘汰最老
     */
    public void addFirst(T element, String key) {
        if (element == null || key == null) return;

        // 已存在则先移除（更新访问顺序），再放入成为“最新”
        map.remove(key);
        map.put(key, element);

        // 超容量：淘汰最老（迭代首个）
        if (map.size() > capacity) {
            Iterator<String> it = map.keySet().iterator();
            if (it.hasNext()) {
                it.next();
                it.remove();
            }
        }
        // 容量通常很小（如 5），整体刷新简单稳妥
        fireContentsChanged(this, 0, Math.max(0, map.size() - 1));
    }

    /**
     * 批量头插：elements 与 keys 一一对应（后面的会更“新”）
     */
    public void addFirst(Map<T, String> elements) {
        if (MapUtil.isEmpty(elements)) {
            return;
        }
        elements.forEach(this::addFirst);
    }

    /**
     * 从尾部删除一个（即最老的；显示层最后一行）
     */
    public void removeLast() {
        if (map.isEmpty()) return;
        Iterator<String> it = map.keySet().iterator();
        if (it.hasNext()) {
            it.next();
            it.remove();
            fireContentsChanged(this, 0, Math.max(0, map.size() - 1));
        }
    }

    /**
     * 清空
     */
    public void clear() {
        int old = map.size();
        if (old == 0) return;
        map.clear();
        fireIntervalRemoved(this, 0, old - 1);
    }

    /**
     * 容量
     */
    public int capacity() {
        return capacity;
    }

    /**
     * 是否包含 key
     */
    public boolean containsKey(String key) {
        return map.containsKey(key);
    }

    /**
     * 可选：根据 key 取值（不改变“最近访问”顺序）
     */
    public T getByKey(String key) {
        return map.get(key);
    }
}