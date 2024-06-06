package io.github.future0923.debug.power.idea.tool;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import io.github.future0923.debug.power.idea.constant.IdeaPluginProjectConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * @author future0923
 */
public class DebugPowerToolWindowFactory implements ToolWindowFactory {

    public static DebugPowerToolWindow getToolWindow(@Nullable Project project) {
        return getToolWindow(project, false);
    }

    public static DebugPowerToolWindow getToolWindow(@Nullable Project project, boolean show) {
        if (project == null) {
            return null;
        }
        ToolWindow toolWindow = getWindow(project);
        if (show) {
            showWindow(project, null);
        }
        if (toolWindow != null) {
            for (Component component : toolWindow.getComponent().getComponents()) {
                if (component instanceof DebugPowerToolWindow) {
                    return ((DebugPowerToolWindow) component);
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
        Content content = contentFactory.createContent(new DebugPowerToolWindow(project), null, false);
        toolWindow.getContentManager().addContent(content);
    }
}
