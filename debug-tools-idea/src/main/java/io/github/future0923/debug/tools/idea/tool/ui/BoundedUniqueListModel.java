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
import java.util.function.Consumer;

/**
 * 受限容量、去重、置顶（LRU）语义的 ListModel（通过 String key 判重）：
 * - addFirst(element, key): 若 key 已存在则置顶并更新元素；否则加入最前；
 * - 超过容量时自动淘汰最老的（显示层最后一行）；
 * - JBList 显示顺序：索引 0 = 最新元素。
 */
public class BoundedUniqueListModel<T> extends AbstractListModel<T> {

    private final int capacity = 500;

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

        int oldIndex = indexOfKey(key);
        boolean existed = oldIndex >= 0;

        if (existed) {
            // 移除旧的
            map.remove(key);
        }

        // 插入到“最新”
        map.put(key, element);

        // 淘汰超容量
        if (map.size() > capacity) {
            Iterator<String> it = map.keySet().iterator();
            if (it.hasNext()) {
                it.next();
                it.remove();
            }
        }

        if (existed) {
            // 原来位置删除
            fireIntervalRemoved(this, oldIndex, oldIndex);
        }
        // 新位置插入
        fireIntervalAdded(this, 0, 0);
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

    /** 计算“显示层索引”（index=0 表示最新），不存在返回 -1 */
    public int indexOfKey(String key) {
        if (key == null || map.isEmpty()) return -1;
        int i = 0, size = map.size();
        // 你是反向显示，所以第一个迭代的是“最老”，最后一个是“最新”
        for (String k : map.keySet()) {
            if (key.equals(k)) {
                // 迭代序 i 对应显示层 index = size-1-i
                return size - 1 - i;
            }
            i++;
        }
        return -1;
    }

    /** 仅刷新某一行（不改顺序不替换对象），用于“在原对象上改字段”的场景 */
    private void refreshRowByKey(JList<T> list, String key) {
        int row = indexOfKey(key);
        if (row < 0) return;
        // 方式A：标准事件（触发 renderer 重跑）
        fireContentsChanged(this, row, row);

        // 方式B：补一手精确重绘（某些 LnF 里重绘更及时）
        if (list != null) {
            java.awt.Rectangle r = list.getCellBounds(row, row);
            if (r != null) list.repaint(r);
        }
    }

    /** 替换为新值，但保持当前位置（不置顶） */
    public void replaceByKey(String key, T newValue) {
        if (key == null || newValue == null) return;
        if (!map.containsKey(key)) return;
        int row = indexOfKey(key);
        map.put(key, newValue);         // 不改顺序
        if (row >= 0) fireContentsChanged(this, row, row);
    }

    /** 替换并置顶（语义同 addFirst 的“去重 + 置顶”），触发整体刷新 */
    public void replaceAndMoveToTop(String key, T newValue) {
        if (key == null || newValue == null) return;
        map.remove(key);
        map.put(key, newValue);
        if (map.size() > capacity) {
            Iterator<String> it = map.keySet().iterator();
            if (it.hasNext()) {
                it.next(); it.remove();
            }
        }
        fireContentsChanged(this, 0, Math.max(0, map.size() - 1));
    }

    /** 对某条记录做“原位变更”（Consumer 内修改字段），然后刷新这一行 */
    public void mutateInPlace(String key, Consumer<T> mutator, JList<T> list) {
        if (key == null || mutator == null) return;
        T v = map.get(key);
        if (v == null) return;
        mutator.accept(v);
        refreshRowByKey(list, key);
    }

    /**
     * 删除指定 key 的元素
     */
    public void removeByKey(String key) {
        if (key == null) return;
        int index = indexOfKey(key);
        if (index < 0) return;

        map.remove(key);
        // 通知列表：这行没了
        fireIntervalRemoved(this, index, index);
    }

    /**
     * 删除指定 index 的元素（显示层 index）
     */
    public void removeAt(int index) {
        T element = getElementAt(index);
        if (element == null) return;

        // 找 key：由于 LinkedHashMap 是 key->value，需要反查
        String keyToRemove = null;
        int size = map.size();
        int target = size - 1 - index;
        int i = 0;
        for (String k : map.keySet()) {
            if (i == target) {
                keyToRemove = k;
                break;
            }
            i++;
        }

        if (keyToRemove != null) {
            map.remove(keyToRemove);
            fireIntervalRemoved(this, index, index);
        }
    }
}