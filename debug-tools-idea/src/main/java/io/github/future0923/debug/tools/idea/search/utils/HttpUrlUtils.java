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
