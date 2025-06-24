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
package io.github.future0923.debug.tools.idea.tool;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import io.github.future0923.debug.tools.idea.constant.IdeaPluginProjectConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * @author future0923
 */
public class DebugToolsToolWindowFactory implements ToolWindowFactory {

    public static DebugToolsToolWindow getToolWindow(@Nullable Project project) {
        return getToolWindow(project, false);
    }

    public static DebugToolsToolWindow getToolWindow(@Nullable Project project, boolean show) {
        if (project == null) {
            return null;
        }
        ToolWindow toolWindow = getWindow(project);
        if (show) {
            showWindow(project, null);
        }
        if (toolWindow != null) {
            for (Component component : toolWindow.getComponent().getComponents()) {
                if (component instanceof DebugToolsToolWindow) {
                    return ((DebugToolsToolWindow) component);
                }
            }
        }
        return null;
    }

    @Nullable
    public static ToolWindow getWindow(@NotNull Project project) {
        return ToolWindowManager.getInstance(project).getToolWindow(IdeaPluginProjectConstants.TOOL_WINDOW_ID);
    }

    public static void showWindow(@NotNull Project project, @Nullable Runnable onShow) {
        ToolWindow window = getWindow(project);
        if (window == null) {
            return;
        }
        window.show(onShow);
    }

    public static void hideWindow(@NotNull Project project, @Nullable Runnable onShow) {
        ToolWindow window = getWindow(project);
        if (window == null) {
            return;
        }
        window.hide(onShow);
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ContentFactory contentFactory = ApplicationManager.getApplication().getService(ContentFactory.class);
        Content content = contentFactory.createContent(new DebugToolsToolWindow(project), null, false);
        toolWindow.getContentManager().addContent(content);
    }

    // 加 @Override 注解在2023.3及以下编译会报错
    public boolean isDumbAware() {
        // 不需要等待索引完成
        return true;
    }
}
