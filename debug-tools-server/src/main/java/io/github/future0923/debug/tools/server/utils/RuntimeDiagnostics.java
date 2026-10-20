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

import io.github.future0923.debug.tools.common.protocal.http.AllClassLoaderRes;
import java.lang.management.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/** 只读运行时快照；查询预算在目标 JVM 内生效，不初始化业务类或调用业务 getter。 */
public final class RuntimeDiagnostics {
    private RuntimeDiagnostics() { }

    public static Map<String, Object> jvm() {
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        MemoryMXBean memory = ManagementFactory.getMemoryMXBean();
        ClassLoadingMXBean classes = ManagementFactory.getClassLoadingMXBean();
        List<Object> gc = new ArrayList<>();
        for (GarbageCollectorMXBean collector : ManagementFactory.getGarbageCollectorMXBeans()) {
            gc.add(map("name", collector.getName(), "count", collector.getCollectionCount(), "timeMillis", collector.getCollectionTime()));
        }
        return map("timestamp", System.currentTimeMillis(), "jvmName", runtime.getName(), "vmName", runtime.getVmName(),
            "vmVersion", runtime.getVmVersion(), "startTime", runtime.getStartTime(), "uptimeMillis", runtime.getUptime(),
            "heap", memory(memory.getHeapMemoryUsage()), "nonHeap", memory(memory.getNonHeapMemoryUsage()),
            "threads", ManagementFactory.getThreadMXBean().getThreadCount(), "loadedClasses", classes.getLoadedClassCount(),
            "unloadedClasses", classes.getUnloadedClassCount(), "gc", gc,
            "availableProcessors", ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors(),
            "systemLoadAverage", ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage());
    }

    private static Map<String, Object> memory(MemoryUsage usage) {
        return map("used", usage.getUsed(), "committed", usage.getCommitted(), "max", usage.getMax());
    }

    public static Map<String, Object> threads(int offset, int limit, int depth) {
        bounds(offset, limit, 256);
        if (depth < 0 || depth > 128) throw new IllegalArgumentException("maxDepth must be between 0 and 128");
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        long[] all = bean.getAllThreadIds();
        Arrays.sort(all);
        long[] selected = Arrays.copyOfRange(all, Math.min(offset, all.length), Math.min(offset + limit, all.length));
        long[] deadlocked;
        String detection = bean.isSynchronizerUsageSupported() ? "MONITORS_AND_SYNCHRONIZERS" : "MONITORS";
        deadlocked = bean.isSynchronizerUsageSupported() ? bean.findDeadlockedThreads() : bean.findMonitorDeadlockedThreads();
        List<Object> items = new ArrayList<>();
        for (ThreadInfo info : bean.getThreadInfo(selected, depth)) {
            if (info == null) continue; // 线程可在两次查询之间退出。
            List<String> stack = new ArrayList<>();
            for (StackTraceElement frame : info.getStackTrace()) stack.add(frame.toString());
            items.add(map("id", info.getThreadId(), "name", info.getThreadName(), "state", info.getThreadState().name(),
                "lockName", info.getLockName(), "lockOwnerId", info.getLockOwnerId(), "lockOwnerName", info.getLockOwnerName(),
                "blockedCount", info.getBlockedCount(), "waitedCount", info.getWaitedCount(), "stack", stack,
                "stackMayBeTruncated", stack.size() == depth));
        }
        int deadlockCount = deadlocked == null ? 0 : deadlocked.length;
        return map("timestamp", System.currentTimeMillis(), "total", all.length, "items", items,
            "nextOffset", offset + selected.length < all.length ? offset + selected.length : null,
            "deadlockDetection", detection, "deadlockedThreadIds", deadlocked == null ? new long[0] : Arrays.copyOf(deadlocked, Math.min(256, deadlockCount)),
            "deadlockCount", deadlockCount, "deadlocksTruncated", deadlockCount > 256);
    }

    /** 仅枚举 Instrumentation 已加载类，保留同名不同 ClassLoader 的每个候选。 */
    public static Map<String, Object> classes(Class<?>[] loaded, String className, String contains, String loader, int offset, int limit, int methodLimit) {
        bounds(offset, limit, 100);
        if (methodLimit < 0 || methodLimit > 200) throw new IllegalArgumentException("methodLimit must be between 0 and 200");
        if ((className == null || className.isEmpty()) && (contains == null || contains.length() < 3)) {
            throw new IllegalArgumentException("Provide exact className or nameContains with at least three characters");
        }
        List<Object> items = new ArrayList<>();
        int matched = 0;
        for (Class<?> type : loaded) {
            if (className != null && !className.equals(type.getName())) continue;
            if (contains != null && !type.getName().contains(contains)) continue;
            String identity = type.getClassLoader() == null ? "bootstrap" : new AllClassLoaderRes.Item(type.getClassLoader()).getIdentity();
            if (loader != null && !loader.equals(identity)) continue;
            if (matched++ < offset || items.size() >= limit) continue;
            Map<String, Object> item = map("className", type.getName(), "classLoaderIdentity", identity,
                "classLoaderName", type.getClassLoader() == null ? "bootstrap" : type.getClassLoader().getClass().getName());
            try {
                java.security.CodeSource source = type.getProtectionDomain().getCodeSource();
                item.put("codeSource", source == null || source.getLocation() == null ? null : source.getLocation().toExternalForm());
                item.put("superClass", type.getSuperclass() == null ? null : type.getSuperclass().getName());
                if (methodLimit > 0) {
                    Method[] methods = type.getDeclaredMethods();
                    Arrays.sort(methods, Comparator.comparing(Method::toString));
                    List<Object> names = new ArrayList<>();
                    for (int i = 0; i < Math.min(methodLimit, methods.length); i++) {
                        Method method = methods[i];
                        List<String> parameters = new ArrayList<>();
                        for (Class<?> parameter : method.getParameterTypes()) parameters.add(parameter.getName());
                        names.add(map("name", method.getName(), "parameterTypes", parameters, "returnType", method.getReturnType().getName(),
                            "modifiers", Modifier.toString(method.getModifiers()), "bridge", method.isBridge(), "synthetic", method.isSynthetic()));
                    }
                    item.put("methods", names); item.put("methodCount", methods.length); item.put("methodsTruncated", methods.length > methodLimit);
                }
            } catch (LinkageError | SecurityException error) { item.put("metadataError", error.getClass().getName() + ": " + error.getMessage()); }
            items.add(item);
        }
        return map("items", items, "count", matched, "nextOffset", offset + items.size() < matched ? offset + items.size() : null,
            "scope", "ALREADY_LOADED_CLASSES", "snapshotStable", false);
    }

    public static void bounds(int offset, int limit, int max) {
        if (offset < 0 || offset > 1000000 || limit < 1 || limit > max) throw new IllegalArgumentException("Invalid offset/limit");
    }
    public static Map<String, Object> map(Object... entries) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (int i = 0; i < entries.length; i += 2) result.put((String) entries[i], entries[i + 1]);
        return result;
    }
}
