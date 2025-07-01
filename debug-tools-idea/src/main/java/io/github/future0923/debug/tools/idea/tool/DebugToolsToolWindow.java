/*
 * Copyright (C) 2024-2025 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package io.github.future0923.debug.tools.idea.tool;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.JBSplitter;
import io.github.future0923.debug.tools.idea.tool.action.AttachAction;
import io.github.future0923.debug.tools.idea.tool.action.ClearCacheAction;
import io.github.future0923.debug.tools.idea.tool.action.ConnectAction;
import io.github.future0923.debug.tools.idea.tool.action.SettingAction;
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
        DefaultActionGroup defaultActionGroup = new DefaultActionGroup();
        defaultActionGroup.add(new AttachAction());
        defaultActionGroup.add(new ConnectAction());
        defaultActionGroup.add(new ClearCacheAction());
        defaultActionGroup.addSeparator();
        defaultActionGroup.add(new SettingAction());
        defaultActionGroup.addSeparator();
        defaultActionGroup.add(ActionManager.getInstance().getAction("DebugToolsTool.EvaluateGroovy"));
        defaultActionGroup.addSeparator();
        defaultActionGroup.add(ActionManager.getInstance().getAction("DebugTools.HttpUrl"));
        ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar(
                ActionPlaces.TOOLBAR,
                defaultActionGroup,
                true
        );
        actionToolbar.setTargetComponent(this);
        setToolbar(actionToolbar.getComponent());
    }

    public void clearHeader() {
        globalParamPanel.clearHeader();
    }
}
