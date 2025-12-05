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

import static io.github.future0923.debug.tools.idea.utils.DebugToolsIcons.Action.Delete;
import static io.github.future0923.debug.tools.idea.utils.DebugToolsIcons.Header.*;

import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.*;
import javax.swing.table.*;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.components.fields.ExpandableTextField;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;

import io.github.future0923.debug.tools.base.hutool.core.util.ClassUtil;
import io.github.future0923.debug.tools.base.hutool.core.util.StrUtil;
import io.github.future0923.debug.tools.client.holder.ClientSocketHolder;
import io.github.future0923.debug.tools.common.protocal.packet.request.ServerCloseRequestPacket;
import io.github.future0923.debug.tools.idea.bundle.DebugToolsBundle;
import io.github.future0923.debug.tools.idea.client.ApplicationProjectHolder;
import io.github.future0923.debug.tools.idea.setting.DebugToolsSettingState;
import io.github.future0923.debug.tools.idea.ui.button.RoundedIconButton;
import io.github.future0923.debug.tools.idea.ui.combobox.ClassLoaderComboBox;
import io.github.future0923.debug.tools.idea.ui.combobox.MethodAroundComboBox;
import io.github.future0923.debug.tools.idea.utils.DebugToolsIcons;
import io.github.future0923.debug.tools.idea.utils.DebugToolsNotifierUtil;
import io.github.future0923.debug.tools.idea.utils.StateUtils;
import lombok.Getter;

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

    // 新增状态图标标签
    private JBLabel statusIconLabel;

    // Tab 容器（将全局请求头拆分到单独的 Tab）
    private JTabbedPane tabbedPane;

    // 表格方式管理全局 Header
    private JBTable headerTable;
    private HeaderTableModel headerTableModel;
    // 顶部“全选/反选”按钮不再需要，改为表头复选框实现
    private JButton selectAllButton; // deprecated
    private JButton invertSelectButton; // deprecated

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

        // 添加状态图标标签
        statusIconLabel = new JBLabel();
        statusIconLabel.setVisible(false);
        attachStatusPanel.add(statusIconLabel);

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
        // 主面板与“全局Header”面板分离到两个 Tab
        FormBuilder mainFormBuilder = FormBuilder.createFormBuilder();
        FormBuilder headerFormBuilder = FormBuilder.createFormBuilder();
        // 注意：不要在 Header Tab 中添加多余的占位面板，避免产生大段空白

        JPanel methodAroundPanel = getMethodAroundPandel();

        JPanel globalHeaderPanel = getHeaderPanel();
        // 主 Tab 内容
        mainFormBuilder.addComponent(attachStatusPanel);
        mainFormBuilder.addComponent(classLoaderPanel);
        mainFormBuilder.addComponent(attachButtonPanel);
        mainFormBuilder.addComponent(printSqlPanel);
        mainFormBuilder.addComponent(methodAroundPanel);
        // 移除这一行，因为我们已经将按钮整合到methodAroundPanel中了
        // // formBuilder.addComponent(methodAroundComboBox.getMethodAroundPanel());
        // 顶部不再放置“全选/反选”按钮，由表头复选框统一控制

        headerFormBuilder.addComponent(globalHeaderPanel);
        createTable(headerFormBuilder);

        // 创建 Tab，并将两个面板分别加入
        tabbedPane = new JTabbedPane();
        JPanel generalPanel = mainFormBuilder.addComponentFillVertically(new JPanel(), 0).getPanel();
        tabbedPane.addTab("General", generalPanel);
        // jPanel 作为 headerFormBuilder 的面板容器
        jPanel = headerFormBuilder.getPanel();
        String headerTabName = DebugToolsBundle.message("global.param.panel.global.header");
        tabbedPane.addTab(headerTabName.substring(0, headerTabName.length() - 1), jPanel);

        // 让 TabbedPane 占满可用空间，避免“被截断”的视觉问题
        GridBagConstraints mainGbc = new GridBagConstraints();
        mainGbc.gridx = 0;
        mainGbc.gridy = 0;
        mainGbc.weightx = 1.0;
        mainGbc.weighty = 1.0;
        mainGbc.fill = GridBagConstraints.BOTH;
        this.add(tabbedPane, mainGbc);

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
                            setStatusConnected();
                            } else {
                                if (info.getClient().getHolder().getRetry() == ClientSocketHolder.FAIL) {
                                setStatusFail();
                                } else if (info.getClient().getHolder().getRetry() == ClientSocketHolder.RETRYING) {
                                setStatusReconnect();
                                } else if (info.getClient().getHolder().getRetry() == ClientSocketHolder.INIT) {
                                setStatusConnecting();
                                }
                            }
                        }
                    } catch (Exception ignored) {}
                },
                0,
                2,
                TimeUnit.SECONDS
        );
    }

    private void createTable(FormBuilder headerFormBuilder) {
        // 表格区：启用/名称/值/删除
        headerTableModel = new HeaderTableModel();
        headerTable = new JBTable(headerTableModel);
        // 设置行高为30像素
        headerTable.setRowHeight(30);
        headerTable.setFillsViewportHeight(true);
        // 使用 ExpandableTextField 作为 Header Value 列的编辑器（列索引2）
        // 列0: 启用复选框；列1: 名称；列2: 值；列3: 删除按钮
        // 在模型设置后再配置列
        // 延后到列模型存在后配置
        JScrollPane tableScroll = new JScrollPane(headerTable);
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(tableScroll, BorderLayout.CENTER);
        headerFormBuilder.addComponentFillVertically(tablePanel, 0);

        // 配置列编辑器/渲染器
        // 0: 启用（使用 JBCheckBox 渲染与编辑）
        TableColumn enabledCol = headerTable.getColumnModel().getColumn(0);
        // 使用自定义的 CheckBoxCellEditor，避免编辑时尺寸变化造成的“放大”闪烁
        enabledCol.setCellEditor(new CheckBoxCellEditor());
        enabledCol.setCellRenderer(new CheckBoxRenderer());
        enabledCol.setMaxWidth(48);
        enabledCol.setMinWidth(40);
        // 避免编辑器因焦点丢失导致的状态闪烁
        headerTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        // 表头“启用”列使用 IntelliJ ThreeStateCheckBox 渲染：全选/半选/全不选
        EnabledHeaderRenderer enabledHeaderRenderer = new EnabledHeaderRenderer(headerTable, headerTableModel);
        enabledCol.setHeaderRenderer(enabledHeaderRenderer);

        // 点击表头“启用”列，切换全选/全不选（半选仅用于显示，不参与点击切换）
        JTableHeader tableHeader = headerTable.getTableHeader();
        tableHeader.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int viewCol = headerTable.getTableHeader().columnAtPoint(e.getPoint());
                int modelCol = headerTable.convertColumnIndexToModel(viewCol);
                if (modelCol == 0) {
                    // 确保先提交当前单元格编辑，避免未提交状态导致模型不同步
                    if (headerTable.isEditing()) {
                        try {
                            headerTable.getCellEditor().stopCellEditing();
                        } catch (Exception ignore) {}
                    }
                    int state = headerTableModel.getEnabledState();
                    // 当前为全选 -> 全不选；否则 -> 全选
                    headerTableModel.setAllEnabled(state == HeaderTableModel.STATE_ALL ? false : true);
                }
            }
        });

        // 监听数据变化，刷新表头以正确显示三态
        headerTableModel.addTableModelListener(e -> headerTable.getTableHeader().repaint());

        // 2: 值列使用 ExpandableTextField
        TableColumn valueCol = headerTable.getColumnModel().getColumn(2);
        valueCol.setCellEditor(new DefaultCellEditor(new ExpandableTextField()));

        // 3: 删除按钮列
        TableColumn delCol = headerTable.getColumnModel().getColumn(3);
        delCol.setCellRenderer(new DeleteButtonRenderer());
        delCol.setCellEditor(new DeleteButtonEditor(headerTable));
        delCol.setMaxWidth(54);
        delCol.setMinWidth(48);

        // 加载已保存的 Header 项到表格（默认启用）
        settingState.getGlobalHeader().forEach((k, v) -> headerTableModel.addRow(true, k, v));
    }

    private @NotNull JPanel getHeaderPanel() {
        JPanel globalHeaderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        // 顶部按钮采用无文字的圆角图标按钮，去掉“Global header”前缀
        globalHeaderLabel = null;
        addHeaderButton = new RoundedIconButton(DebugToolsIcons.Action.Add);
        addHeaderButton.setToolTipText(DebugToolsBundle.message("global.param.panel.add.header.tooltip"));
        addHeaderButton.addActionListener(e -> {
            // 表格新增空白行（默认启用）
            if (headerTableModel != null) {
                headerTableModel.addRow(true, "", "");
            }
        });
        globalHeaderPanel.add(addHeaderButton);
        addAuthHeaderButton = new RoundedIconButton(Auth);
        addAuthHeaderButton.setToolTipText(DebugToolsBundle.message("global.param.panel.add.auth.tooltip"));
        addAuthHeaderButton.addActionListener(e -> {
            if (headerTableModel != null) {
                headerTableModel.addRow(true, "Authorization", "");
            }
        });
        globalHeaderPanel.add(addAuthHeaderButton);
        removeAllHeaderButton = new RoundedIconButton(Clear);
        removeAllHeaderButton.setToolTipText(DebugToolsBundle.message("global.param.panel.remove.all.tooltip"));
        removeAllHeaderButton.addActionListener(e -> {
            // 清空表格
            if (headerTableModel != null) {
                headerTableModel.clear();
            }
            DebugToolsNotifierUtil.notifyInfo(project,
                DebugToolsBundle.message("global.param.panel.notification.header.remove.all"));
        });
        globalHeaderPanel.add(removeAllHeaderButton);
        saveHeaderButton = new RoundedIconButton(Save);
        saveHeaderButton.setToolTipText(DebugToolsBundle.message("global.param.panel.save.tooltip"));
        saveHeaderButton.addActionListener(e -> {
            settingState.clearGlobalHeaderCache();
            if (headerTableModel != null) {
                for (int i = 0; i < headerTableModel.getRowCount(); i++) {
                    Boolean enabled = (Boolean)headerTableModel.getValueAt(i, 0);
                    String key = (String)headerTableModel.getValueAt(i, 1);
                    String value = (String)headerTableModel.getValueAt(i, 2);
                    if (Boolean.TRUE.equals(enabled) && StringUtils.isNotBlank(key)) {
                        settingState.putGlobalHeader(key, value == null ? "" : value);
                    }
                }
            }
            DebugToolsNotifierUtil.notifyInfo(project,
                DebugToolsBundle.message("global.param.panel.notification.header.save"));
        });
        globalHeaderPanel.add(saveHeaderButton);
        return globalHeaderPanel;
    }

    private @NotNull JPanel getMethodAroundPandel() {
        // Method Around Panel
        JPanel methodAroundPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // --- 第一行布局 (标签 | 按钮组 | Spacer) ---

        // 1. Method Around标签 (gridx=0, gridy=0)
        methodAroundLabel = new JBLabel(DebugToolsBundle.message("method.around"));
        // 设置标签的最大宽度
        methodAroundLabel.setMaximumSize(new Dimension(200, methodAroundLabel.getPreferredSize().height));
        // 设置标签的首选宽度
        methodAroundLabel.setPreferredSize(new Dimension(200, methodAroundLabel.getPreferredSize().height));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        // 移除右侧内边距，使其紧贴下一个组件
        gbc.insets = JBUI.insets(5, 0, 5, 5);
        methodAroundPanel.add(methodAroundLabel, gbc);
        // 2. Spacer 组件
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = JBUI.emptyInsets();
        methodAroundPanel.add(Box.createHorizontalGlue(), gbc);
        // 3. 右侧按钮组 (gridx=1, gridy=0)
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = JBUI.insets(5, 5, 5, 0);
        methodAroundPanel.add(methodAroundComboBox.getMethodAroundPanel(), gbc);

        // --- 第二行布局 (下拉框占满整行) ---

        // 4. Method Around下拉框 (gridx=0, gridy=1)
        // 逻辑处理
        methodAroundComboBox.addActionListener(
            e -> settingState.setDefaultMethodAroundName((String)methodAroundComboBox.getSelectedItem()));
        if (StrUtil.isNotBlank(settingState.getDefaultMethodAroundName())) {
            methodAroundComboBox.setSelected(settingState.getDefaultMethodAroundName());
        }

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = JBUI.insetsBottom(5);
        methodAroundPanel.add(methodAroundComboBox, gbc);

        return methodAroundPanel;
    }

    private void unAttached() {
        local.setVisible(false);
        setStatusUnattached();
        textField.setVisible(false);
        attachButtonPanel.setVisible(false);
        classLoaderPanel.setVisible(false);
        printSqlPanel.setVisible(false);
    }

    // 新增设置连接状态的方法
    private void setStatusConnected() {
        attached.setVisible(false);
        statusIconLabel.setIcon(DebugToolsIcons.Attach.Link);
        statusIconLabel.setToolTipText(DebugToolsBundle.message("global.param.panel.status.connected"));
        statusIconLabel.setVisible(true);
        attached.setBackground(JBColor.GREEN);
        attachButtonPanel.setVisible(true);
        classLoaderPanel.setVisible(true);
    }

    // 新增设置失败状态的方法
    private void setStatusFail() {
        attached.setVisible(false);
        statusIconLabel.setIcon(DebugToolsIcons.Attach.FairLink);
        statusIconLabel.setToolTipText(DebugToolsBundle.message("global.param.panel.status.fail"));
        statusIconLabel.setVisible(true);
        attached.setBackground(JBColor.RED);
        textField.setVisible(true);
        attachButtonPanel.setVisible(true);
    }

    // 新增设置重连状态的方法
    private void setStatusReconnect() {
        attached.setVisible(false);
        statusIconLabel.setIcon(DebugToolsIcons.Attach.Relinking);
        statusIconLabel.setToolTipText(DebugToolsBundle.message("global.param.panel.status.reconnect"));
        statusIconLabel.setVisible(true);
        attached.setBackground(JBColor.ORANGE);
        textField.setVisible(true);
        attachButtonPanel.setVisible(true);
    }

    // 新增设置连接中状态的方法
    private void setStatusConnecting() {
        attached.setVisible(false);
        statusIconLabel.setIcon(DebugToolsIcons.Attach.Linking);
        statusIconLabel.setToolTipText(DebugToolsBundle.message("global.param.panel.status.connecting"));
        statusIconLabel.setVisible(true); // 显示图标
        attached.setBackground(JBColor.YELLOW);
        textField.setVisible(true);
        attachButtonPanel.setVisible(true);
    }

    // 新增设置未附着状态的方法
    private void setStatusUnattached() {
        attached.setVisible(false);
        statusIconLabel.setIcon(DebugToolsIcons.Attach.Unlink);
        statusIconLabel.setToolTipText(DebugToolsBundle.message("global.param.panel.status.unattached"));
        statusIconLabel.setVisible(true);
        attached.setBackground(JBColor.GRAY);
        textField.setVisible(false);
        attachButtonPanel.setVisible(false);
        classLoaderPanel.setVisible(false);
        printSqlPanel.setVisible(false);
    }

    public void clearHeader() {
        // 兼容旧逻辑，清空面板列表
        for (JPanel panel : headerPanelList) {
            if (jPanel != null) {
                jPanel.remove(panel);
            }
        }
        if (jPanel != null) {
            jPanel.revalidate();
            jPanel.repaint();
        }
        headerItemMap.clear();
        if (headerTableModel != null) {
            headerTableModel.clear();
        }
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

        // 顶部四个图标按钮仅更新提示，不更新文本，避免出现图标+文字的拥挤效果
        if (addHeaderButton != null) {
            addHeaderButton.setToolTipText(DebugToolsBundle.message("global.param.panel.add.header.tooltip"));
        }

        if (addAuthHeaderButton != null) {
            addAuthHeaderButton.setToolTipText(DebugToolsBundle.message("global.param.panel.add.auth.tooltip"));
        }

        if (removeAllHeaderButton != null) {
            removeAllHeaderButton.setToolTipText(DebugToolsBundle.message("global.param.panel.remove.all.tooltip"));
        }

        if (saveHeaderButton != null) {
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
            setStatusUnattached();
        } else {
            setStatusConnected();
        }
        
        // Revalidate and repaint the panel to ensure UI updates
        this.revalidate();
        this.repaint();
    }
}

// 简单的表模型：两列 Key/Value，支持编辑与增删
class HeaderTableModel extends AbstractTableModel {
    // 行数据：是否启用、key、value
    static class Row {
        boolean enabled;
        String key;
        String value;

        Row(boolean enabled, String key, String value) {
            this.enabled = enabled;
            this.key = key == null ? "" : key;
            this.value = value == null ? "" : value;
        }
    }

    private final java.util.List<Row> data = new java.util.ArrayList<>();
    // 启用列三态：全不选、全选、混合
    public static final int STATE_NONE = 0;
    public static final int STATE_ALL = 1;
    public static final int STATE_MIXED = 2;
    private static final String[] COLS = {"Enabled", "Header Name", "Header Value", ""};

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public String getColumnName(int column) {
        return COLS[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return Boolean.class;
            case 1:
            case 2:
                return String.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Row r = data.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return r.enabled;
            case 1:
                return r.key;
            case 2:
                return r.value;
            case 3:
                return null; // 删除按钮列无实际值
            default:
                return null;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        // 删除按钮列也需要可编辑，才能触发单元格编辑器的点击事件
        return true;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        // 防御：在行被删除或编辑器滞后提交时避免越界
        if (rowIndex < 0 || rowIndex >= data.size()) {
            return;
        }
        Row r = data.get(rowIndex);
        if (columnIndex == 0) {
            r.enabled = aValue instanceof Boolean ? (Boolean)aValue : false;
        } else if (columnIndex == 1) {
            r.key = aValue == null ? "" : aValue.toString();
        } else if (columnIndex == 2) {
            r.value = aValue == null ? "" : aValue.toString();
        }
        // 二次校验，防止并发删除导致的刷新越界
        if (rowIndex >= 0 && rowIndex < data.size()) {
            fireTableCellUpdated(rowIndex, columnIndex);
        }
    }

    public void addRow(boolean enabled, String k, String v) {
        data.add(new Row(enabled, k, v));
        int idx = data.size() - 1;
        fireTableRowsInserted(idx, idx);
    }

    public void removeRow(int row) {
        if (row < 0 || row >= data.size()) {
            return;
        }
        data.remove(row);
        fireTableRowsDeleted(row, row);
    }

    public void setAllEnabled(boolean enabled) {
        for (Row r : data) {
            r.enabled = enabled;
        }
        fireTableRowsUpdated(0, Math.max(0, data.size() - 1));
    }

    public void invertEnabled() {
        for (Row r : data) {
            r.enabled = !r.enabled;
        }
        fireTableRowsUpdated(0, Math.max(0, data.size() - 1));
    }

    public void clear() {
        int size = data.size();
        if (size == 0) {
            return;
        }
        data.clear();
        fireTableRowsDeleted(0, size - 1);
    }

    /**
     * 计算“启用”列的总体状态：全选/全不选/混合
     */
    public int getEnabledState() {
        if (data.isEmpty()) {
            return STATE_NONE;
        }
        boolean first = data.get(0).enabled;
        for (int i = 1; i < data.size(); i++) {
            if (data.get(i).enabled != first) {
                return STATE_MIXED;
            }
        }
        return first ? STATE_ALL : STATE_NONE;
    }
}

// 删除按钮渲染器：显示圆角图标按钮
class DeleteButtonRenderer extends JButton implements TableCellRenderer {
    public DeleteButtonRenderer() {
        super();
        setOpaque(true);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setIcon(Delete);
        setToolTipText("Delete");
        setFocusable(false);
        setMargin(new Insets(0, 0, 0, 0));
        Dimension d = new Dimension(20, 20);
        setPreferredSize(d);
        setMinimumSize(d);
        setMaximumSize(new Dimension(24, 24));
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
        int row, int column) {
        return this;
    }
}

// 删除按钮编辑器：点击删除当前行
class DeleteButtonEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
    private final JButton button;
    private final JTable table;

    public DeleteButtonEditor(JTable table) {
        this.table = table;
        this.button = new JButton();
        this.button.setBorderPainted(false);
        this.button.setContentAreaFilled(false);
        this.button.setIcon(Delete);
        this.button.setToolTipText("Delete");
        this.button.addActionListener(this);
        this.button.setFocusable(false);
        this.button.setMargin(new Insets(0, 0, 0, 0));
        Dimension d = new Dimension(20, 20);
        this.button.setPreferredSize(d);
        this.button.setMinimumSize(d);
        this.button.setMaximumSize(new Dimension(24, 24));
    }

    @Override
    public Object getCellEditorValue() {
        return null;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        return button;
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        int row = table.getEditingRow();
        if (row >= 0) {
            TableModel model = table.getModel();
            if (model instanceof HeaderTableModel) {
                ((HeaderTableModel)model).removeRow(row);
            }
        }
        fireEditingStopped();
    }
}

// 复选框渲染器，使用 IDE 的 JBCheckBox 保持一致风格
class CheckBoxRenderer extends JBCheckBox implements TableCellRenderer {
    public CheckBoxRenderer() {
        setHorizontalAlignment(SwingConstants.CENTER);
        setOpaque(false);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
        int row, int column) {
        boolean selected = false;
        if (value instanceof Boolean) {
            selected = (Boolean)value;
        }
        setSelected(selected);
        return this;
    }
}

// 自定义复选框编辑器，固定尺寸与外观，避免点击时“放大”闪烁
class CheckBoxCellEditor extends AbstractCellEditor implements TableCellEditor {
    private final JBCheckBox checkBox;

    public CheckBoxCellEditor() {
        this.checkBox = new JBCheckBox();
        this.checkBox.setHorizontalAlignment(SwingConstants.CENTER);
        this.checkBox.setOpaque(false);
        this.checkBox.setFocusable(false);
        this.checkBox.setBorderPainted(false);
        this.checkBox.setMargin(new Insets(0, 0, 0, 0));
        Dimension d = new Dimension(18, 18);
        this.checkBox.setPreferredSize(d);
        this.checkBox.setMinimumSize(d);
        this.checkBox.setMaximumSize(new Dimension(20, 20));
    }

    @Override
    public Object getCellEditorValue() {
        return checkBox.isSelected();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        boolean selected = false;
        if (value instanceof Boolean) {
            selected = (Boolean) value;
        }
        checkBox.setSelected(selected);
        return checkBox;
    }
}

// 表头渲染器：优先使用 IDEA 官方 ThreeStateCheckBox（若运行环境缺失该类则回退到自绘“—”半选样式）
class EnabledHeaderRenderer implements TableCellRenderer {
    private final HeaderTableModel model;
    private final Object triState; // 通过反射持有 ThreeStateCheckBox 实例
    private final JLabel fallbackMixed; // 回退时的“—”显示
    private final JBCheckBox fallbackCheckBox;

    public EnabledHeaderRenderer(JTable table, HeaderTableModel model) {
        this.model = model;
        Object tri = null;
        try {
            // 兼容两个可能的包名
            Class<?> cls;
            try {
                cls = Class.forName("com.intellij.ui.components.ThreeStateCheckBox");
            } catch (ClassNotFoundException e) {
                cls = Class.forName("com.intellij.openapi.ui.ThreeStateCheckBox");
            }
            tri = cls.getDeclaredConstructor().newInstance();
            // 设置基础属性
            try { cls.getMethod("setOpaque", boolean.class).invoke(tri, false); } catch (Throwable ignore) {}
            try { cls.getMethod("setFocusable", boolean.class).invoke(tri, false); } catch (Throwable ignore) {}
            JTableHeader header = table.getTableHeader();
            if (header != null) {
                try { cls.getMethod("setBorder", javax.swing.border.Border.class)
                        .invoke(tri, UIManager.getBorder("TableHeader.cellBorder")); } catch (Throwable ignore) {}
            }
        } catch (Throwable ignore) {
            // ignore
        }
        this.triState = tri;

        // 回退控件
        this.fallbackCheckBox = new JBCheckBox();
        this.fallbackCheckBox.setOpaque(false);
        this.fallbackCheckBox.setFocusable(false);
        this.fallbackCheckBox.setHorizontalAlignment(SwingConstants.CENTER);
        this.fallbackMixed = new JLabel("—", SwingConstants.CENTER);
        this.fallbackMixed.setForeground(JBColor.GRAY);
        this.fallbackMixed.setOpaque(false);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        int state = model.getEnabledState();
        if (triState != null) {
            // 使用官方 ThreeStateCheckBox
            try {
                Class<?> stateEnum = Class.forName((triState.getClass().getName()) + "$State");
                Object enumValue;
                if (state == HeaderTableModel.STATE_ALL) {
                    enumValue = Enum.valueOf((Class<Enum>) stateEnum.asSubclass(Enum.class), "SELECTED");
                } else if (state == HeaderTableModel.STATE_NONE) {
                    enumValue = Enum.valueOf((Class<Enum>) stateEnum.asSubclass(Enum.class), "NOT_SELECTED");
                } else {
                    enumValue = Enum.valueOf((Class<Enum>) stateEnum.asSubclass(Enum.class), "DONT_CARE");
                }
                triState.getClass().getMethod("setState", stateEnum).invoke(triState, enumValue);
            } catch (Throwable ignore) {}
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            panel.setOpaque(false);
            panel.add((Component) triState);
            return panel;
        } else {
            // 回退：使用复选框 + 叠加“—”显示半选
            boolean all = state == HeaderTableModel.STATE_ALL;
            boolean none = state == HeaderTableModel.STATE_NONE;
            fallbackCheckBox.setSelected(all);
            JPanel container = new JPanel();
            container.setOpaque(false);
            container.setLayout(new OverlayLayout(container));
            fallbackMixed.setVisible(!(all || none));
            container.add(fallbackMixed);
            container.add(fallbackCheckBox);
            JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            wrapper.setOpaque(false);
            wrapper.add(container);
            return wrapper;
        }
    }
}