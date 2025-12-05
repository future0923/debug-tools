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

import java.awt.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
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
import com.intellij.ui.table.JBTable;
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
import io.github.future0923.debug.tools.idea.utils.DebugToolsIdeaClassUtil;
import io.github.future0923.debug.tools.idea.utils.DebugToolsUIHelper;
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

    // 表格视图相关
    // 旧的平面表格保留定义以减少侵入，但不再使用
    private JBTable paramTable;
    private ParamTableModel paramTableModel;
    // 新增：层级 TreeTable 视图
    private TreeTableView paramTreeTable;
    private ListTreeTableModelOnColumns treeTableModel;
    private DefaultMutableTreeNode treeRoot;
    private JPanel contentPanel; // CardLayout 容器
    private static final String CARD_JSON = "json";
    private static final String CARD_TABLE = "table";
    private boolean syncing = false; // 防抖，避免双向同步循环
    private String currentViewCard = CARD_JSON;

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
        JPanel headerButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
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
                        headerButtonPanel
                )
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
        JButton addHeaderButton = new JButton(DebugToolsBundle.message("action.add"));
        headerButtonPanel.add(addHeaderButton);
        addHeaderButton.addActionListener(e -> {
            DebugToolsUIHelper.addHeaderLabelItem(jPanel, formBuilder, 150, 400, null, null, headerItemMap, project);
            DebugToolsUIHelper.refreshUI(formBuilder);
        });
        Optional.of(methodDataContext)
                .map(MethodDataContext::getCache)
                .map(ParamCache::getItemHeaderMap)
            .ifPresent(map -> map.forEach((key, value) -> DebugToolsUIHelper.addHeaderLabelItem(jPanel, formBuilder,
                150, 400, key, value, headerItemMap, project)));
        DebugToolsUIHelper.refreshUI(formBuilder);

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
        paramTreeTable.setRowHeight(24);
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

        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridy = 2;
        this.add(contentPanel, gbc);
    }

    private void getAllClassLoader() {
        classLoaderComboBox.refreshClassLoader(false);
        classLoaderComboBox.setSelectedClassLoader(StateUtils.getProjectDefaultClassLoader(project));
    }

    public Map<String, String> getItemHeaderMap() {
        Map<String, String> headerMap = new HashMap<>(headerItemMap.size());
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

        // 自定义树列渲染：为数组节点在 Key 列右侧放置一个更小的“+”按钮
        tree.setCellRenderer((t, value, selected, expanded, leaf, row, hasFocus) -> {
            Component baseComp = base.getTreeCellRendererComponent(t, value, selected, expanded, leaf, row, hasFocus);
            JPanel panel = new JPanel(new BorderLayout());
            panel.setOpaque(true);
            if (selected) {
                panel.setBackground(table.getSelectionBackground());
            } else {
                panel.setBackground(table.getBackground());
            }
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
                JButton plus = new JButton("+");
                plus.setMargin(JBUI.insets(0, 4));
                plus.setFocusable(false);
                plus.setPreferredSize(new Dimension(18, 18));
                plus.setBorderPainted(true);
                plus.setOpaque(false);
                // 渲染器中的按钮不处理事件，仅用于视觉提示
                panel.add(plus, BorderLayout.EAST);
            }
            return panel;
        });

        // 点击 Key 列时处理“添加元素”动作（包括点击到按钮区域）
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
                int newIdx = arrNode.getChildCount();
                ParamNode child =
                    new ParamNode(String.valueOf(newIdx), "", String.valueOf(newIdx), NodeKind.VALUE, "", false);
                arrNode.add(new DefaultMutableTreeNode(child));
                treeTableModel.reload(arrNode);
                tree.expandPath(path);
            }
        });
    }
}