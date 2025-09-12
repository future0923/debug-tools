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
import io.github.future0923.debug.tools.base.hutool.core.util.StrUtil;
import io.github.future0923.debug.tools.idea.search.beans.HttpUrlItem;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author future0923
 */
public class HttpUrlUtils {

    /**
     * 获取项目中所有的请求信息
     *
     * @param project 项目
     */
    public static Map<String, HttpUrlItem[]> getAllRequest(Project project) {
        return Arrays.stream(ModuleManager.getInstance(project).getModules())
                .map(m -> getModuleAllRequests(project, m))
                .flatMap(List::stream)
                .filter(item -> item != null && StrUtil.isNotBlank(item.getPath()))
                .collect(Collectors.groupingBy(
                        HttpUrlItem::getPath,
                        LinkedHashMap::new,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> list.toArray(new HttpUrlItem[0])
                        )
                ));
    }

    /**
     * 获取模块下的所有请求
     *
     * @param project 项目
     * @param module  模块
     * @return 请求集合
     */
    public static List<HttpUrlItem> getModuleAllRequests(Project project, Module module) {
        return SpringUtils.getSpringRequestByModule(project, module);
    }
}
