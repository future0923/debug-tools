package io.github.future0923.debug.power.idea.tool.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.JBPopupMenu;
import com.intellij.ui.PortField;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBDimension;
import io.github.future0923.debug.power.idea.setting.DebugPowerSettingState;
import io.github.future0923.debug.power.idea.utils.DebugPowerAttachUtils;

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
        DebugPowerSettingState settingState = DebugPowerSettingState.getInstance(project);
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
            settingState.setRemoteHost(hostField.getText());
            settingState.setRemoteTcpPort(tcpPortField.getNumber());
            settingState.setRemoteHttpPort(httpPortField.getNumber());
            settingState.setLocal(false);
            DebugPowerAttachUtils.attachRemote(
                    project,
                    settingState.getRemoteHost(),
                    settingState.getRemoteTcpPort()
            );
            this.setVisible(false);
        });
        this.add(button, BorderLayout.SOUTH);
    }


}
