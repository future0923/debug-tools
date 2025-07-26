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
package io.github.future0923.debug.tools.idea.utils;

import com.intellij.openapi.project.Project;
import io.github.future0923.debug.tools.common.protocal.http.AllClassLoaderRes;
import io.github.future0923.debug.tools.idea.ui.combobox.ClassLoaderComboBox;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author future0923
 */
public class StateUtils {

    /**
     * 项目打开时间
     */
    private static final Map<Project, Long> PROJECT_OPEN_TIME_MAP = new ConcurrentHashMap<>();

    /**
     * 项目默认类加载器
     */
    private static final Map<Project, AllClassLoaderRes.Item> PROJECT_DEFAULT_CLASSLOADER_MAP = new ConcurrentHashMap<>();

    /**
     * 项目默认类加载器选择框
     */
    private static final Map<Project, ClassLoaderComboBox> CLASS_LOADER_COMBO_BOX_MAP = new ConcurrentHashMap<>();

    /**
     * 项目跟踪方法
     */
    private static final Map<Project, Set<String>> TRACE_METHOD_MAP = new ConcurrentHashMap<>();

    public static void setProjectOpenTime(Project project) {
        PROJECT_OPEN_TIME_MAP.put(project, System.currentTimeMillis());
    }

    public static Long getProjectOpenTime(Project project) {
        return PROJECT_OPEN_TIME_MAP.getOrDefault(project, System.currentTimeMillis());
    }

    public static void setProjectDefaultClassLoader(Project project, AllClassLoaderRes.Item identity) {
        PROJECT_DEFAULT_CLASSLOADER_MAP.put(project, identity);
    }

    public static AllClassLoaderRes.Item getProjectDefaultClassLoader(Project project) {
        return PROJECT_DEFAULT_CLASSLOADER_MAP.get(project);
    }

    public static ClassLoaderComboBox getClassLoaderComboBox(Project project) {
        return CLASS_LOADER_COMBO_BOX_MAP.computeIfAbsent(project, ClassLoaderComboBox::new);
    }

    public static Set<String> getTraceMethod(Project project) {
        return TRACE_METHOD_MAP.get(project);
    }

    public static void setTraceMethod(Project project, String traceMethod) {
        TRACE_METHOD_MAP.computeIfAbsent(project, k -> ConcurrentHashMap.newKeySet()).add(traceMethod);
    }

    public static void removeTraceMethod(Project project, String qualifierMethod) {
        Set<String> traceMethod = getTraceMethod(project);
        if (traceMethod != null) {
            traceMethod.remove(qualifierMethod);
        }
    }
}
