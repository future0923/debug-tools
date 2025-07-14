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
import com.intellij.ui.components.JBRadioButton;
import com.intellij.ui.components.JBTextArea;
import io.github.future0923.debug.tools.base.utils.DebugToolsStringUtils;
import io.github.future0923.debug.tools.idea.model.ServerDisplayValue;
import io.github.future0923.debug.tools.idea.setting.DebugToolsSettingState;
import io.github.future0923.debug.tools.idea.utils.DebugToolsAttachUtils;
import io.github.future0923.debug.tools.idea.utils.StateUtils;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author future0923
 */
public class AttachServerMenu extends JBPopupMenu {

    private final List<JBRadioButton> radioButtonList = new ArrayList<>();

    private final JPanel radioPanel = new JPanel();

    public AttachServerMenu(Project project) {
        super();
        this.setLayout(new BorderLayout());
        initToolbar(project);
    }

    private void initToolbar(Project project) {
        radioPanel.setMinimumSize(new Dimension(500, 100));
        initVmServer();
        JPanel buttonPane = new JPanel();
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(e -> this.setVisible(false));
        buttonPane.add(cancel);
        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(e -> {
            radioPanel.removeAll();
            initVmServer();
        });
        buttonPane.add(refresh);
        JButton attach = new JButton("Attach");
        attach.addActionListener(e -> radioButtonList.stream().filter(AbstractButton::isSelected).findFirst().ifPresent(button -> {
            ServerDisplayValue serverDisplayValue = ServerDisplayValue.of(button.getText());
            if (serverDisplayValue != null) {
                DebugToolsSettingState settingState = DebugToolsSettingState.getInstance(project);
                String agentPath = settingState.loadAgentPath();
                if (DebugToolsStringUtils.isNotBlank(agentPath)) {
                    DebugToolsAttachUtils.attachLocal(project, serverDisplayValue.getKey(), serverDisplayValue.getValue(), agentPath);
                    StateUtils.getClassLoaderComboBox(project).refreshClassLoader(true);
                }
                settingState.setLocal(true);
            }
            this.setVisible(false);
        }));
        buttonPane.add(attach);
        this.add(radioPanel, BorderLayout.CENTER);
        this.add(buttonPane, BorderLayout.SOUTH);
    }

    private void initVmServer() {
        ButtonGroup radioGroup = new ButtonGroup();
        DebugToolsAttachUtils.vmConsumer(size -> {
                    if (size == 0) {
                        JBTextArea textArea = new JBTextArea("No server found");
                        textArea.setEnabled(false);
                        radioPanel.add(textArea);
                    } else {
                        radioPanel.setLayout(new GridLayout(size, 1, 3, 3));
                    }
                },
                descriptor -> {
                    JBRadioButton radioButton = new JBRadioButton(ServerDisplayValue.display(descriptor.id(), descriptor.displayName()));
                    radioPanel.add(radioButton);
                    radioGroup.add(radioButton);
                    radioButtonList.add(radioButton);
                });
    }
}
