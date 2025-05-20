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
import com.intellij.ui.PortField;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBDimension;
import io.github.future0923.debug.tools.idea.setting.DebugToolsSettingState;
import io.github.future0923.debug.tools.idea.utils.DebugToolsAttachUtils;

import javax.swing.*;
import java.awt.*;

/**
 * @author future0923
 */
public class ConnectServerMenu extends JBPopupMenu {

    private final Project project;

    private final JBTextField hostField = new JBTextField();

    private final PortField tcpPortField = new PortField(12345);

    private final PortField httpPortField = new PortField(22222);

    public ConnectServerMenu(Project project) {
        super();
        setPreferredSize(new JBDimension(300, 200));
        this.project = project;
        init();
    }

    private void init() {
        DebugToolsSettingState settingState = DebugToolsSettingState.getInstance(project);
        hostField.setText(settingState.getRemoteHost());
        if (settingState.getRemoteTcpPort() != null) {
            tcpPortField.setNumber(settingState.getRemoteTcpPort());
        }
        if (settingState.getRemoteHttpPort() != null) {
            httpPortField.setNumber(settingState.getRemoteHttpPort());
        }
        FormBuilder formBuilder = FormBuilder.createFormBuilder();
        JPanel jPanel = formBuilder
                .addLabeledComponent(
                        new JBLabel("Host:"),
                        hostField
                )
                .addLabeledComponent(
                        new JBLabel("Tcp port:"),
                        tcpPortField
                )
                .addLabeledComponent(
                        new JBLabel("Http port:"),
                        httpPortField
                )
                .addComponentFillVertically(new JPanel(), 5)
                .getPanel();
        this.add(jPanel, BorderLayout.CENTER);
        JPanel button = new JPanel();
        JButton cancel = new JButton("Cancel");
        button.add(cancel);
        cancel.addActionListener(e -> this.setVisible(false));
        JButton saveConnect = new JButton("Save & Connect");
        button.add(saveConnect);
        saveConnect.addActionListener(e -> {
            this.setVisible(false);
            settingState.setRemoteHost(hostField.getText());
            settingState.setRemoteTcpPort(tcpPortField.getNumber());
            settingState.setRemoteHttpPort(httpPortField.getNumber());
            DebugToolsAttachUtils.attachRemote(
                    project,
                    settingState.getRemoteHost(),
                    settingState.getRemoteTcpPort()
            );
            settingState.setLocal(false);
        });
        this.add(button, BorderLayout.SOUTH);
    }


}
