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
package io.github.future0923.debug.tools.idea.utils;

import com.intellij.openapi.project.Project;
import io.github.future0923.debug.tools.common.protocal.http.AllClassLoaderRes;

import java.util.Map;
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
}
