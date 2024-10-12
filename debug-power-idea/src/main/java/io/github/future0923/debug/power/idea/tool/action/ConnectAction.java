package io.github.future0923.debug.power.idea.tool.action;

import com.intellij.openapi.project.Project;
import io.github.future0923.debug.power.idea.tool.DebugPowerToolWindow;
import io.github.future0923.debug.power.idea.tool.ui.ConnectServerMenu;
import io.github.future0923.debug.power.idea.utils.DebugPowerIcons;

/**
 * @author future0923
 */
public class ConnectAction extends BaseToolAction {

    public ConnectAction() {
        getTemplatePresentation().setText("Connect");
        getTemplatePresentation().setIcon(DebugPowerIcons.connect_icon);
    }

    @Override
    protected void doActionPerformed(Project project, DebugPowerToolWindow toolWindow) {
        ConnectServerMenu attachServerMenu = new ConnectServerMenu(project);
        attachServerMenu.show(toolWindow, 0, toolWindow.getY());
    }
}
