package io.github.future0923.debug.tools.idea.tool.action;

import com.intellij.openapi.project.Project;
import io.github.future0923.debug.tools.idea.tool.DebugToolsToolWindow;
import io.github.future0923.debug.tools.idea.tool.ui.AttachServerMenu;
import io.github.future0923.debug.tools.idea.utils.DebugToolsIcons;

/**
 * @author future0923
 */
public class AttachAction extends BaseToolAction {

    public AttachAction() {
        getTemplatePresentation().setText("Attach");
        getTemplatePresentation().setIcon(DebugToolsIcons.add_icon);
    }

    @Override
    protected void doActionPerformed(Project project, DebugToolsToolWindow toolWindow) {
        AttachServerMenu attachServerMenu = new AttachServerMenu(project);
        attachServerMenu.show(toolWindow, 0, attachServerMenu.getY());
    }
}
