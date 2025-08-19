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

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.project.Project;
import io.github.future0923.debug.tools.idea.action.TraceMethodGroup;
import io.github.future0923.debug.tools.idea.bundle.DebugToolsBundle;
import io.github.future0923.debug.tools.idea.setting.DebugToolsSettingState;
import io.github.future0923.debug.tools.idea.setting.LanguageSetting;
import io.github.future0923.debug.tools.idea.tool.DebugToolsToolWindow;
import io.github.future0923.debug.tools.idea.tool.DebugToolsToolWindowFactory;

/**
 * Utility class for language-related operations.
 *
 * @author future0923
 */
public class LanguageUtils {
    
    /**
     * Refresh UI components after language change
     *
     * @param project the project
     */
    public static void refreshUI(Project project) {
        // Refresh tool window if it exists
        DebugToolsToolWindow toolWindow = DebugToolsToolWindowFactory.getToolWindow(project);
        if (toolWindow != null) {
            toolWindow.refresh();
            // Refresh GlobalParamPanel
            toolWindow.getGlobalParamPanel().refresh();
        }
        
        // Refresh action groups
        refreshActionGroups();
    }
    
    /**
     * Refresh action groups after language change
     */
    private static void refreshActionGroups() {
        ActionManager actionManager = ActionManager.getInstance();
        
        // Refresh TraceMethodGroup
        AnAction traceMethodAction = actionManager.getAction("DebugToolsTool.TraceMethod");
        if (traceMethodAction instanceof TraceMethodGroup traceMethodGroup) {
            // Force refresh the presentation text
            traceMethodGroup.getTemplatePresentation().setText(
                DebugToolsBundle.message("action.trace.method.group")
            );
        }
        
        // Refresh QuickDebug action
        AnAction quickDebugAction = actionManager.getAction("DebugToolsTool.QuickDebug");
        if (quickDebugAction != null) {
            quickDebugAction.getTemplatePresentation().setText(
                DebugToolsBundle.message("action.quick.debug.text")
            );
        }
        
        // Refresh ExecuteLast action
        AnAction executeLastAction = actionManager.getAction("DebugToolsTool.ExecuteLast");
        if (executeLastAction != null) {
            executeLastAction.getTemplatePresentation().setText(
                DebugToolsBundle.message("action.execute.last.text")
            );
        }
        
        // Refresh ExecuteLastWithDefaultClassLoader action
        AnAction executeLastWithDefaultClassLoaderAction = actionManager.getAction("DebugToolsTool.ExecuteLastWithDefaultClassLoader");
        if (executeLastWithDefaultClassLoaderAction != null) {
            executeLastWithDefaultClassLoaderAction.getTemplatePresentation().setText(
                DebugToolsBundle.message("action.execute.last.with.default.classloader.text")
            );
        }
        
        // Refresh HttpUrlSearch action
        AnAction httpUrlSearchAction = actionManager.getAction("DebugTools.HttpUrl");
        if (httpUrlSearchAction != null) {
            httpUrlSearchAction.getTemplatePresentation().setText(
                DebugToolsBundle.message("action.search.http.url.text")
            );
            httpUrlSearchAction.getTemplatePresentation().setDescription(
                DebugToolsBundle.message("action.search.http.url.description")
            );
        }
    }

}