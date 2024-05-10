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
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import io.github.future0923.debug.power.idea.context.MethodDataContext;
import io.github.future0923.debug.power.idea.listener.MulticasterEventPublisher;
import io.github.future0923.debug.power.idea.listener.impl.ConvertDataListener;
import io.github.future0923.debug.power.idea.listener.impl.PrettyDataListener;
import io.github.future0923.debug.power.idea.listener.impl.SimpleDataListener;
import io.github.future0923.debug.power.idea.model.ServerDisplayValue;
import io.github.future0923.debug.power.idea.setting.DebugPowerSettingState;
import io.github.future0923.debug.power.idea.ui.JsonEditor;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

/**
 * @author future0923
 */
public class MainPanel extends JBPanel<MainPanel> {

    private final ComboBox<ServerDisplayValue> serverComboBox = new ComboBox<>(500);

    private final JButton attachButton = new JButton("Attach");

    private final JButton refreshButton = new JButton("Refresh");

    private final JButton clearButton = new JButton("Clear cache");

    @Getter
    private final JBTextField authField = new JBTextField();

    private final JBTextField classNameField = new JBTextField();

    private final JBTextField methodNameField = new JBTextField();

    private final MainToolBar toolBar;

    @Getter
    private final JsonEditor editor;

    private final DebugPowerSettingState settingState;

    public MainPanel(Project project, MethodDataContext methodDataContext) {
        super(new GridBagLayout());
        setPreferredSize(new JBDimension(670, 500));
        this.settingState = DebugPowerSettingState.getInstance(project);
        // attach下拉框
        initServerComboBox();
        // 权限
        authField.setText(settingState.getHeaders().getOrDefault("Authorization", ""));
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
        this.editor = new JsonEditor(methodDataContext.cacheContent, methodDataContext.getParamList(), project);
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
            // 重新验证组件的布局
            //attachButton.revalidate();
            // 重新绘制组件
            //attachButton.repaint();
        });
        refreshButton.addActionListener(e -> {
            attachButton.setText("Attach");
            attachButton.setEnabled(true);
            settingState.setAttach(null);
            refreshServerComboBox();
        });
        clearButton.addActionListener(e -> {
            settingState.clearCache();
            try {
                String agentPath = settingState.getAgentPath();
                File file = new File(agentPath);
                if (file.exists()) {
                    file.delete();
                }
            } catch (Exception ignored) {
            }
            settingState.setAgentPath(null);
        });
    }

    private void initLayout() {
        // 服务信息Panel
        JPanel serverJPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        serverJPanel.add(serverComboBox);
        serverJPanel.add(attachButton);
        serverJPanel.add(refreshButton);
        serverJPanel.add(clearButton);
        JPanel jPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent(
                        new JBLabel("Attach server:"),
                        serverJPanel
                )
                .addLabeledComponent(
                        new JBLabel("Authorization header:"),
                        authField
                )
                .addLabeledComponent(
                        new JBLabel("Current class:"),
                        classNameField
                )
                .addLabeledComponent(
                        new JBLabel("Current method:"),
                        methodNameField
                )
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
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
        List<VirtualMachineDescriptor> list = VirtualMachine.list();
        for (VirtualMachineDescriptor descriptor : list) {
            if (descriptor.displayName().startsWith("org.gradle")
                    || descriptor.displayName().startsWith("org.jetbrains")
                    || descriptor.displayName().startsWith("com.intellij")
            ) {
                continue;
            }
            serverComboBox.addItem(new ServerDisplayValue(descriptor.id(), descriptor.displayName()));
        }
        if (settingState.getAttach() != null) {
            serverComboBox.setSelectedItem(settingState.getAttach());
            setAttached();
        }
    }

    private void setAttached() {
        attachButton.setText("Attached");
        attachButton.setEnabled(false);
    }
}
