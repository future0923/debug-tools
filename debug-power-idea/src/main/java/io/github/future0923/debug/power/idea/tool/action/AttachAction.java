package io.github.future0923.debug.power.idea.tool.action;

import com.intellij.openapi.project.Project;
import io.github.future0923.debug.power.idea.tool.DebugPowerToolWindow;
import io.github.future0923.debug.power.idea.tool.ui.AttachServerMenu;
import io.github.future0923.debug.power.idea.utils.DebugPowerIcons;

/**
 * @author future0923
 */
public class AttachAction extends BaseToolAction {

    public AttachAction() {
        getTemplatePresentation().setText("Attach");
        getTemplatePresentation().setIcon(DebugPowerIcons.add_icon);
    }

    @Override
    protected void doActionPerformed(Project project, DebugPowerToolWindow toolWindow) {
        AttachServerMenu attachServerMenu = new AttachServerMenu(project);
        attachServerMenu.show(toolWindow, 0, attachServerMenu.getY());
    }
}
