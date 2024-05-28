package io.github.future0923.debug.power.idea.tool;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.JBSplitter;
import io.github.future0923.debug.power.idea.tool.action.AttachAction;
import io.github.future0923.debug.power.idea.tool.ui.GlobalParamPanel;

/**
 * @author future0923
 */
public class DebugPowerToolWindow extends SimpleToolWindowPanel {

    public DebugPowerToolWindow(Project project) {
        super(false, false);
        initToolBar();
        JBSplitter content = new JBSplitter(true, DebugPowerToolWindow.class.getName(), 0.5F);
        content.setFirstComponent(new GlobalParamPanel(project));
        content.setSecondComponent(null);
        this.setContent(content);
    }

    private void initToolBar() {
        DefaultActionGroup actionGroup = new DefaultActionGroup();
        actionGroup.add(new AttachAction());
        actionGroup.addSeparator();
        ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar(
                ActionPlaces.TOOLBAR,
                actionGroup,
                true
        );
        actionToolbar.setTargetComponent(this);
        setToolbar(actionToolbar.getComponent());
    }
}
