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
