package io.github.future0923.debug.tools.idea.utils;

import com.intellij.openapi.project.Project;

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

    public static void setProjectOpenTime(Project project) {
        PROJECT_OPEN_TIME_MAP.put(project, System.currentTimeMillis());
    }

    public static Long getProjectOpenTime(Project project) {
        return PROJECT_OPEN_TIME_MAP.getOrDefault(project, System.currentTimeMillis());
    }
}
