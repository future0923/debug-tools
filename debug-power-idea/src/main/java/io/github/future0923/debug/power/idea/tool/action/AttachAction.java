package io.github.future0923.debug.power.idea.tool.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import io.github.future0923.debug.power.idea.tool.ui.AttachServerMenu;
import io.github.future0923.debug.power.idea.tool.DebugPowerToolWindowFactory;
import io.github.future0923.debug.power.idea.tool.DebugPowerToolWindow;
import io.github.future0923.debug.power.idea.utils.DebugPowerIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author future0923
 */
public class AttachAction extends DumbAwareAction {

    private DebugPowerToolWindow toolWindow;

    public AttachAction() {
        getTemplatePresentation().setText("Attach");
        getTemplatePresentation().setIcon(DebugPowerIcons.add_icon);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }
        if (getToolWindow(project) == null) {
            return;
        }
        AttachServerMenu attachServerMenu = new AttachServerMenu(project);
        attachServerMenu.show(toolWindow, 0, attachServerMenu.getY());
    }

    private DebugPowerToolWindow getToolWindow(@Nullable Project project) {
        if (toolWindow != null) {
            return toolWindow;
        }
        return (toolWindow = DebugPowerToolWindowFactory.getToolWindow(project));
    }
}
