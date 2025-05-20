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
package io.github.future0923.debug.tools.idea.search.utils;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import io.github.future0923.debug.tools.idea.search.beans.HttpUrlInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author future0923
 */
public class HttpUrlUtils {

    /**
     * 获取项目中所有的请求信息
     *
     * @param project 项目
     */
    public static Map<String, List<HttpUrlInfo>> getAllRequest(Project project) {
        Map<String, List<HttpUrlInfo>> map = new HashMap<>();
        Module[] modules = ModuleManager.getInstance(project).getModules();
        for (Module module : modules) {
            List<HttpUrlInfo> requestInfos = getModuleAllRequests(project, module);
            if (requestInfos.isEmpty()) {
                continue;
            }
            map.put(module.getName(), requestInfos);
        }
        return map;
    }

    /**
     * 获取模块下的所有请求
     *
     * @param project 项目
     * @param module  模块
     * @return 请求集合
     */
    public static List<HttpUrlInfo> getModuleAllRequests(Project project, Module module) {
        List<HttpUrlInfo> httpUrlInfos = new ArrayList<>();
        List<HttpUrlInfo> springRequestInfoByModule = SpringUtils.getSpringRequestByModule(project, module);
        if (!springRequestInfoByModule.isEmpty()) {
            httpUrlInfos.addAll(springRequestInfoByModule);
        }
        return httpUrlInfos;
    }
}
