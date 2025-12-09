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
package io.github.future0923.debug.tools.idea.ui.main;

import static io.github.future0923.debug.tools.idea.utils.DebugToolsIcons.Header.Expand;
import static io.github.future0923.debug.tools.idea.utils.DebugToolsIcons.Header.ExpandDown;

import java.awt.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.commons.lang3.StringUtils;

import com.intellij.openapi.project.Project;
import com.intellij.ui.Gray;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.dualView.TreeTableView;
import com.intellij.ui.treeStructure.treetable.ListTreeTableModelOnColumns;
import com.intellij.ui.treeStructure.treetable.TreeColumnInfo;
import com.intellij.ui.treeStructure.treetable.TreeTable;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBDimension;

import io.github.future0923.debug.tools.base.hutool.core.util.StrUtil;
import io.github.future0923.debug.tools.common.dto.RunDTO;
import io.github.future0923.debug.tools.common.enums.RunContentType;
import io.github.future0923.debug.tools.common.utils.DebugToolsJsonUtils;
import io.github.future0923.debug.tools.idea.bundle.DebugToolsBundle;
import io.github.future0923.debug.tools.idea.listener.data.DataListener;
import io.github.future0923.debug.tools.idea.listener.data.MulticasterEventPublisher;
import io.github.future0923.debug.tools.idea.listener.data.event.DataEvent;
import io.github.future0923.debug.tools.idea.listener.data.event.ToggleViewEvent;
import io.github.future0923.debug.tools.idea.listener.data.impl.ConvertDataListener;
import io.github.future0923.debug.tools.idea.listener.data.impl.PrettyDataListener;
import io.github.future0923.debug.tools.idea.listener.data.impl.SimpleDataListener;
import io.github.future0923.debug.tools.idea.model.InvokeMethodRecordDTO;
import io.github.future0923.debug.tools.idea.ui.combobox.ClassLoaderComboBox;
import io.github.future0923.debug.tools.idea.ui.combobox.MethodAroundComboBox;
import io.github.future0923.debug.tools.idea.utils.DebugToolsIcons;
import io.github.future0923.debug.tools.idea.utils.DebugToolsUIHelper;
import io.github.future0923.debug.tools.idea.utils.StateUtils;
import lombok.Getter;

/**
 * @author future0923
 */
public class InvokeMethodRecordQuickPanel extends JBPanel<InvokeMethodRecordQuickPanel> {

    private final Project project;

    private final JBTextField applicationNameField = new JBTextField();

    @Getter
    private final ClassLoaderComboBox classLoaderComboBox;

    @Getter
    private final MethodAroundComboBox methodAroundComboBox;

    private final JButton refreshButton = new JButton(DebugToolsBundle.message("action.refresh"));

    private final JBTextField classNameField = new JBTextField();

    private final JBTextField methodNameField = new JBTextField();

    @Getter
    private final TraceMethodPanel traceMethodPanel;

    private final Map<JBTextField, JBTextField> headerItemMap = new HashMap<>();

    @Getter
    private final JBTextField xxlJobParamField = new JBTextField();

    private final MainToolBar toolBar;

    private final MulticasterEventPublisher publisher;

    @Getter
    private final MainJsonEditor editor;
    // 新增：层级 TreeTable 视图（与 MainPanel 保持一致）
    private TreeTableView paramTreeTable;
    private ListTreeTableModelOnColumns treeTableModel;
    private DefaultMutableTreeNode treeRoot;
    private JPanel contentPanel; // CardLayout 容器
    private static final String CARD_JSON = "json";
    private static final String CARD_TABLE = "table";
    private boolean syncing = false; // 防抖，避免双向同步循环
    // 使用显式状态记录当前卡片，避免依赖 isShowing() 导致首次切换判断错误
    private String currentViewCard = CARD_JSON;
    // Header 区域折叠/表格支持（与 MainPanel 保持一致）
    private JPanel headerContainer; // 含工具条+表格
    private JTable headerTable;
    private javax.swing.table.DefaultTableModel headerTableModel;
    private JButton headerExpandButton; // 位于“Header:”这一行，用于展开/折叠
    private GridBagConstraints headerGbcRef; // 保存用于动态调整的约束
    private GridBagConstraints contentGbcRef; // 保存内容区的约束

    public InvokeMethodRecordQuickPanel(final Project project, final InvokeMethodRecordDTO recordDTO) {
        super(new GridBagLayout());
        setPreferredSize(new JBDimension(800, 700));
        this.project = project;
        applicationNameField.setText(StateUtils.getProjectAttachApplicationName(project));
        applicationNameField.setEditable(false);
        this.classLoaderComboBox = new ClassLoaderComboBox(project, 600, false);
        // 当前类和方法
        classNameField.setText(recordDTO.getClassName());
        methodNameField.setText(recordDTO.getMethodName());
        RunDTO formatRunDTO = recordDTO.parseRunDTO();
        if (StringUtils.isNotBlank(formatRunDTO.getXxlJobParam())) {
            xxlJobParamField.setText(formatRunDTO.getXxlJobParam());
        }
        methodAroundComboBox = new MethodAroundComboBox(project, 370);
        if (StrUtil.isNotBlank(recordDTO.getMethodAroundName())) {
            methodAroundComboBox.setSelected(recordDTO.getMethodAroundName());
        }
        traceMethodPanel = new TraceMethodPanel();
        traceMethodPanel.processDefaultInfo(project, formatRunDTO.getTraceMethodDTO());
        this.publisher = new MulticasterEventPublisher();
        // 工具栏
        // 为切换按钮提供本地直连的切换处理器，绕过事件系统，确保按钮点击必然生效
        this.toolBar = new MainToolBar(publisher, project, this::toggleViewDirectly);
        // 仍然初始化 json 编辑器以保持监听器等依赖不受影响，但界面上不再展示
        this.editor = new MainJsonEditor(DebugToolsJsonUtils.toJsonStr(formatRunDTO.getTargetMethodContent()), null, project);
        publisher.addListener(new SimpleDataListener(editor));
        publisher.addListener(new PrettyDataListener(editor));
        publisher.addListener(new ConvertDataListener(project, editor));
        initLayout(formatRunDTO);
    }

    private void initLayout(RunDTO formatRunDTO) {
        JPanel classLoaderJPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        getAllClassLoader();
        refreshButton.addActionListener(e -> {
            classLoaderComboBox.removeAllItems();
            getAllClassLoader();
            classLoaderComboBox.setSelectedClassLoader(StateUtils.getProjectDefaultClassLoader(project));
        });
        classLoaderJPanel.add(classLoaderComboBox);
        classLoaderJPanel.add(refreshButton);
        JPanel methodAroundPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        methodAroundPanel.add(methodAroundComboBox);
        methodAroundPanel.add(methodAroundComboBox.getMethodAroundPanel());
        // Header 折叠区容器（按钮工具 + 表格）
        headerContainer = buildHeaderTableContainer();
        headerContainer.setVisible(false); // 默认折叠
        FormBuilder formBuilder = FormBuilder.createFormBuilder();
        JPanel jPanel = formBuilder
                .addLabeledComponent(
                        new JBLabel(DebugToolsBundle.message("main.panel.application.name")),
                        applicationNameField
                )
                .addLabeledComponent(
                        new JBLabel(DebugToolsBundle.message("main.panel.class.loader")),
                        classLoaderJPanel
                )
                .addLabeledComponent(
                        new JBLabel(DebugToolsBundle.message("main.panel.current.class")),
                        classNameField
                )
                .addLabeledComponent(
                        new JBLabel(DebugToolsBundle.message("main.panel.current.method")),
                        methodNameField
                )
                .addLabeledComponent(
                        new JBLabel(DebugToolsBundle.message("main.panel.xxl.job.param")),
                        xxlJobParamField
                )
                .addLabeledComponent(
                        new JBLabel(DebugToolsBundle.message("method.around")),
                        methodAroundPanel
                )
                .addLabeledComponent(
                        new JBLabel(DebugToolsBundle.message("main.panel.trace.method")),
                        traceMethodPanel.getComponent()
                )
                .addLabeledComponent(
                        new JBLabel(DebugToolsBundle.message("main.panel.header")),
                        buildHeaderToggleStrip()
                )
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
        // 旧的 Key/Value 行式 UI 替换为表格，加载历史项到表格
        Optional.ofNullable(formatRunDTO.getHeaders())
            .ifPresent(map -> map.forEach((k, v) -> headerTableModel.addRow(new Object[] {Boolean.TRUE, k, v, ""})));

        GridBagConstraints gbc = new GridBagConstraints();
        // 将组件的填充方式设置为水平填充。这意味着组件将在水平方向上拉伸以填充其在容器中的可用空间，但不会在垂直方向上拉伸。
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(jPanel, gbc);

        gbc.fill = GridBagConstraints.LINE_START;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.gridx = 0;
        gbc.gridy = 1;
        // 在工具栏右侧添加“视图切换”按钮
        // 工具栏（切换按钮已放入 MainToolBar 内，由事件驱动）
        add(toolBar, gbc);

        // 将 Header 容器作为可折叠区域加入主布局（默认收起，不占权重）
        headerGbcRef = new GridBagConstraints();
        headerGbcRef.fill = GridBagConstraints.BOTH;
        headerGbcRef.weightx = 1;
        headerGbcRef.weighty = 0; // 折叠状态不占据额外空间
        headerGbcRef.gridx = 0;
        headerGbcRef.gridy = 2;
        add(headerContainer, headerGbcRef);

        // 初始化 TreeTable（层级展示 json_entity 内容）
        buildEmptyTree();
        ColumnInfo<?, ?>[] columns = new ColumnInfo[] {new TreeColumnInfo("Key") {
            @Override
            public String toString() {
                return "Key";
            }
        }, new ColumnInfo<DefaultMutableTreeNode, Object>("Value") {
            @Override
            public Object valueOf(DefaultMutableTreeNode treeNode) {
                ParamNode node = (ParamNode)treeNode.getUserObject();
                return node == null ? "" : node.getDisplayValue();
            }

            @Override
            public boolean isCellEditable(DefaultMutableTreeNode treeNode) {
                ParamNode node = (ParamNode)treeNode.getUserObject();
                return node != null && node.kind == NodeKind.VALUE;
            }

            @Override
            public void setValue(DefaultMutableTreeNode treeNode, Object value) {
                ParamNode node = (ParamNode)treeNode.getUserObject();
                if (node != null && node.kind == NodeKind.VALUE) {
                    node.value = value == null ? "" : String.valueOf(value);
                    treeTableModel.nodeChanged(treeNode);
                }
            }
        }, new ColumnInfo<DefaultMutableTreeNode, String>("Data Type") {
            @Override
            public String valueOf(DefaultMutableTreeNode treeNode) {
                ParamNode node = (ParamNode)treeNode.getUserObject();
                if (node == null)
                    return "";
                if (node.isTopParam())
                    return node.type;
                switch (node.kind) {
                    case OBJECT:
                        return "object";
                    case ARRAY:
                        return "array";
                    case VALUE:
                        return "value";
                    default:
                        return "";
                }
            }
        }};
        treeTableModel = new ListTreeTableModelOnColumns(treeRoot, columns);
        paramTreeTable = new TreeTableView(treeTableModel);
        paramTreeTable.setRootVisible(false);
        paramTreeTable.setRowHeight(24);
        paramTreeTable.setShowGrid(true);
        // 使用浅灰色网格线
        paramTreeTable.setGridColor(Gray._200);
        // 在 Key（树）列展示“+”按钮并处理点击以添加数组元素
        installArrayAddButtonOnKeyColumn(paramTreeTable);

        // 内容区：使用 CardLayout，支持 JSON 与表格切换
        contentPanel = new JPanel(new CardLayout());
        contentPanel.add(editor, CARD_JSON);
        contentPanel.add(new JScrollPane(paramTreeTable), CARD_TABLE);

        // 监听工具栏的 ToggleViewEvent，切换 JSON/Table 视图并做一次同步
        // 说明：即使事件分发异常，工具栏已通过本地回调保证可切换，这里作为兼容补充
        publisher.addListener(new DataListener() {
            @Override
            public void event(DataEvent dataEvent) {
                if (!(dataEvent instanceof ToggleViewEvent)) {
                    return;
                }
                toggleViewDirectly();
            }
        });

        contentGbcRef = new GridBagConstraints();
        contentGbcRef.fill = GridBagConstraints.BOTH;
        contentGbcRef.weightx = 1;
        contentGbcRef.weighty = 1; // 默认由编辑器区域占据剩余空间
        contentGbcRef.gridx = 0;
        contentGbcRef.gridy = 3;
        this.add(contentPanel, contentGbcRef);
    }

    private void getAllClassLoader() {
        classLoaderComboBox.refreshClassLoader(false);
        classLoaderComboBox.setSelectedClassLoader(StateUtils.getProjectDefaultClassLoader(project));
    }

    // 本地直连的切换方法：不依赖事件系统，确保按钮点击必然生效
    public void toggleViewDirectly() {
        CardLayout cl = (CardLayout)contentPanel.getLayout();
        if (CARD_TABLE.equals(currentViewCard)) {
            // 表格 -> JSON
            updateJsonFromTable();
            cl.show(contentPanel, CARD_JSON);
            currentViewCard = CARD_JSON;
        } else {
            // JSON -> 表格
            updateTableFromEditor();
            if (treeRoot.getChildCount() == 0) {
                addEmptyTopParam();
            }
            cl.show(contentPanel, CARD_TABLE);
            currentViewCard = CARD_TABLE;
        }
    }

    // 构建“Header:”行的展开/折叠条
    private JComponent buildHeaderToggleStrip() {
        JPanel strip = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        headerExpandButton = new JButton(Expand);
        headerExpandButton.setMargin(new Insets(0, 0, 0, 0));
        headerExpandButton.setFocusable(false);
        headerExpandButton.setToolTipText(DebugToolsBundle.message("action.expand"));
        headerExpandButton.addActionListener(e -> toggleHeaderExpanded());
        strip.add(headerExpandButton);
        return strip;
    }

    // Header 工具条 + 表格
    private JPanel buildHeaderTableContainer() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));

        // 工具按钮：Add / Auth / Clear
        JButton btnAdd = new io.github.future0923.debug.tools.idea.ui.button.RoundedIconButton(
            io.github.future0923.debug.tools.idea.utils.DebugToolsIcons.Action.Add);
        btnAdd.setToolTipText(DebugToolsBundle.message("global.param.panel.add.header.tooltip"));
        toolbar.add(btnAdd);

        JButton btnAuth = new io.github.future0923.debug.tools.idea.ui.button.RoundedIconButton(
            io.github.future0923.debug.tools.idea.utils.DebugToolsIcons.Header.Auth);
        btnAuth.setToolTipText(DebugToolsBundle.message("global.param.panel.add.auth.tooltip"));
        toolbar.add(btnAuth);

        JButton btnClear = new io.github.future0923.debug.tools.idea.ui.button.RoundedIconButton(
            io.github.future0923.debug.tools.idea.utils.DebugToolsIcons.Action.Clear);
        btnClear.setToolTipText(DebugToolsBundle.message("global.param.panel.remove.all.tooltip"));
        toolbar.add(btnClear);

        panel.add(toolbar, BorderLayout.NORTH);

        // 简化版表格：启用、Key、Value、删除
        headerTableModel =
            new DefaultTableModel(new Object[] {"", "Header Name", "Header Value", ""}, 0) {
                @Override
                public Class<?> getColumnClass(int columnIndex) {
                    return columnIndex == 0 ? Boolean.class : String.class;
                }

                @Override
                public boolean isCellEditable(int row, int col) {
                    return true;
                }
            };
        headerTable = new JTable(headerTableModel);
        headerTable.setRowHeight(28);
        headerTable.setFillsViewportHeight(true);
        JScrollPane sp = new JScrollPane(headerTable);
        sp.setBorder(null);
        sp.setPreferredSize(new JBDimension(0, 240));
        panel.add(sp, BorderLayout.CENTER);

        // 列配置：缩窄 Enabled 列，并为删除列提供按钮
        TableColumnModel colModel = headerTable.getColumnModel();
        // 0: Enabled
        if (colModel.getColumnCount() > 0) {
            javax.swing.table.TableColumn enCol = colModel.getColumn(0);
            enCol.setMinWidth(50);
            enCol.setMaxWidth(60);
            enCol.setPreferredWidth(36);
            enCol.setResizable(false);
        }
        // 3: Delete 按钮
        if (colModel.getColumnCount() > 3) {
            javax.swing.table.TableColumn delCol = colModel.getColumn(3);
            delCol.setCellRenderer(new DeleteButtonRendererMP());
            delCol.setCellEditor(new DeleteButtonEditorMP(headerTable));
            delCol.setMinWidth(40);
            delCol.setMaxWidth(52);
            delCol.setPreferredWidth(44);
            delCol.setResizable(false);
        }

        // 初始化事件
        btnAdd.addActionListener(e -> headerTableModel.addRow(new Object[] {Boolean.TRUE, "", "", ""}));
        btnAuth.addActionListener(e -> headerTableModel.addRow(new Object[] {Boolean.TRUE, "Authorization", "", ""}));
        btnClear.addActionListener(e -> headerTableModel.setRowCount(0));

        return panel;
    }

    private void toggleHeaderExpanded() {
        boolean expand = !headerContainer.isVisible();
        headerContainer.setVisible(expand);
        // 切换箭头
        headerExpandButton.setIcon(expand ? ExpandDown : Expand);
        // 互斥：展开 Header 时折叠编辑器；收起 Header 时显示编辑器
        contentPanel.setVisible(!expand);
        // 同时联动隐藏/显示顶部工具栏
        toolBar.setVisible(!expand);

        // 调整权重：展开时给 Header 占据剩余空间；收起时给内容区
        GridBagLayout layout = (GridBagLayout)getLayout();
        headerGbcRef.weighty = expand ? 1 : 0;
        contentGbcRef.weighty = expand ? 0 : 1;
        layout.setConstraints(headerContainer, headerGbcRef);
        layout.setConstraints(contentPanel, contentGbcRef);
        revalidate();
        repaint();
    }

    // ========== Header 表格专用渲染/编辑器（删除按钮） ==========
    private static class DeleteButtonRendererMP extends JButton implements javax.swing.table.TableCellRenderer {
        DeleteButtonRendererMP() {
            setOpaque(true);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setIcon(DebugToolsIcons.Action.Delete);
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

    private static class DeleteButtonEditorMP extends AbstractCellEditor
        implements javax.swing.table.TableCellEditor, java.awt.event.ActionListener {
        private final JButton button = new JButton();
        private final JTable table;

        DeleteButtonEditorMP(JTable table) {
            this.table = table;
            button.setBorderPainted(false);
            button.setContentAreaFilled(false);
            button.setIcon(DebugToolsIcons.Action.Delete);
            button.setToolTipText("Delete");
            button.addActionListener(this);
            button.setFocusable(false);
            button.setMargin(new Insets(0, 0, 0, 0));
            Dimension d = new Dimension(20, 20);
            button.setPreferredSize(d);
            button.setMinimumSize(d);
            button.setMaximumSize(new Dimension(24, 24));
        }

        @Override
        public Object getCellEditorValue() {
            return null;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
            int column) {
            return button;
        }

        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            // 先记录当前编辑行，再停止编辑，最后再修改模型，避免按钮渲染残留
            int row = table.getEditingRow();
            // 提前终止编辑，释放单元格编辑器，防止删除后按钮仍留在单元格中
            fireEditingStopped();

            if (row >= 0) {
                javax.swing.table.TableModel model = table.getModel();
                if (model instanceof javax.swing.table.DefaultTableModel) {
                    ((javax.swing.table.DefaultTableModel)model).removeRow(row);
                }
                // 调整选中行，避免焦点停留在已删除的索引上
                int rowCount = table.getRowCount();
                if (rowCount > 0) {
                    int newSel = Math.min(row, rowCount - 1);
                    if (newSel >= 0) {
                        try {
                            table.setRowSelectionInterval(newSel, newSel);
                        } catch (IllegalArgumentException ignore) {
                            // ignore
                        }
                    }
                }
            }
            // 强制刷新表格绘制
            table.revalidate();
            table.repaint();
        }
    }

    // ====== 渲染与编辑增强：树列边框 + 数组添加按钮 ======
    private void installTreeColumnBorderRenderer(TreeTable table, java.awt.Color borderColor) {
        final JTree tree = table.getTree();
        final javax.swing.tree.TreeCellRenderer base = tree.getCellRenderer();
        tree.setCellRenderer((tree1, value, selected, expanded, leaf, row, hasFocus) -> {
            java.awt.Component comp =
                base.getTreeCellRendererComponent(tree1, value, selected, expanded, leaf, row, hasFocus);
            if (comp instanceof JComponent) {
                JComponent jc = (JComponent)comp;
                jc.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, borderColor));
                jc.setOpaque(true);
            }
            return comp;
        });
    }

    private void installArrayAddButtonOnKeyColumn(TreeTable table) {
        final JTree tree = table.getTree();
        final javax.swing.tree.TreeCellRenderer base = tree.getCellRenderer();
        // 在树列右侧渲染更小的“+”按钮（仅数组节点显示），并增加单元格边框
        tree.setCellRenderer((t, value, selected, expanded, leaf, row, hasFocus) -> {
            java.awt.Component baseComp =
                base.getTreeCellRendererComponent(t, value, selected, expanded, leaf, row, hasFocus);
            JPanel panel = new JPanel(new BorderLayout());
            panel.setOpaque(true);
            panel.setBackground(selected ? table.getSelectionBackground() : table.getBackground());
            panel.add(baseComp, BorderLayout.CENTER);

            boolean showPlus = false;
            if (value instanceof DefaultMutableTreeNode) {
                Object uo = ((DefaultMutableTreeNode)value).getUserObject();
                if (uo instanceof ParamNode) {
                    showPlus = ((ParamNode)uo).kind == NodeKind.ARRAY;
                }
            }
            if (showPlus) {
                JButton plus = new JButton("+");
                plus.setMargin(new Insets(0, 4, 0, 4));
                plus.setFocusable(false);
                plus.setPreferredSize(new Dimension(18, 18));
                plus.setOpaque(false);
                panel.add(plus, BorderLayout.EAST);
            }
            panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, new java.awt.Color(200, 200, 200)));
            return panel;
        });

        // 在 Key 列点击时添加数组元素
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (row < 0 || col != 0)
                    return;
                javax.swing.tree.TreePath path = tree.getPathForRow(row);
                if (path == null)
                    return;
                DefaultMutableTreeNode arrNode = (DefaultMutableTreeNode)path.getLastPathComponent();
                Object uo = arrNode.getUserObject();
                if (!(uo instanceof ParamNode))
                    return;
                ParamNode pn = (ParamNode)uo;
                if (pn.kind != NodeKind.ARRAY)
                    return;
                int newIdx = arrNode.getChildCount();
                ParamNode child =
                    new ParamNode(String.valueOf(newIdx), "", String.valueOf(newIdx), NodeKind.VALUE, "", false);
                arrNode.add(new DefaultMutableTreeNode(child));
                treeTableModel.reload(arrNode);
                tree.expandPath(path);
            }
        });
    }

    public Map<String, String> getItemHeaderMap() {
        Map<String, String> map = new HashMap<>();
        // 优先从新表格模型读取
        if (headerTableModel != null && headerTableModel.getRowCount() > 0) {
            int rows = headerTableModel.getRowCount();
            for (int i = 0; i < rows; i++) {
                Object enabled = headerTableModel.getValueAt(i, 0);
                Object keyObj = headerTableModel.getValueAt(i, 1);
                Object valObj = headerTableModel.getValueAt(i, 2);
                boolean on = !(enabled instanceof Boolean) || (Boolean)enabled;
                String key = keyObj == null ? "" : String.valueOf(keyObj).trim();
                String val = valObj == null ? "" : String.valueOf(valObj);
                if (on && StringUtils.isNotBlank(key)) {
                    map.put(key, val);
                }
            }
            return map;
        }
        // 兼容旧的行式 UI（理论上不会再走到这里）
        headerItemMap.forEach((k, v) -> {
            String key = k.getText();
            if (StringUtils.isNotBlank(key)) {
                map.put(key, v.getText());
            }
        });
        return map;
    }

    // ====== TreeTable 支撑结构与工具（与 MainPanel 一致） ======
    private enum NodeKind {
        OBJECT, ARRAY, VALUE
    }

    private static class ParamNode extends DefaultMutableTreeNode {
        String key; // 参数名 / 字段原始名
        String type; // 仅顶层参数节点使用
        String fieldName;// 展示名（放到 Key 列）
        NodeKind kind;
        Object value; // 仅 VALUE 节点使用
        boolean topParam;

        ParamNode(String key, String type, String fieldName, NodeKind kind, Object value, boolean topParam) {
            super();
            this.key = key == null ? "" : key;
            this.type = type == null ? "" : type;
            this.fieldName = fieldName == null ? "" : fieldName;
            this.kind = kind == null ? NodeKind.VALUE : kind;
            this.value = value;
            this.topParam = topParam;
        }

        boolean isTopParam() {
            return topParam;
        }

        String getDisplayValue() {
            if (kind == NodeKind.VALUE) {
                return value == null ? "" : String.valueOf(value);
            }
            return "";
        }

        @Override
        public String toString() {
            if (isTopParam()) {
                return StrUtil.isNotBlank(key) ? key : (StrUtil.isNotBlank(fieldName) ? fieldName : "");
            }
            return StrUtil.isNotBlank(fieldName) ? fieldName : (StrUtil.isNotBlank(key) ? key : "");
        }
    }

    private void buildEmptyTree() {
        treeRoot = new DefaultMutableTreeNode(new ParamNode("ROOT", "", "", NodeKind.OBJECT, null, false));
    }

    private void clearTree() {
        if (treeRoot != null) {
            treeRoot.removeAllChildren();
            if (treeTableModel != null)
                treeTableModel.reload(treeRoot);
        }
    }

    private void addEmptyTopParam() {
        ParamNode top = new ParamNode("", RunContentType.SIMPLE.getType(), "", NodeKind.VALUE, "", true);
        DefaultMutableTreeNode tn = new DefaultMutableTreeNode(top);
        treeRoot.add(tn);
        if (treeTableModel != null)
            treeTableModel.reload(treeRoot);
    }

    private void buildChildrenForJsonEntity(DefaultMutableTreeNode parent, Object raw) {
        if (raw == null)
            return;
        if (raw instanceof Map) {
            ParamNode parentNode = (ParamNode)parent.getUserObject();
            parentNode.kind = NodeKind.OBJECT;
            for (Map.Entry<?, ?> e : ((Map<?, ?>)raw).entrySet()) {
                String k = String.valueOf(e.getKey());
                Object v = e.getValue();
                DefaultMutableTreeNode child = nodeFromValue(k, v);
                parent.add(child);
            }
        } else if (raw instanceof Collection) {
            ParamNode parentNode = (ParamNode)parent.getUserObject();
            parentNode.kind = NodeKind.ARRAY;
            int idx = 0;
            for (Object v : (Collection<?>)raw) {
                DefaultMutableTreeNode child = nodeFromValue(String.valueOf(idx++), v);
                parent.add(child);
            }
        } else if (raw.getClass().isArray()) {
            ParamNode parentNode = (ParamNode)parent.getUserObject();
            parentNode.kind = NodeKind.ARRAY;
            Object[] arr = (Object[])raw;
            for (int i = 0; i < arr.length; i++) {
                DefaultMutableTreeNode child = nodeFromValue(String.valueOf(i), arr[i]);
                parent.add(child);
            }
        } else {
            ParamNode parentNode = (ParamNode)parent.getUserObject();
            parentNode.kind = NodeKind.VALUE;
            parentNode.value = String.valueOf(raw);
        }
    }

    private DefaultMutableTreeNode nodeFromValue(String key, Object v) {
        if (v instanceof Map || v instanceof Collection || (v != null && v.getClass().isArray())) {
            ParamNode n = new ParamNode(key, "", key, v instanceof Map ? NodeKind.OBJECT : NodeKind.ARRAY, null, false);
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(n);
            buildChildrenForJsonEntity(node, v);
            return node;
        } else {
            ParamNode n = new ParamNode(key, "", key, NodeKind.VALUE, v == null ? null : String.valueOf(v), false);
            return new DefaultMutableTreeNode(n);
        }
    }

    private Object buildJsonFromNode(DefaultMutableTreeNode node) {
        ParamNode pn = (ParamNode)node.getUserObject();
        if (pn.kind == NodeKind.VALUE) {
            if (RunContentType.JSON_ENTITY.getType().equalsIgnoreCase(pn.type) && pn.isTopParam()) {
                try {
                    return DebugToolsJsonUtils.parse(String.valueOf(pn.value));
                } catch (Throwable ignore) {
                    return pn.value;
                }
            }
            return pn.value;
        }
        if (pn.kind == NodeKind.OBJECT) {
            Map<String, Object> m = new HashMap<>();
            for (int i = 0; i < node.getChildCount(); i++) {
                DefaultMutableTreeNode ch = (DefaultMutableTreeNode)node.getChildAt(i);
                ParamNode cpn = (ParamNode)ch.getUserObject();
                String mapKey = StrUtil.isNotBlank(cpn.fieldName) ? cpn.fieldName : cpn.key;
                m.put(mapKey, buildJsonFromNode(ch));
            }
            return m;
        }
        java.util.List<Object> list = new java.util.ArrayList<>();
        for (int i = 0; i < node.getChildCount(); i++) {
            DefaultMutableTreeNode ch = (DefaultMutableTreeNode)node.getChildAt(i);
            list.add(buildJsonFromNode(ch));
        }
        return list;
    }

    // 将树状表格内容同步到 JSON 编辑器
    private void updateJsonFromTable() {
        if (syncing)
            return;
        try {
            syncing = true;
            Map<String, Object> outer = new HashMap<>();
            for (int i = 0; i < treeRoot.getChildCount(); i++) {
                DefaultMutableTreeNode tn = (DefaultMutableTreeNode)treeRoot.getChildAt(i);
                ParamNode top = (ParamNode)tn.getUserObject();
                if (StrUtil.isBlank(top.key))
                    continue;
                Map<String, Object> inner = new HashMap<>();
                if (StrUtil.isNotBlank(top.type))
                    inner.put("type", top.type);
                Object contentVal;
                if (RunContentType.JSON_ENTITY.getType().equalsIgnoreCase(top.type)) {
                    contentVal = buildJsonFromNode(tn);
                } else {
                    contentVal = top.kind == NodeKind.VALUE ? top.value : buildJsonFromNode(tn);
                }
                if (contentVal != null)
                    inner.put("content", contentVal);
                outer.put(top.key, inner);
            }
            // 使用统一的 DebugToolsJsonUtils 序列化，避免差异
            String json = DebugToolsJsonUtils.toJsonStr(outer);
            editor.setText(json);
        } finally {
            syncing = false;
        }
    }

    // 将 JSON 编辑器内容同步到树状表格
    @SuppressWarnings("unchecked")
    private void updateTableFromEditor() {
        if (syncing)
            return;
        try {
            syncing = true;
            clearTree();
            Object obj = DebugToolsJsonUtils.toBean(editor.getText(), Map.class);
            if (obj instanceof Map) {
                Map<String, Object> m = (Map<String, Object>)obj;
                for (Map.Entry<String, Object> en : m.entrySet()) {
                    String name = en.getKey();
                    Object val = en.getValue();
                    String type = "";
                    Object rawContent = null;
                    if (val instanceof Map) {
                        Object t = ((Map<?, ?>)val).get("type");
                        Object c = ((Map<?, ?>)val).get("content");
                        Object v = ((Map<?, ?>)val).get("value");
                        type = t == null ? "" : String.valueOf(t);
                        rawContent = c != null ? c : v;
                    } else {
                        rawContent = val;
                    }
                    if (StrUtil.isBlank(type)) {
                        if (rawContent instanceof Map || rawContent instanceof Collection
                            || (rawContent != null && rawContent.getClass().isArray())) {
                            type = RunContentType.JSON_ENTITY.getType();
                        } else {
                            type = RunContentType.SIMPLE.getType();
                        }
                    }
                    ParamNode top = new ParamNode(name, type, name, NodeKind.VALUE, null, true);
                    DefaultMutableTreeNode topNode = new DefaultMutableTreeNode(top);
                    treeRoot.add(topNode);
                    if (RunContentType.JSON_ENTITY.getType().equalsIgnoreCase(type)) {
                        buildChildrenForJsonEntity(topNode, rawContent);
                    } else {
                        top.kind = NodeKind.VALUE;
                        top.value = rawContent == null ? "" : String.valueOf(rawContent);
                    }
                }
                if (treeTableModel != null)
                    treeTableModel.reload(treeRoot);
            }
        } catch (Exception ignore) {
            // 如果解析失败则不更新表格，避免破坏用户数据
        } finally {
            syncing = false;
        }
    }
}