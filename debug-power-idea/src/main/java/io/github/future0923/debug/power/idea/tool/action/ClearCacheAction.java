package io.github.future0923.debug.power.idea.tool.action;

import com.intellij.openapi.project.Project;
import io.github.future0923.debug.power.idea.tool.DebugPowerToolWindow;
import io.github.future0923.debug.power.idea.tool.ui.ClearCacheMenu;
import io.github.future0923.debug.power.idea.utils.DebugPowerIcons;

/**
 * @author future0923
 */
public class ClearCacheAction extends BaseToolAction {

    public ClearCacheAction() {
        getTemplatePresentation().setText("Clear Cache");
        getTemplatePresentation().setIcon(DebugPowerIcons.clear_icon);
    }

    @Override
    protected void doActionPerformed(Project project, DebugPowerToolWindow toolWindow) {
        ClearCacheMenu clearCacheMenu = new ClearCacheMenu(project, toolWindow);
        clearCacheMenu.show(toolWindow, 0, clearCacheMenu.getY());
    }

}
