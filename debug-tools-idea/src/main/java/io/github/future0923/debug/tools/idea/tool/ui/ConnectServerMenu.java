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
import com.intellij.openapi.util.text.HtmlChunk;
import com.intellij.ui.JBColor;
import com.intellij.ui.PortField;
import com.intellij.ui.components.JBBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBDimension;
import io.github.future0923.debug.tools.base.hutool.json.JSONObject;
import io.github.future0923.debug.tools.base.hutool.json.JSONUtil;
import io.github.future0923.debug.tools.idea.bundle.DebugToolsBundle;
import io.github.future0923.debug.tools.idea.setting.DebugToolsSettingState;
import io.github.future0923.debug.tools.idea.utils.DebugToolsAttachUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.Set;

/**
 * @author future0923
 */
public class ConnectServerMenu extends JBPopupMenu {

    private final Project project;

    private final JBTextField hostField = new JBTextField();

    private final JBTextField nameField = new JBTextField();

    private final PortField tcpPortField = new PortField(12345);

    private final PortField httpPortField = new PortField(22222);

    private final JBBox historyPanel = JBBox.createVerticalBox();

    public ConnectServerMenu(Project project) {
        super();
        setPreferredSize(new JBDimension(450, 400));
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
        JBColor jbColor = JBColor.namedColor("TextField.errorOutline", JBColor.RED);
        String redHex = String.format("#%02x%02x%02x", jbColor.getRed(), jbColor.getGreen(), jbColor.getBlue());
        FormBuilder formBuilder = FormBuilder.createFormBuilder();
        JPanel jPanel = formBuilder
                .addLabeledComponent(
                        new JBLabel(HtmlChunk.html()
                                .children(
                                        HtmlChunk.text("*").wrapWith("font").attr("color", redHex),
                                        HtmlChunk.text(DebugToolsBundle.message("connect.server.menu.host"))
                                ).toString()),
                        hostField
                )
                .addLabeledComponent(
                        new JBLabel(HtmlChunk.html()
                                .children(
                                        HtmlChunk.text("*").wrapWith("font").attr("color", redHex),
                                        HtmlChunk.text(DebugToolsBundle.message("connect.server.menu.tcp.port"))
                                ).toString()),
                        tcpPortField
                )
                .addLabeledComponent(
                        new JBLabel(HtmlChunk.html()
                                .children(
                                        HtmlChunk.text("*").wrapWith("font").attr("color", redHex),
                                        HtmlChunk.text(DebugToolsBundle.message("connect.server.menu.http.port"))
                                ).toString()),
                        httpPortField
                )
                .addLabeledComponent(
                        new JBLabel(DebugToolsBundle.message("connect.server.menu.name")),
                        nameField
                )
                .addComponentFillVertically(new JPanel(), 5)
                .getPanel();
        this.add(jPanel, BorderLayout.CENTER);

        JPanel button = new JPanel();
        JButton cancel = new JButton(DebugToolsBundle.message("connect.server.menu.cancel"));
        button.add(cancel);
        cancel.addActionListener(e -> this.setVisible(false));

        JButton connectBtn = new JButton(DebugToolsBundle.message("connect.server.menu.connect"));
        button.add(connectBtn);
        connectBtn.addActionListener(e -> {
            this.setVisible(false);
            settingState.setRemoteName(nameField.getText());
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

        JButton saveConnect = new JButton(DebugToolsBundle.message("connect.server.menu.save.and.connect"));
        button.add(saveConnect);
        saveConnect.addActionListener(e -> {
            this.setVisible(false);
            settingState.setRemoteName(nameField.getText());
            settingState.setRemoteHost(hostField.getText());
            settingState.setRemoteTcpPort(tcpPortField.getNumber());
            settingState.setRemoteHttpPort(httpPortField.getNumber());
            DebugToolsAttachUtils.attachRemote(
                    project,
                    settingState.getRemoteHost(),
                    settingState.getRemoteTcpPort()
            );
            settingState.setLocal(false);
            settingState.saveRemoteHost();
        });

        JButton delAllBtn = new JButton(DebugToolsBundle.message("connect.server.menu.delete.all"));
        button.add(delAllBtn);
        delAllBtn.addActionListener(e -> {
            settingState.delAllHost();
            historyPanel.removeAll();
            historyPanel.revalidate();
            historyPanel.repaint();
        });

        this.add(button, BorderLayout.SOUTH);

        this.showLastHosts2(settingState);
    }

    private void showLastHosts2(DebugToolsSettingState settingState) {
        historyPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        historyPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 1000));
        JScrollPane scrollPane = new JBScrollPane(historyPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        this.add(scrollPane, BorderLayout.SOUTH);

        Set<Map.Entry<String, String>> entries = settingState.getRemoteHosts().entrySet();
        for (Map.Entry<String, String> entry : entries) {
            JPanel itemPanel = createListItemPanel(entry, historyPanel);
            historyPanel.add(itemPanel);
            historyPanel.add(JBBox.createVerticalStrut(5)); // 添加间距
        }
    }
    private JPanel createListItemPanel(Map.Entry<String, String> entry, Container parent) {
        DebugToolsSettingState settingState = DebugToolsSettingState.getInstance(project);
        JSONObject hostInfo = JSONUtil.parseObj(entry.getValue());

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new JBColor(new Color(195, 197, 208), new Color(76, 79, 85)),1,true),
                BorderFactory.createEmptyBorder(3, 3, 3, 3)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                settingState.setRemoteName(entry.getValue());
                settingState.setRemoteHost(hostInfo.getStr("host"));
                settingState.setRemoteTcpPort(hostInfo.getInt("tcpPort"));
                settingState.setRemoteHttpPort(hostInfo.getInt("httpPort"));
                hostField.setText(settingState.getRemoteHost());
                nameField.setText(entry.getKey());
                if (settingState.getRemoteTcpPort() != null) {
                    tcpPortField.setNumber(settingState.getRemoteTcpPort());
                }
                if (settingState.getRemoteHttpPort() != null) {
                    httpPortField.setNumber(settingState.getRemoteHttpPort());
                }
            }
        });

        JLabel label = new JLabel(entry.getKey());
        label.setToolTipText(hostInfo.getStr("host") + "@" + hostInfo.getInt("tcpPort") + "@" + hostInfo.getInt("httpPort"));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 1, 1));

        JButton editButton = new JButton(DebugToolsBundle.message("connect.server.menu.connect.button"));
        editButton.addActionListener(e -> {
            settingState.setRemoteName(entry.getValue());
            settingState.setRemoteHost(hostInfo.getStr("host"));
            settingState.setRemoteTcpPort(hostInfo.getInt("tcpPort"));
            settingState.setRemoteHttpPort(hostInfo.getInt("httpPort"));
            DebugToolsAttachUtils.attachRemote(
                    project,
                    settingState.getRemoteHost(),
                    settingState.getRemoteTcpPort()
            );
            settingState.setLocal(false);
            this.setVisible(false);
        });

        JButton deleteButton = new JButton(DebugToolsBundle.message("connect.server.menu.remove.button"));
        deleteButton.addActionListener(e -> {
            settingState.getRemoteHosts().remove(entry.getKey());
            parent.remove(panel);
            parent.revalidate();
            parent.repaint();
        });

        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        panel.add(label, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.EAST);

        return panel;
    }
}

