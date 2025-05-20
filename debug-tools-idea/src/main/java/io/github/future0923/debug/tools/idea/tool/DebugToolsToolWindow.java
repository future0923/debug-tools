/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
