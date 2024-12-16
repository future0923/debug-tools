package io.github.future0923.debug.tools.idea.api.utils;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.NavigatablePsiElement;
import io.github.future0923.debug.tools.idea.api.beans.ApiInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author future0923
 */
public class ApiUtils {

    public static final Map<NavigatablePsiElement, List<ApiInfo>> REQUEST_CACHE = new ConcurrentHashMap<>();

    /**
     * 获取项目中所有的请求信息
     *
     * @param project 项目
     * @return Map<模块名, 请求集合>
     */
    public static Map<String, List<ApiInfo>> getAllRequest(Project project) {
        REQUEST_CACHE.clear();
        Map<String, List<ApiInfo>> map = new HashMap<>();
        Module[] modules = ModuleManager.getInstance(project).getModules();
        for (Module module : modules) {
            List<ApiInfo> requestInfos = getModuleAllRequests(project, module);
            if (requestInfos.isEmpty()) {
                continue;
            }
            map.put(module.getName(), requestInfos);
            requestInfos.forEach(requestInfo -> {
                List<ApiInfo> requestInfoList;
                if (REQUEST_CACHE.containsKey(requestInfo.getPsiElement())) {
                    requestInfoList = REQUEST_CACHE.get(requestInfo.getPsiElement());
                } else {
                    requestInfoList = new ArrayList<>();
                    REQUEST_CACHE.put(requestInfo.getPsiElement(), requestInfoList);
                }
                requestInfoList.add(requestInfo);
            });
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
    public static List<ApiInfo> getModuleAllRequests(Project project, Module module) {
        List<ApiInfo> requestInfos = new ArrayList<>();
        List<ApiInfo> springRequestInfoByModule = SpringUtils.getSpringRequestByModule(project, module);
        if (!springRequestInfoByModule.isEmpty()) {
            requestInfos.addAll(springRequestInfoByModule);
        }
        return requestInfos;
    }
}
