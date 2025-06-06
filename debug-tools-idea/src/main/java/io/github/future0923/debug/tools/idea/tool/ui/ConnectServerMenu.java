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
import com.intellij.ui.JBColor;
import com.intellij.ui.PortField;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBDimension;
import io.github.future0923.debug.tools.base.hutool.core.util.StrUtil;
import io.github.future0923.debug.tools.base.hutool.json.JSONObject;
import io.github.future0923.debug.tools.base.hutool.json.JSONUtil;
import io.github.future0923.debug.tools.idea.setting.DebugToolsSettingState;
import io.github.future0923.debug.tools.idea.utils.DebugToolsAttachUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

/**
 * @author future0923
 */
public class ConnectServerMenu extends JBPopupMenu {

    private final Project project;

    private final JBTextField hostField = new JBTextField();

    private final JBTextField nameField = new JBTextField();

    private final PortField tcpPortField = new PortField(12345);

    private final PortField httpPortField = new PortField(22222);

    JPanel historyJPanel = new JPanel(new GridBagLayout()) {
        @Override
        public Dimension getPreferredSize() {
            // 根据内容动态计算大小
            int width = 0;
            int height = 0;
            for (Component comp : getComponents()) {
                Rectangle bounds = comp.getBounds();
                width = Math.max(width, bounds.x + bounds.width);
                height = Math.max(height, bounds.y + bounds.height);
            }
            // 添加一些边距
            return new Dimension(width + 20, height + 100);
        }
    };

    JPanel itemPanel = new JPanel(new BorderLayout());
    JPanel historyJPanel2 = new JPanel(new GridBagLayout());

    public ConnectServerMenu(Project project) {
        super();
        setPreferredSize(new JBDimension(450, 300));
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
                .addLabeledComponent(
                        new JBLabel("Name:"),
                        nameField
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
            settingState.saveHost();
        });

        JButton delAllBtn = new JButton("DelAll");
        button.add(delAllBtn);
        delAllBtn.addActionListener(e -> {
            settingState.delAllHost();
            historyJPanel.removeAll();
            historyJPanel.revalidate();
            historyJPanel.repaint();
        });

        this.add(button, BorderLayout.SOUTH);

        this.showLastHosts(settingState);
    }

    void showLastHosts(DebugToolsSettingState settingState){
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // 边距
        gbc.fill = GridBagConstraints.HORIZONTAL;
        int buttonsPerRow = 3, i = 0;

        historyJPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        Set<Map.Entry<String, String>> entries = settingState.getRemoteHosts().entrySet();
        for (Map.Entry<String, String> entry : entries) {

            i++;
            gbc.gridx = (i - 1) % buttonsPerRow;
            gbc.gridy = (i - 1) / buttonsPerRow;

            JSONObject hostInfo = JSONUtil.parseObj(entry.getValue());
            JButton historyButton = new JButton(entry.getKey());
            historyButton.addActionListener(e -> {
                this.setVisible(false);
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
            });
            historyJPanel.add(historyButton, gbc);
        }
        this.add(historyJPanel, BorderLayout.SOUTH);

        getThis();

        this.setPreferredSize(new JBDimension(420, 520));

        JButton historyButton = new JButton("Test");
        historyButton.addMouseListener(new MouseAdapter() {
            private Timer multiClickTimer;
            private int clickCount = 0;

            {
                multiClickTimer = new Timer(300, e -> {
                    // 定时器触发时根据点击次数处理
                    switch (clickCount) {
                        case 1:
                            System.out.println("单击");
                            break;
                        case 2:
                            System.out.println("双击");
                            break;
                        case 3:
                            System.out.println("三击");
                            break;
                    }
                    clickCount = 0;
                });
                multiClickTimer.setRepeats(false);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                clickCount++;
                if (!multiClickTimer.isRunning()) {
                    multiClickTimer.restart();
                }
            }
        });

        this.add(historyButton, BorderLayout.SOUTH);


        JButton deleteButton = new JButton("大苏打撒啊大大阿达×");
        deleteButton.setFont(new Font("Arial", Font.BOLD, 12));
        deleteButton.setForeground(Color.GRAY); // 默认灰色
        deleteButton.setBorderPainted(false);
        deleteButton.setContentAreaFilled(false);
        deleteButton.setFocusPainted(false);
        deleteButton.setPreferredSize(new Dimension(20, 20));

// 添加悬停效果
        deleteButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                deleteButton.setForeground(Color.RED);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                deleteButton.setForeground(Color.GRAY);
            }
        });

        deleteButton.addActionListener(e -> {
            // 添加确认对话框
            this.remove(deleteButton);
            this.revalidate();
            this.repaint();
        });
        this.add(deleteButton, BorderLayout.SOUTH);

        createAndShowGUI();
    }



    private void createAndShowGUI() {
        // 使用垂直布局的Box容器
        Box buttonListContainer = Box.createVerticalBox();
        buttonListContainer.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        historyJPanel2.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        // 添加滚动面板
        JScrollPane scrollPane = new JBScrollPane(historyJPanel2);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.add(scrollPane, BorderLayout.SOUTH);

        // 示例数据
        List<String> buttonItems = new ArrayList<>();
        buttonItems.add("127.0.0.1@45612");
        buttonItems.add("127.0.0.1@45612");
        buttonItems.add("127.0.0.1@45612");
        buttonItems.add("127.0.0.1@45612");
        buttonItems.add("127.0.0.1@45612");
        buttonItems.add("127.0.0.1@45612");
        buttonItems.add("127.0.0.1@45612");
        buttonItems.add("127.0.0.1@45612");
        buttonItems.add("127.0.0.1@45612");
        buttonItems.add("127.0.0.1@45612");
        buttonItems.add("127.0.0.1@45612");
        buttonItems.add("127.0.0.1@45612");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // 边距
        gbc.fill = GridBagConstraints.HORIZONTAL;
        int buttonsPerRow = 3, i = 0;
        DebugToolsSettingState settingState = DebugToolsSettingState.getInstance(project);
        itemPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        Set<Map.Entry<String, String>> entries = settingState.getRemoteHosts().entrySet();
//        for (Map.Entry<String, String> entry : entries) {
        JPanel row1 = new JPanel(new GridLayout(1, 3, 10, 0)); // 1行3列，水平间距10
        for (String str : buttonItems) {

            i++;
            gbc.gridx = (i - 1) % buttonsPerRow;
            gbc.gridy = (i - 1) / buttonsPerRow;


            if (i%3  == 0) {
                row1 = new JPanel(new GridLayout(1, 3, 10, 0)); // 1行3列，水平间距10
                buttonListContainer.add(row1);
            }

            addButtonListItem(row1, str, gbc);

        }
        this.add(historyJPanel2, BorderLayout.SOUTH);
    }

    void getThis(){
        JButton button = new JButton("右键点击显示菜单");

        // 创建弹出菜单
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem menuItem1 = new JMenuItem("选项1");
        JMenuItem menuItem2 = new JMenuItem("选项2");

        menuItem1.addActionListener(e -> System.out.println("选项1被选择"));
        menuItem2.addActionListener(e -> System.out.println("选项2被选择"));

        popupMenu.add(menuItem1);
        popupMenu.add(menuItem2);

        // 添加鼠标监听器
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        this.add(button, BorderLayout.SOUTH);
    }

    private void addButtonListItem(Container container, String itemText, GridBagConstraints gbc) {
        // 创建主面板，使用BorderLayout布局
        JPanel itemPanel = new JPanel(new BorderLayout());
        itemPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1,new Color(78, 81, 87)),
                BorderFactory.createEmptyBorder(0, 2, 0, 0)
        ));
//        itemPanel.setMaximumSize(new Dimension(1000, 50));
        DebugToolsSettingState settingState = DebugToolsSettingState.getInstance(project);
        // 创建主按钮（实际是JLabel模拟按钮行为）
        JLabel itemLabel = new JLabel(itemText);
        itemLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        itemLabel.addMouseListener(new MouseAdapter() {
            private Timer multiClickTimer;
            private int clickCount = 0;
            {
                multiClickTimer = new Timer(300, e -> {
                    if (clickCount == 1) {

                    }
                    if (clickCount >= 2) {

                    }
                    clickCount = 0;
                });
                multiClickTimer.setRepeats(false);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                clickCount++;
                if (!multiClickTimer.isRunning()) {
                    multiClickTimer.restart();
                }
            }
        });


        // 创建删除按钮
        JButton connButton = new JButton("连接");
        connButton.setBorderPainted(false);
        connButton.setContentAreaFilled(false);
        connButton.setFocusPainted(false);
        connButton.setPreferredSize(new Dimension(50, 50));
        connButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        connButton.addActionListener(e -> {
            container.remove(itemPanel);
            container.revalidate();
            container.repaint();
        });

        // 添加组件到面板

        // 创建删除按钮
        JButton deleteButton = new JButton("×");
//        deleteButton.setFont(new Font("Arial", Font.BOLD, 14));
        deleteButton.setForeground(JBColor.RED);
        deleteButton.setBorderPainted(false);
        deleteButton.setContentAreaFilled(false);
        deleteButton.setFocusPainted(false);
        deleteButton.setPreferredSize(new Dimension(30, 30));
        deleteButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        deleteButton.addActionListener(e -> {
            container.remove(itemPanel);
            container.revalidate();
            container.repaint();
        });

        // 添加组件到面板
        itemPanel.add(itemLabel, BorderLayout.WEST);
        itemPanel.add(connButton, BorderLayout.CENTER);
        itemPanel.add(deleteButton, BorderLayout.EAST);

        // 添加到容器
        container.add(itemPanel,  gbc);
        container.add(Box.createVerticalStrut(5)); // 添加间距
    }
}

