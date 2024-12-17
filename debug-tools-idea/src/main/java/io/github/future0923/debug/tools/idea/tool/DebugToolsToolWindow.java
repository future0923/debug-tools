package io.github.future0923.debug.tools.idea.tool;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.JBSplitter;
import io.github.future0923.debug.tools.idea.tool.ui.GlobalParamPanel;

/**
 * @author future0923
 */
public class DebugToolsToolWindow extends SimpleToolWindowPanel {

    private final GlobalParamPanel globalParamPanel;

    public DebugToolsToolWindow(Project project) {
        super(false, false);
        initToolBar();
        JBSplitter content = new JBSplitter(true, DebugToolsToolWindow.class.getName(), 0.5F);
        globalParamPanel = new GlobalParamPanel(project);
        content.setFirstComponent(globalParamPanel);
        content.setSecondComponent(null);
        this.setContent(content);
    }

    private void initToolBar() {
        AnAction action = ActionManager.getInstance().getAction("DebugTools.Toolbar");
        ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar(
                ActionPlaces.TOOLBAR,
                action instanceof ActionGroup ? (ActionGroup) action : new DefaultActionGroup(),
                true
        );
        actionToolbar.setTargetComponent(this);
        setToolbar(actionToolbar.getComponent());
    }

    public void clearHeader() {
        globalParamPanel.clearHeader();
    }
}
