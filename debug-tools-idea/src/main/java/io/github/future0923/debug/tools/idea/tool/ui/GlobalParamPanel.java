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
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import io.github.future0923.debug.tools.base.hutool.core.util.ClassUtil;
import io.github.future0923.debug.tools.base.hutool.core.util.StrUtil;
import io.github.future0923.debug.tools.client.holder.ClientSocketHolder;
import io.github.future0923.debug.tools.common.protocal.packet.request.ServerCloseRequestPacket;
import io.github.future0923.debug.tools.idea.bundle.DebugToolsBundle;
import io.github.future0923.debug.tools.idea.client.ApplicationProjectHolder;
import io.github.future0923.debug.tools.idea.setting.DebugToolsSettingState;
import io.github.future0923.debug.tools.idea.ui.combobox.ClassLoaderComboBox;
import io.github.future0923.debug.tools.idea.ui.combobox.MethodAroundComboBox;
import io.github.future0923.debug.tools.idea.utils.DebugToolsNotifierUtil;
import io.github.future0923.debug.tools.idea.utils.DebugToolsUIHelper;
import io.github.future0923.debug.tools.idea.utils.StateUtils;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author future0923
 */
public class GlobalParamPanel extends JBPanel<GlobalParamPanel> {

    private final Project project;

    private final DebugToolsSettingState settingState;

    @Getter
    private final JTextPane textField = new JTextPane();

    private final JTextPane local = new JTextPane();

    private final JPanel attachButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));

    private final JPanel classLoaderPanel = new JPanel(new BorderLayout(5, 5));
    @Getter
    private final JTextPane attached = new JTextPane();

    private final Map<JBTextField, JBTextField> headerItemMap = new HashMap<>();

    private final List<JPanel> headerPanelList = new ArrayList<>();

    private JPanel jPanel;

    private final PrintSqlPanel printSqlPanel;

    private final MethodAroundComboBox methodAroundComboBox;
    
    // Keep references to components that need to be refreshed
    private JBLabel attachStatusLabel;
    private JBLabel defaultClassLoaderLabel;
    private JButton closeButton;
    private JButton stopButton;
    private JBLabel globalHeaderLabel;
    private JButton addHeaderButton;
    private JButton addAuthHeaderButton;
    private JButton removeAllHeaderButton;
    private JButton saveHeaderButton;
    private JBLabel methodAroundLabel;

    public GlobalParamPanel(Project project) {
        super(new GridBagLayout());
        this.project = project;
        this.settingState = DebugToolsSettingState.getInstance(project);
        this.methodAroundComboBox = new MethodAroundComboBox(project, 300);
        this.printSqlPanel = StateUtils.getPrintSqlPanel(project);
        this.printSqlPanel.setVisible(false);
        initLayout();
    }

    private void initLayout() {
        JPanel attachStatusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        attached.setEditable(false);
        attached.setForeground(JBColor.WHITE);
        attached.setOpaque(true);
        attachStatusLabel = new JBLabel(DebugToolsBundle.message("global.param.panel.attach.status"));
        attachStatusPanel.add(attachStatusLabel);
        attachStatusPanel.add(local);
        attachStatusPanel.add(attached);
        attachStatusPanel.add(textField);

        ClassLoaderComboBox classLoaderComboBox = StateUtils.getClassLoaderComboBox(project);
        defaultClassLoaderLabel = new JBLabel(DebugToolsBundle.message("global.param.panel.default.classloader"));
        classLoaderPanel.add(defaultClassLoaderLabel, BorderLayout.WEST);
        classLoaderPanel.add(classLoaderComboBox);
        closeButton = new JButton(DebugToolsBundle.message("global.param.panel.close"));
        attachButtonPanel.add(closeButton);
        closeButton.addActionListener(e -> ApplicationProjectHolder.close(project));
        stopButton = new JButton(DebugToolsBundle.message("global.param.panel.stop"));
        attachButtonPanel.add(stopButton);
        stopButton.addActionListener(e -> {
            try {
                ApplicationProjectHolder.send(project, new ServerCloseRequestPacket());
            } catch (Exception ex) {
                Messages.showErrorDialog(project, ex.getMessage(), DebugToolsBundle.message("global.param.panel.stop"));
            }
            ApplicationProjectHolder.close(project);
            unAttached();
        });
        FormBuilder formBuilder = FormBuilder.createFormBuilder();
        jPanel = formBuilder.addComponentFillVertically(new JPanel(), 0).getPanel();

        JPanel methodAroundPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        methodAroundLabel = new JBLabel(DebugToolsBundle.message("method.around"));
        methodAroundPanel.add(methodAroundLabel);
        methodAroundComboBox.addActionListener(e -> settingState.setDefaultMethodAroundName((String) methodAroundComboBox.getSelectedItem()));
        if (StrUtil.isNotBlank(settingState.getDefaultMethodAroundName())) {
            methodAroundComboBox.setSelected(settingState.getDefaultMethodAroundName());
        }
        methodAroundPanel.add(methodAroundComboBox);

        JPanel globalHeaderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        globalHeaderLabel = new JBLabel(DebugToolsBundle.message("global.param.panel.global.header"));
        globalHeaderPanel.add(globalHeaderLabel);
        addHeaderButton = new JButton(DebugToolsBundle.message("action.add"));
        addHeaderButton.setToolTipText(DebugToolsBundle.message("global.param.panel.add.header.tooltip"));
        addHeaderButton.addActionListener(e -> {
            headerPanelList.add(DebugToolsUIHelper.addHeaderComponentItem(jPanel, formBuilder, 100, 230, null, null, headerItemMap));
            DebugToolsUIHelper.refreshUI(formBuilder);
        });
        globalHeaderPanel.add(addHeaderButton);
        addAuthHeaderButton = new JButton(DebugToolsBundle.message("global.param.panel.add.auth"));
        addAuthHeaderButton.setToolTipText(DebugToolsBundle.message("global.param.panel.add.auth.tooltip"));
        addAuthHeaderButton.addActionListener(e -> {
            headerPanelList.add(DebugToolsUIHelper.addHeaderComponentItem(jPanel, formBuilder, 100, 230, "Authorization", null, headerItemMap));
            DebugToolsUIHelper.refreshUI(formBuilder);
        });
        globalHeaderPanel.add(addAuthHeaderButton);
        removeAllHeaderButton = new JButton(DebugToolsBundle.message("global.param.panel.remove.all"));
        removeAllHeaderButton.setToolTipText(DebugToolsBundle.message("global.param.panel.remove.all.tooltip"));
        removeAllHeaderButton.addActionListener(e -> {
            clearHeader();
            DebugToolsNotifierUtil.notifyInfo(project, DebugToolsBundle.message("global.param.panel.notification.header.remove.all"));
        });
        globalHeaderPanel.add(removeAllHeaderButton);
        saveHeaderButton = new JButton(DebugToolsBundle.message("action.save"));
        saveHeaderButton.setToolTipText(DebugToolsBundle.message("global.param.panel.save.tooltip"));
        saveHeaderButton.addActionListener(e -> {
            settingState.clearGlobalHeaderCache();
            headerItemMap.forEach((k, v) -> {
                String key = k.getText();
                if (StringUtils.isNotBlank(key)) {
                    settingState.putGlobalHeader(key, v.getText());
                }
            });
            DebugToolsNotifierUtil.notifyInfo(project, DebugToolsBundle.message("global.param.panel.notification.header.save"));
        });
        globalHeaderPanel.add(saveHeaderButton);
        formBuilder.addComponent(attachStatusPanel);
        formBuilder.addComponent(classLoaderPanel);
        formBuilder.addComponent(attachButtonPanel);
        formBuilder.addComponent(printSqlPanel);
        formBuilder.addComponent(methodAroundPanel);
        formBuilder.addComponent(methodAroundComboBox.getMethodAroundPanel());
        formBuilder.addComponent(globalHeaderPanel);
        settingState.getGlobalHeader().forEach((k, v) -> headerPanelList.add(DebugToolsUIHelper.addHeaderComponentItem(jPanel, formBuilder, 100, 230, k, v, headerItemMap)));
        DebugToolsUIHelper.refreshUI(formBuilder);
        add(jPanel, DebugToolsUIHelper.northGridBagConstraints());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 0;
        gbc.gridheight = -1;
        this.add(new JPanel(), gbc);

        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleWithFixedDelay(
                () -> {
                    try {
                        ApplicationProjectHolder.Info info = ApplicationProjectHolder.getInfo(project);
                        if (info == null || info.getClient() == null) {
                            unAttached();
                        } else {
                            local.setText(settingState.isLocal() ? "L" : "R");
                            local.setVisible(true);
                            if (!info.getClient().isClosed()) {
                                textField.setText(ClassUtil.getShortClassName(info.getApplicationName()));
                                textField.setVisible(true);
                                attached.setText(DebugToolsBundle.message("global.param.panel.status.connected"));
                                attached.setBackground(JBColor.GREEN);
                                attachButtonPanel.setVisible(true);
                                classLoaderPanel.setVisible(true);
                            } else {
                                if (info.getClient().getHolder().getRetry() == ClientSocketHolder.FAIL) {
                                    attached.setText(DebugToolsBundle.message("global.param.panel.status.fail"));
                                } else if (info.getClient().getHolder().getRetry() == ClientSocketHolder.RETRYING) {
                                    attached.setText(DebugToolsBundle.message("global.param.panel.status.reconnect"));
                                } else if (info.getClient().getHolder().getRetry() == ClientSocketHolder.INIT) {
                                    attached.setText(DebugToolsBundle.message("global.param.panel.status.connecting"));
                                }
                                attached.setBackground(JBColor.RED);
                                textField.setVisible(true);
                                attachButtonPanel.setVisible(true);
                            }
                        }
                    } catch (Exception ignored) {}
                },
                0,
                2,
                TimeUnit.SECONDS
        );
    }

    private void unAttached() {
        local.setVisible(false);
        attached.setText(DebugToolsBundle.message("global.param.panel.status.unattached"));
        attached.setBackground(JBColor.RED);
        textField.setVisible(false);
        attachButtonPanel.setVisible(false);
        classLoaderPanel.setVisible(false);
        printSqlPanel.setVisible(false);
    }

    public void clearHeader() {
        for (JPanel panel : headerPanelList) {
            jPanel.remove(panel);
        }
        jPanel.revalidate();
        jPanel.repaint();
        headerItemMap.clear();
    }
    
    /**
     * Refresh the UI components with new language settings
     */
    public void refresh() {
        // Update all components that use DebugToolsBundle messages
        if (attachStatusLabel != null) {
            attachStatusLabel.setText(DebugToolsBundle.message("global.param.panel.attach.status"));
        }
        
        if (defaultClassLoaderLabel != null) {
            defaultClassLoaderLabel.setText(DebugToolsBundle.message("global.param.panel.default.classloader"));
        }
        
        if (closeButton != null) {
            closeButton.setText(DebugToolsBundle.message("global.param.panel.close"));
        }
        
        if (stopButton != null) {
            stopButton.setText(DebugToolsBundle.message("global.param.panel.stop"));
            stopButton.setToolTipText(DebugToolsBundle.message("global.param.panel.stop"));
        }
        
        if (globalHeaderLabel != null) {
            globalHeaderLabel.setText(DebugToolsBundle.message("global.param.panel.global.header"));
        }
        
        if (addHeaderButton != null) {
            addHeaderButton.setText(DebugToolsBundle.message("action.add"));
            addHeaderButton.setToolTipText(DebugToolsBundle.message("global.param.panel.add.header.tooltip"));
        }
        
        if (addAuthHeaderButton != null) {
            addAuthHeaderButton.setText(DebugToolsBundle.message("global.param.panel.add.auth"));
            addAuthHeaderButton.setToolTipText(DebugToolsBundle.message("global.param.panel.add.auth.tooltip"));
        }
        
        if (removeAllHeaderButton != null) {
            removeAllHeaderButton.setText(DebugToolsBundle.message("global.param.panel.remove.all"));
            removeAllHeaderButton.setToolTipText(DebugToolsBundle.message("global.param.panel.remove.all.tooltip"));
        }
        
        if (saveHeaderButton != null) {
            saveHeaderButton.setText(DebugToolsBundle.message("action.save"));
            saveHeaderButton.setToolTipText(DebugToolsBundle.message("global.param.panel.save.tooltip"));
        }

        if (methodAroundLabel != null) {
            methodAroundLabel.setText(DebugToolsBundle.message("method.around"));
        }

        methodAroundComboBox.refreshBundle();

        printSqlPanel.refreshBundle();
        
        // Update status messages
        ApplicationProjectHolder.Info info = ApplicationProjectHolder.getInfo(project);
        if (info == null || info.getClient() == null || info.getClient().isClosed()) {
            attached.setText(DebugToolsBundle.message("global.param.panel.status.unattached"));
        } else {
            attached.setText(DebugToolsBundle.message("global.param.panel.status.connected"));
        }
        
        // Revalidate and repaint the panel to ensure UI updates
        this.revalidate();
        this.repaint();
    }
}
