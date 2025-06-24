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
