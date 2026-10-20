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
package io.github.future0923.debug.tools.server.utils;

import io.github.future0923.debug.tools.base.utils.DebugToolsClassUtils;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/** 有界结果检查：分页时只读取本页，路径先匹配真实字段后才访问值。 */
public final class ResultInspector {
    private ResultInspector() { }

    public static Map<String, Object> inspect(String path, String view, int offset, int limit, int maxDepth, int maxNodes) {
        if (path == null || path.isEmpty() || path.length() > 8192 || offset < 0 || offset > 100000 || limit < 1 || limit > 200
                || maxDepth < 1 || maxDepth > 20 || maxNodes < 1 || maxNodes > 2000) throw new IllegalArgumentException("Invalid inspection limits or offsetPath");
        if (!Arrays.asList("DEBUG", "CHILDREN", "JSON", "TO_STRING").contains(view)) throw new IllegalArgumentException("Unknown result view");
        String[] parts = path.split("/", -1);
        if (parts.length > 101) throw new IllegalArgumentException("Result path exceeds maximum depth");
        Object root = DebugToolsResultUtils.getValueByOffset(parts[0]);
        if (root == null) throw new NoSuchElementException("Result expired, released, or target JVM restarted");
        Object value = root;
        for (int i = 1; i < parts.length; i++) value = resolve(value, parts[i]);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("offsetPath", path);
        result.put("view", view);
        result.put("found", true);
        if ("CHILDREN".equals(view)) {
            List<Child> page = children(value, offset, limit + 1);
            boolean more = page.size() > limit;
            if (more) page = page.subList(0, limit);
            List<Map<String, Object>> nodes = new ArrayList<>();
            for (Child child : page) nodes.add(node(child.name, child.value, path + "/" + child.token));
            result.put("items", nodes);
            result.put("nextOffset", more ? offset + page.size() : null);
            result.put("truncated", more);
        } else if ("JSON".equals(view)) {
            Budget budget = new Budget(maxNodes);
            result.put("value", snapshot(value, path, maxDepth, budget, new IdentityHashMap<>()));
            result.put("truncated", budget.truncated);
        } else if ("TO_STRING".equals(view)) {
            String text = String.valueOf(value);
            result.put("value", text.length() > 65536 ? text.substring(0, 65536) : text);
            result.put("truncated", text.length() > 65536);
        } else {
            result.put("value", node("result", value, path));
            result.put("truncated", false);
        }
        return result;
    }

    /** 只接受已存在字段/元素对应的 token，不把任意整数当作 Unsafe 内存偏移。 */
    private static Object resolve(Object value, String token) {
        if (value == null) throw new IllegalArgumentException("Cannot expand a null value");
        if (value instanceof Map) {
            for (Object item : ((Map<?, ?>) value).entrySet()) {
                Map.Entry<?, ?> entry = (Map.Entry<?, ?>) item;
                if ((System.identityHashCode(entry.getKey()) + "@map").equals(token)) return entry;
            }
        } else if (value instanceof Collection || value.getClass().isArray()) {
            if (!token.matches("[0-9]+@collection")) throw new IllegalArgumentException("Invalid collection offset");
            int index = Integer.parseInt(token.substring(0, token.indexOf('@')));
            if (index > 100000) throw new IllegalArgumentException("Collection offset exceeds limit");
            if (value.getClass().isArray()) {
                if (index < Array.getLength(value)) return Array.get(value, index);
            } else {
                int i = 0;
                for (Object item : (Collection<?>) value) if (i++ == index) return item;
            }
        } else if (value instanceof Map.Entry) {
            Map.Entry<?, ?> entry = (Map.Entry<?, ?>) value;
            if ("0@map.entry".equals(token)) return entry.getKey();
            if ("1@map.entry".equals(token)) return entry.getValue();
        } else {
            for (Child child : children(value, 0, Integer.MAX_VALUE)) if (child.token.equals(token)) return child.value;
        }
        throw new IllegalArgumentException("Result field or element does not exist");
    }

    /** 枚举到页面末尾即停止，支持原生数组，避免 toArray 或完整 DTO 序列化。 */
    private static List<Child> children(Object value, int offset, int limit) {
        List<Child> result = new ArrayList<>();
        if (value == null || scalar(value)) return result;
        int i = 0;
        if (value instanceof Map) {
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
                if (i++ < offset) continue;
                result.add(new Child(display(entry.getKey()), entry.getValue(), System.identityHashCode(entry.getKey()) + "@map"));
                if (result.size() >= limit) break;
            }
        } else if (value instanceof Collection) {
            for (Object item : (Collection<?>) value) {
                int index = i++;
                if (index < offset) continue;
                result.add(new Child(String.valueOf(index), item, index + "@collection"));
                if (result.size() >= limit) break;
            }
        } else if (value.getClass().isArray()) {
            int end = (int) Math.min(Array.getLength(value), (long) offset + limit);
            for (i = offset; i < end; i++) result.add(new Child(String.valueOf(i), Array.get(value, i), i + "@collection"));
        } else if (value instanceof Map.Entry) {
            Map.Entry<?, ?> entry = (Map.Entry<?, ?>) value;
            if (offset == 0) result.add(new Child("key", entry.getKey(), "0@map.entry"));
            if (offset <= 1 && result.size() < limit) result.add(new Child("value", entry.getValue(), "1@map.entry"));
        } else if (isRecord(value.getClass())) {
            try {
                Object[] components = (Object[]) Class.class.getMethod("getRecordComponents").invoke(value.getClass());
                for (Object component : components) {
                    if (i++ < offset) continue;
                    String name = (String) component.getClass().getMethod("getName").invoke(component);
                    Method accessor = (Method) component.getClass().getMethod("getAccessor").invoke(component);
                    accessor.setAccessible(true);
                    result.add(new Child(name, accessor.invoke(value), name + "@record.component"));
                    if (result.size() >= limit) break;
                }
            } catch (ReflectiveOperationException error) { throw new IllegalArgumentException("Cannot read record components", error); }
        } else {
            for (Field field : DebugToolsClassUtils.getAllDeclaredFields(value.getClass())) {
                if (Modifier.isStatic(field.getModifiers())) continue;
                if (i++ < offset) continue;
                result.add(new Child(field.getName(), DebugToolsResultUtils.getValueByField(value, field), DebugToolsResultUtils.getObjectFieldOffset(field)));
                if (result.size() >= limit) break;
            }
        }
        return result;
    }

    private static Map<String, Object> node(String name, Object value, String path) {
        Map<String, Object> node = new LinkedHashMap<>();
        node.put("name", name); node.put("value", display(value)); node.put("filedOffset", path);
        node.put("valueClassName", value == null ? null : value.getClass().getName());
        node.put("leaf", value == null || scalar(value));
        return node;
    }

    /** JSON 快照使用深度/节点预算与循环引用标记，不调用对象 getter。 */
    private static Object snapshot(Object value, String path, int depth, Budget budget, IdentityHashMap<Object, String> visited) {
        if (budget.remaining-- <= 0) { budget.truncated = true; return Collections.singletonMap("$truncated", true); }
        if (value == null) return null;
        if (scalar(value)) {
            if (value instanceof String && ((String) value).length() > 4096) budget.truncated = true;
            return value instanceof String || value instanceof Character || value instanceof Enum ? display(value) : value;
        }
        if (visited.containsKey(value)) return Collections.singletonMap("$ref", visited.get(value));
        if (depth <= 0) { budget.truncated = true; return node("value", value, path); }
        visited.put(value, path);
        List<Child> children = children(value, 0, Math.max(1, budget.remaining + 1));
        boolean array = value instanceof Collection || value.getClass().isArray();
        List<Object> list = new ArrayList<>();
        Map<String, Object> object = new LinkedHashMap<>();
        for (Child child : children) {
            if (budget.remaining <= 0) { budget.truncated = true; break; }
            Object item = snapshot(child.value, path + "/" + child.token, depth - 1, budget, visited);
            if (array) list.add(item); else object.put(child.name, item);
        }
        return array ? list : object;
    }

    private static boolean scalar(Object value) {
        return value instanceof String || value instanceof Number || value instanceof Boolean || value instanceof Character || value instanceof Enum;
    }
    private static String display(Object value) {
        if (value == null) return null;
        String text = scalar(value) ? (value instanceof Enum ? ((Enum<?>) value).name() : String.valueOf(value))
                : value.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(value));
        return text.length() > 4096 ? text.substring(0, 4096) : text;
    }
    private static boolean isRecord(Class<?> type) {
        try { return Boolean.TRUE.equals(Class.class.getMethod("isRecord").invoke(type)); }
        catch (ReflectiveOperationException ignored) { return false; }
    }
    private static final class Child {
        final String name; final Object value; final String token;
        Child(String name, Object value, String token) { this.name = name; this.value = value; this.token = token; }
    }
    private static final class Budget {
        int remaining; boolean truncated;
        Budget(int remaining) { this.remaining = remaining; }
    }
}
