package io.github.future0923.debug.tools.idea.tool.action;

import com.intellij.openapi.project.Project;
import io.github.future0923.debug.tools.idea.tool.DebugToolsToolWindow;
import io.github.future0923.debug.tools.idea.tool.ui.ConnectServerMenu;
import io.github.future0923.debug.tools.idea.utils.DebugToolsIcons;

/**
 * @author future0923
 */
public class ConnectAction extends BaseToolAction {

    public ConnectAction() {
        getTemplatePresentation().setText("Connect");
        getTemplatePresentation().setIcon(DebugToolsIcons.connect_icon);
    }

    @Override
    protected void doActionPerformed(Project project, DebugToolsToolWindow toolWindow) {
        ConnectServerMenu attachServerMenu = new ConnectServerMenu(project);
        attachServerMenu.show(toolWindow, 0, toolWindow.getY());
    }
}
