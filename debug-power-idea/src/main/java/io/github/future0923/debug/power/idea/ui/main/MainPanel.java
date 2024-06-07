package io.github.future0923.debug.power.idea.ui.main;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBDimension;
import io.github.future0923.debug.power.idea.context.MethodDataContext;
import io.github.future0923.debug.power.idea.listener.data.MulticasterEventPublisher;
import io.github.future0923.debug.power.idea.listener.data.impl.ConvertDataListener;
import io.github.future0923.debug.power.idea.listener.data.impl.PrettyDataListener;
import io.github.future0923.debug.power.idea.listener.data.impl.SimpleDataListener;
import io.github.future0923.debug.power.idea.model.ParamCache;
import io.github.future0923.debug.power.idea.model.ServerDisplayValue;
import io.github.future0923.debug.power.idea.setting.DebugPowerSettingState;
import io.github.future0923.debug.power.idea.ui.JsonEditor;
import io.github.future0923.debug.power.idea.utils.DebugPowerAttachUtils;
import io.github.future0923.debug.power.idea.utils.DebugPowerUIHelper;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author future0923
 */
public class MainPanel extends JBPanel<MainPanel> {

    private final Project project;

    private final MethodDataContext methodDataContext;

    private final ComboBox<ServerDisplayValue> serverComboBox = new ComboBox<>(500);

    private final JButton attachButton = new JButton("Attach");

    private final JButton refreshButton = new JButton("Refresh");

    private final JBTextField classNameField = new JBTextField();

    private final JBTextField methodNameField = new JBTextField();

    private final Map<JBTextField, JBTextField> headerItemMap = new HashMap<>();

    private final MainToolBar toolBar;

    @Getter
    private final JsonEditor editor;

    private final DebugPowerSettingState settingState;

    public MainPanel(Project project, MethodDataContext methodDataContext) {
        super(new GridBagLayout());
        setPreferredSize(new JBDimension(670, 500));
        this.project = project;
        this.methodDataContext = methodDataContext;
        this.settingState = DebugPowerSettingState.getInstance(project);
        // attach下拉框
        initServerComboBox();
        // 当前类和方法
        PsiMethod psiMethod = methodDataContext.getPsiMethod();
        PsiClass psiClass = methodDataContext.getPsiClass();
        if (psiClass != null && psiMethod != null) {
            classNameField.setText(psiClass.getQualifiedName());
            methodNameField.setText(psiMethod.getName());
        }
        MulticasterEventPublisher publisher = new MulticasterEventPublisher();
        // 工具栏
        this.toolBar = new MainToolBar(publisher);
        // json编辑器
        this.editor = new JsonEditor(methodDataContext.getCache().formatContent(), methodDataContext.getParamList(), project);
        publisher.addListener(new SimpleDataListener(editor));
        publisher.addListener(new PrettyDataListener(editor));
        publisher.addListener(new ConvertDataListener(project, editor));
        initLayout();
        initListener();
    }

    private void initListener() {
        attachButton.addActionListener(e -> {
            ServerDisplayValue item = serverComboBox.getItem();
            setAttached();
            settingState.setAttach(new ServerDisplayValue(item.getKey(), item.getValue()));
        });
        refreshButton.addActionListener(e -> {
            attachButton.setText("Attach");
            attachButton.setEnabled(true);
            settingState.setAttach(null);
            refreshServerComboBox();
        });
    }

    private void initLayout() {
        // 服务信息Panel
        JPanel serverJPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        serverJPanel.add(serverComboBox);
        serverJPanel.add(attachButton);
        serverJPanel.add(refreshButton);
        JPanel headerButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        FormBuilder formBuilder = FormBuilder.createFormBuilder();
        JPanel jPanel = formBuilder
                .addLabeledComponent(
                        new JBLabel("Attach server:"),
                        serverJPanel
                )
                .addLabeledComponent(
                        new JBLabel("Current class:"),
                        classNameField
                )
                .addLabeledComponent(
                        new JBLabel("Current method:"),
                        methodNameField
                )
                .addLabeledComponent(
                        new JBLabel("Header:"),
                        headerButtonPanel
                )
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
        JButton addHeaderButton = new JButton("Add");
        headerButtonPanel.add(addHeaderButton);
        addHeaderButton.addActionListener(e -> {
            DebugPowerUIHelper.addHeaderLabelItem(jPanel, formBuilder, 150, 400, null, null, headerItemMap);
            DebugPowerUIHelper.refreshUI(formBuilder);
        });
        Optional.of(methodDataContext)
                .map(MethodDataContext::getCache)
                .map(ParamCache::getItemHeaderMap)
                .ifPresent(map -> map.forEach((key, value) -> DebugPowerUIHelper.addHeaderLabelItem(jPanel, formBuilder, 150, 400, key, value, headerItemMap)));
        DebugPowerUIHelper.refreshUI(formBuilder);

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

        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 0;
        gbc.gridheight = -1;
        this.add(editor, gbc);
    }

    private void initServerComboBox() {
        serverComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                ServerDisplayValue displayValue;
                if (value == null) {
                    displayValue = new ServerDisplayValue("", "没有启动的服务");
                } else {
                    displayValue = (ServerDisplayValue) value;
                }
                String serverName = displayValue.getValue();
                JLabel jLabel = (JLabel) super.getListCellRendererComponent(list, serverName, index, isSelected, cellHasFocus);
                jLabel.setToolTipText(serverName);
                return jLabel;
            }
        });
        refreshServerComboBox();
    }

    private void refreshServerComboBox() {
        serverComboBox.removeAllItems();
        DebugPowerAttachUtils.vmConsumer(descriptor -> serverComboBox.addItem(new ServerDisplayValue(descriptor.id(), descriptor.displayName())));
        ServerDisplayValue attach = settingState.getAttach();
        if (attach != null) {
            // jps
            if (DebugPowerAttachUtils.status(project, attach.getKey())) {
                ComboBoxModel<ServerDisplayValue> model = serverComboBox.getModel();
                for (int i = 0; i < model.getSize(); i++) {
                    if (model.getElementAt(i).getKey().equals(attach.getKey())) {
                        serverComboBox.setSelectedIndex(i);
                    }
                }
                setAttached();
            } else {
                settingState.setAttach(null);
            }
        }
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

    private void setAttached() {
        attachButton.setText("Attached");
        attachButton.setEnabled(false);
    }
}
