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
