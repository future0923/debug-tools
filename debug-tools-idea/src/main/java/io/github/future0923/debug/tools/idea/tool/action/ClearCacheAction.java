package io.github.future0923.debug.tools.idea.tool.action;

import com.intellij.openapi.project.Project;
import io.github.future0923.debug.tools.idea.tool.DebugToolsToolWindow;
import io.github.future0923.debug.tools.idea.tool.ui.ClearCacheMenu;
import io.github.future0923.debug.tools.idea.utils.DebugToolsIcons;

/**
 * @author future0923
 */
public class ClearCacheAction extends BaseToolAction {

    public ClearCacheAction() {
        getTemplatePresentation().setText("Clear Cache");
        getTemplatePresentation().setIcon(DebugToolsIcons.clear_icon);
    }

    @Override
    protected void doActionPerformed(Project project, DebugToolsToolWindow toolWindow) {
        ClearCacheMenu clearCacheMenu = new ClearCacheMenu(project, toolWindow);
        clearCacheMenu.show(toolWindow, 0, clearCacheMenu.getY());
    }

}
