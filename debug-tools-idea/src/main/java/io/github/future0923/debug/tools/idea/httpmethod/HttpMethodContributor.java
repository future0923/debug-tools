package io.github.future0923.debug.tools.idea.httpmethod;

import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import io.github.future0923.debug.tools.idea.httpmethod.beans.HttpMethodInfo;
import io.github.future0923.debug.tools.idea.httpmethod.beans.HttpMethodItem;
import io.github.future0923.debug.tools.idea.httpmethod.utils.HttpMethodUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 提供HttpMethod搜索的数据
 *
 * @author future0923
 */
public class HttpMethodContributor implements ChooseByNameContributor {

    private final Module module;

    private List<HttpMethodItem> itemList;

    public HttpMethodContributor(Module module) {
        this.module = module;
    }

    /**
     * 返回可供搜索的 元素名称列表
     */
    @Override
    public String @NotNull [] getNames(Project project, boolean includeNonProjectItems) {
        List<HttpMethodInfo> requestInfos;
        if (includeNonProjectItems && module != null) {
            requestInfos = HttpMethodUtils.getModuleAllRequests(project, module);
        } else {
            Map<String, List<HttpMethodInfo>> allRequest = HttpMethodUtils.getAllRequest(project);
            requestInfos = allRequest.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
        }
        List<String> names = new ArrayList<>(requestInfos.size());
        itemList = new ArrayList<>(requestInfos.size());
        for (HttpMethodInfo requestInfo : requestInfos) {
            HttpMethodItem httpMethodItem = new HttpMethodItem(requestInfo.getPsiElement(), requestInfo.getMethod(), requestInfo.getPath());
            names.add(httpMethodItem.getName());
            itemList.add(httpMethodItem);
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