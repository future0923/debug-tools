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
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

import org.apache.commons.lang3.StringUtils;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
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
import com.intellij.util.ui.JBUI;

import io.github.future0923.debug.tools.base.hutool.core.util.StrUtil;
import io.github.future0923.debug.tools.common.enums.RunContentType;
import io.github.future0923.debug.tools.common.utils.DebugToolsJsonUtils;
import io.github.future0923.debug.tools.idea.bundle.DebugToolsBundle;
import io.github.future0923.debug.tools.idea.context.MethodDataContext;
import io.github.future0923.debug.tools.idea.listener.data.MulticasterEventPublisher;
import io.github.future0923.debug.tools.idea.listener.data.impl.ConvertDataListener;
import io.github.future0923.debug.tools.idea.listener.data.impl.PrettyDataListener;
import io.github.future0923.debug.tools.idea.listener.data.impl.SimpleDataListener;
import io.github.future0923.debug.tools.idea.model.ParamCache;
import io.github.future0923.debug.tools.idea.setting.DebugToolsSettingState;
import io.github.future0923.debug.tools.idea.ui.combobox.ClassLoaderComboBox;
import io.github.future0923.debug.tools.idea.ui.combobox.MethodAroundComboBox;
import io.github.future0923.debug.tools.idea.utils.DebugToolsIcons;
import io.github.future0923.debug.tools.idea.utils.DebugToolsIdeaClassUtil;
import io.github.future0923.debug.tools.idea.utils.StateUtils;
import lombok.Getter;

/**
 * @author future0923
 */
public class MainPanel extends JBPanel<MainPanel> {

    private final Project project;

    private final MethodDataContext methodDataContext;

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

    // 新增：层级 TreeTable 视图
    private TreeTableView paramTreeTable;
    private ListTreeTableModelOnColumns treeTableModel;
    private DefaultMutableTreeNode treeRoot;
    private JPanel contentPanel; // CardLayout 容器
    private static final String CARD_JSON = "json";
    private static final String CARD_TABLE = "table";
    private boolean syncing = false; // 防抖，避免双向同步循环
    private String currentViewCard = CARD_JSON;
    // Header 区域折叠/表格支持
    private JPanel headerContainer; // 含工具条+表格
    private JTable headerTable;
    private javax.swing.table.DefaultTableModel headerTableModel;
    private JButton headerExpandButton; // 位于“Header:”这一行，用于展开/折叠
    private GridBagConstraints headerGbcRef; // 保存用于动态调整的约束
    private GridBagConstraints contentGbcRef; // 保存内容区的约束

    public MainPanel(Project project, MethodDataContext methodDataContext) {
        super(new GridBagLayout());
        setPreferredSize(new JBDimension(800, 700));
        this.project = project;
        applicationNameField.setText(StateUtils.getProjectAttachApplicationName(project));
        applicationNameField.setEditable(false);
        this.classLoaderComboBox = new ClassLoaderComboBox(project, 600, false);
        this.methodDataContext = methodDataContext;
        // 当前类和方法
        PsiMethod psiMethod = methodDataContext.getPsiMethod();
        PsiClass psiClass = methodDataContext.getPsiClass();
        if (psiClass != null && psiMethod != null) {
            classNameField.setText(DebugToolsIdeaClassUtil.tryInnerClassName(psiClass));
            methodNameField.setText(psiMethod.getName());
        }
        ParamCache paramCache = methodDataContext.getCache();
        if (StringUtils.isNotBlank(paramCache.getXxlJobParam())) {
            xxlJobParamField.setText(paramCache.getXxlJobParam());
        }
        DebugToolsSettingState settingState = DebugToolsSettingState.getInstance(project);
        methodAroundComboBox = new MethodAroundComboBox(project, 370);
        if (StrUtil.isNotBlank(paramCache.getMethodAround())) {
            methodAroundComboBox.setSelected(paramCache.getMethodAround());
        } else if (StrUtil.isNotBlank(settingState.getDefaultMethodAroundName())) {
            methodAroundComboBox.setSelected(settingState.getDefaultMethodAroundName());
        }
        traceMethodPanel = new TraceMethodPanel();
        traceMethodPanel.processDefaultInfo(project, paramCache.getTraceMethodDTO());
        this.publisher = new MulticasterEventPublisher();
        // 工具栏
        this.toolBar = new MainToolBar(publisher, project, this::toggleViewDirectly);
        // json编辑器
        this.editor = new MainJsonEditor(paramCache.formatContent(), methodDataContext.getParamList(), project);
        publisher.addListener(new SimpleDataListener(editor));
        publisher.addListener(new PrettyDataListener(editor));
        publisher.addListener(new ConvertDataListener(project, editor));
        initLayout();
    }

    private void initLayout() {
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
        // 旧的 Key/Value 行式 UI 替换为表格，加载缓存项到表格
        Optional.of(methodDataContext)
            .map(MethodDataContext::getCache).map(ParamCache::getItemHeaderMap)
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
                // 顶层参数节点不在 Value 列直接编辑；叶子或简单值可编辑
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
        // 稍微增大行高，避免右侧小按钮被裁切
        paramTreeTable.setRowHeight(26);
        // 在 Key（树）列展示“+”按钮，并在点击时新增数组元素
        installArrayAddButtonOnKeyColumn(paramTreeTable);

        // 内容区：CardLayout 包含 JSON 编辑器与表格
        contentPanel = new JPanel(new CardLayout());
        contentPanel.add(editor, CARD_JSON);
        contentPanel.add(new JScrollPane(paramTreeTable), CARD_TABLE);

        // 监听切换事件（作为备用，主路径使用本地回调）
        publisher.addListener(event -> {
            if (event instanceof io.github.future0923.debug.tools.idea.listener.data.event.ToggleViewEvent) {
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

    // 构建“Header:”行的展开/折叠条
    private JComponent buildHeaderToggleStrip() {
        JPanel strip = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        headerExpandButton = new JButton(Expand);
        headerExpandButton.setMargin(JBUI.emptyInsets());
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

        // 工具按钮：Add / Auth / Clear / Save / Collapse
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

    private void getAllClassLoader() {
        classLoaderComboBox.refreshClassLoader(false);
        classLoaderComboBox.setSelectedClassLoader(StateUtils.getProjectDefaultClassLoader(project));
    }

    public Map<String, String> getItemHeaderMap() {
        // 优先从表格读取（新的表格样式）
        Map<String, String> headerMap = new HashMap<>();
        if (headerTableModel != null) {
            for (int i = 0; i < headerTableModel.getRowCount(); i++) {
                Object enabled = headerTableModel.getValueAt(i, 0);
                Object key = headerTableModel.getValueAt(i, 1);
                Object val = headerTableModel.getValueAt(i, 2);
                if (Boolean.TRUE.equals(enabled) && key != null && StringUtils.isNotBlank(String.valueOf(key))) {
                    headerMap.put(String.valueOf(key), val == null ? "" : String.valueOf(val));
                }
            }
            return headerMap;
        }
        // 兼容旧实现（基于文本框行）
        headerItemMap.forEach((k, v) -> {
            String key = k.getText();
            if (StringUtils.isNotBlank(key)) {
                headerMap.put(key, v.getText());
            }
        });
        return headerMap;
    }

    // 简单的参数表模型：name/type/content
    static class ParamTableModel extends AbstractTableModel {
        static class Row {
            String name;
            String type;
            String content;

            Row(String name, String type, String content) {
                this.name = name == null ? "" : name;
                this.type = type == null ? "" : type;
                this.content = content == null ? "" : content;
            }
        }

        private final java.util.List<Row> rows = new java.util.ArrayList<>();
        private static final String[] COLS = {"name", "type", "content"};

        @Override
        public int getRowCount() {
            return rows.size();
        }

        @Override
        public int getColumnCount() {
            return 3;
        }

        @Override
        public String getColumnName(int column) {
            return COLS[column];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return String.class;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return true;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Row r = rows.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return r.name;
                case 1:
                    return r.type;
                case 2:
                    return r.content;
                default:
                    return null;
            }
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if (rowIndex < 0 || rowIndex >= rows.size())
                return;
            Row r = rows.get(rowIndex);
            String val = aValue == null ? "" : String.valueOf(aValue);
            if (columnIndex == 0)
                r.name = val;
            else if (columnIndex == 1)
                r.type = val;
            else if (columnIndex == 2)
                r.content = val;
            fireTableCellUpdated(rowIndex, columnIndex);
        }

        public void addRow(String name, String type, String content) {
            rows.add(new Row(name, type, content));
            int i = rows.size() - 1;
            fireTableRowsInserted(i, i);
        }

        public java.util.List<Row> getRows() {
            return rows;
        }

        public void clear() {
            int size = rows.size();
            if (size == 0)
                return;
            rows.clear();
            fireTableRowsDeleted(0, size - 1);
        }
    }

    // ====== TreeTable 支撑结构与工具 ======
    private enum NodeKind {
        OBJECT, ARRAY, VALUE
    }

    private static class ParamNode extends DefaultMutableTreeNode {
        String key; // 显示在“Key”列
        String type; // 仅顶层参数节点使用（RunContentType）
        String fieldName; // 字段别名（可编辑）
        NodeKind kind; // 节点类型：对象、数组、值
        Object value; // 仅 VALUE 节点使用（字符串形式保留用户输入）
        boolean topParam; // 是否为顶层参数节点

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
            // 将“Field name”展示到“Key”列中；顶层参数展示参数名 key
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
            // 基础值
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
                // 使用 fieldName 作为对象键（若为空则退回 key）
                String mapKey = StrUtil.isNotBlank(cpn.fieldName) ? cpn.fieldName : cpn.key;
                m.put(mapKey, buildJsonFromNode(ch));
            }
            return m;
        }
        // ARRAY
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < node.getChildCount(); i++) {
            DefaultMutableTreeNode ch = (DefaultMutableTreeNode)node.getChildAt(i);
            list.add(buildJsonFromNode(ch));
        }
        return list;
    }

    // TreeTable -> JSON 同步
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
                    // SIMPLE 等类型直接使用顶层 VALUE 值
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

    // JSON -> TreeTable 同步
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
                    // 若未显式提供类型，依据内容进行推断：复杂结构 -> json_entity，其他 -> simple
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
                        // 内容为对象/数组/值，递归构建
                        buildChildrenForJsonEntity(topNode, rawContent);
                    } else {
                        // 简单类型，放在顶层节点的 value
                        top.kind = NodeKind.VALUE;
                        top.value = rawContent == null ? "" : String.valueOf(rawContent);
                    }
                }
                if (treeTableModel != null)
                    treeTableModel.reload(treeRoot);
            }
        } catch (Exception ignore) {
            // ignore parse errors
        } finally {
            syncing = false;
        }
    }

    // 本地直连切换：确保按钮点击必然生效
    private void toggleViewDirectly() {
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

    private void installArrayAddButtonOnKeyColumn(TreeTable table) {
        final JTree tree = table.getTree();
        final TreeCellRenderer base = tree.getCellRenderer();
        // 右侧小按钮尺寸/间距（渲染与点击区域一致）
        final int BTN_W = 18;
        final int BTN_H = 18;
        final int BTN_GAP = 4; // 与单元格右侧/按钮之间的水平间距

        // 自定义树列渲染：为数组节点在 Key 列右侧放置一个更小的“+”按钮
        tree.setCellRenderer((t, value, selected, expanded, leaf, row, hasFocus) -> {
            Component baseComp = base.getTreeCellRendererComponent(t, value, selected, expanded, leaf, row, hasFocus);
            JPanel panel = new JPanel(new BorderLayout());
            // 透明，避免覆盖选择高亮；由表格背景负责绘制
            panel.setOpaque(false);
            panel.add(baseComp, BorderLayout.CENTER);

            // 判断是否数组节点
            boolean showPlus = false;
            if (value instanceof DefaultMutableTreeNode) {
                Object uo = ((DefaultMutableTreeNode)value).getUserObject();
                if (uo instanceof ParamNode) {
                    ParamNode pn = (ParamNode)uo;
                    showPlus = pn.kind == NodeKind.ARRAY;
                }
            }
            if (showPlus) {
                // 在渲染器右侧放置“-”和“+”两个小按钮，仅用于显示
                JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, BTN_GAP, 0));
                btnPanel.setOpaque(false);
                JButton minus = new JButton("-");
                minus.setMargin(JBUI.emptyInsets());
                minus.setFocusable(false);
                minus.setBorderPainted(false);
                minus.setContentAreaFilled(false);
                minus.setPreferredSize(new Dimension(BTN_W, BTN_H));

                JButton plus = new JButton("+");
                plus.setMargin(JBUI.emptyInsets());
                plus.setFocusable(false);
                plus.setBorderPainted(false);
                plus.setContentAreaFilled(false);
                plus.setPreferredSize(new Dimension(BTN_W, BTN_H));

                btnPanel.add(minus);
                btnPanel.add(plus);
                panel.add(btnPanel, BorderLayout.EAST);
            }
            return panel;
        });

        // 点击 Key 列时处理“添加/删除元素”动作（仅在按钮可点击区域内生效）
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (row < 0 || col != 0)
                    return; // 仅处理 Key 列
                TreePath path = tree.getPathForRow(row);
                if (path == null)
                    return;
                DefaultMutableTreeNode arrNode = (DefaultMutableTreeNode)path.getLastPathComponent();
                Object uo = arrNode.getUserObject();
                if (!(uo instanceof ParamNode))
                    return;
                ParamNode pn = (ParamNode)uo;
                if (pn.kind != NodeKind.ARRAY)
                    return;
                // 判断点击是否落在右侧按钮区域
                Rectangle cell = table.getCellRect(row, col, false);
                int x = e.getX();
                // 两个按钮的总宽度：minus 与 plus 及其间距和右侧边距
                int totalButtonsWidth = BTN_W + BTN_W + BTN_GAP + BTN_GAP; // 左间距 + 中间间距
                int rightEdge = cell.x + cell.width;
                int plusLeft = rightEdge - BTN_W - BTN_GAP; // 预留右边距
                int minusLeft = plusLeft - BTN_W - BTN_GAP; // 减号在加号左侧

                if (x >= minusLeft && x <= rightEdge) {
                    if (x >= plusLeft) {
                        // 点击“+”区域：新增一个元素
                        int newIdx = arrNode.getChildCount();
                        ParamNode child = new ParamNode(String.valueOf(newIdx), "", String.valueOf(newIdx),
                            NodeKind.VALUE, "", false);
                        arrNode.add(new DefaultMutableTreeNode(child));
                        treeTableModel.reload(arrNode);
                        tree.expandPath(path);
                    } else if (x >= minusLeft) {
                        // 点击“-”区域：删除最后一个元素（若存在）
                        int count = arrNode.getChildCount();
                        if (count > 0) {
                            arrNode.remove(count - 1);
                            treeTableModel.reload(arrNode);
                            tree.expandPath(path);
                        }
                    }
                }
            }
        });
    }
}