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
package io.github.future0923.debug.tools.idea.tool.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.JBPopupMenu;
import com.intellij.ui.components.JBCheckBox;
import io.github.future0923.debug.tools.idea.setting.DebugToolsSettingState;
import io.github.future0923.debug.tools.idea.tool.DebugToolsToolWindow;
import io.github.future0923.debug.tools.idea.tool.action.ClearCacheType;
import io.github.future0923.debug.tools.idea.utils.DebugToolsNotifierUtil;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author future0923
 */
public class ClearCacheMenu extends JBPopupMenu {

    private final Project project;

    private final List<JBCheckBox> checkBoxList = new ArrayList<>();

    public ClearCacheMenu(Project project, DebugToolsToolWindow toolWindow) {
        super();
        this.project = project;
        this.setLayout(new BorderLayout());
        DebugToolsSettingState settingState = DebugToolsSettingState.getInstance(project);
        initItem();
        initButton(settingState, toolWindow);
    }

    private void initButton(DebugToolsSettingState settingState, DebugToolsToolWindow toolWindow) {
        JPanel buttonPane = new JPanel();
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(e -> this.setVisible(false));
        buttonPane.add(cancel);
        JButton clear = new JButton("Clear");
        clear.addActionListener(e -> {
            checkBoxList.stream().filter(AbstractButton::isSelected).forEach(box -> {
                if (ClearCacheType.CORE_JAR.getType().equals(box.getText())) {
                    settingState.clearAgentCache();
                }
                if (ClearCacheType.METHOD_PARAM.getType().equals(box.getText())) {
                    settingState.clearMethodParamCache();
                }
                if (ClearCacheType.GLOBAL_HEADER.getType().equals(box.getText())) {
                    settingState.clearGlobalHeaderCache();
                    toolWindow.clearHeader();
                }
            });
            this.setVisible(false);
            DebugToolsNotifierUtil.notifyInfo(project, "Clear cache successful");
        });
        buttonPane.add(clear);
        JButton clearAll = new JButton("Clear all");
        clearAll.addActionListener(e -> {
            settingState.clearAllCache();
            toolWindow.clearHeader();
            this.setVisible(false);
            DebugToolsNotifierUtil.notifyInfo(project, "Cache all successful");
        });
        buttonPane.add(clearAll);
        this.add(buttonPane, BorderLayout.SOUTH);
    }

    private void initItem() {
        checkBoxList.add(new JBCheckBox(ClearCacheType.CORE_JAR.getType()));
        checkBoxList.add(new JBCheckBox(ClearCacheType.METHOD_PARAM.getType()));
        checkBoxList.add(new JBCheckBox(ClearCacheType.GLOBAL_HEADER.getType()));
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1, 3, 3));
        for (JBCheckBox box : checkBoxList) {
            panel.add(box);
        }
        this.add(panel, BorderLayout.CENTER);
    }
}
