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
package io.github.future0923.debug.tools.idea.search;

import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import io.github.future0923.debug.tools.idea.search.beans.HttpUrlInfo;
import io.github.future0923.debug.tools.idea.search.beans.HttpUrlItem;
import io.github.future0923.debug.tools.idea.search.utils.HttpUrlUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 提供HttpUrl搜索的数据
 *
 * @author future0923
 */
public class HttpUrlContributor implements ChooseByNameContributor {

    private List<HttpUrlItem> itemList;

    /**
     * 返回可供搜索的 元素名称列表
     */
    @Override
    public String @NotNull [] getNames(Project project, boolean includeNonProjectItems) {
        List<HttpUrlInfo> requestInfos = HttpUrlUtils.getAllRequest(project).values().stream().flatMap(Collection::stream).toList();
        List<String> names = new ArrayList<>(requestInfos.size());
        itemList = new ArrayList<>(requestInfos.size());
        for (HttpUrlInfo requestInfo : requestInfos) {
            HttpUrlItem httpUrlItem = new HttpUrlItem(requestInfo.getPsiElement(), requestInfo.getMethod(), requestInfo.getPath());
            names.add(httpUrlItem.getName());
            itemList.add(httpUrlItem);
        }
        return names.toArray(new String[0]);
    }

    /**
     * 根据提供的名称返回实际的 可导航元素。
     * 这些元素最终会在查找结果列表中展示，并支持用户导航。
     */
    @Override
    public NavigationItem @NotNull [] getItemsByName(String name, String pattern, Project project, boolean includeNonProjectItems) {
        return itemList.stream()
                .filter(item -> item.getName() != null && item.getName().equals(name))
                .toList()
                .toArray(new NavigationItem[0]);
    }
}