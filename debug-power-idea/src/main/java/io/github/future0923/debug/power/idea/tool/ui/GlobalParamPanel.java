package io.github.future0923.debug.power.idea.tool.ui;

import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import io.github.future0923.debug.power.common.utils.DebugPowerClassUtils;
import io.github.future0923.debug.power.idea.model.ServerDisplayValue;
import io.github.future0923.debug.power.idea.setting.DebugPowerSettingState;
import io.github.future0923.debug.power.idea.utils.DebugPowerAttachUtils;
import io.github.future0923.debug.power.idea.utils.DebugPowerNotifierUtil;
import io.github.future0923.debug.power.idea.utils.DebugPowerStoreUtils;
import io.github.future0923.debug.power.idea.utils.DebugPowerUIHelper;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author future0923
 */
public class GlobalParamPanel extends JBPanel<GlobalParamPanel> {

    private final Project project;

    private final DebugPowerSettingState settingState;

    @Getter
    private final JTextPane textField = new JTextPane();

    @Getter
    private final JTextPane attached = new JTextPane();

    private final Map<JBTextField, JBTextField> headerItemMap = new HashMap<>();

    public GlobalParamPanel(Project project) {
        super(new GridBagLayout());
        this.project = project;
        this.settingState = DebugPowerSettingState.getInstance(project);
        initLayout();
    }

    private void initLayout() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        attached.setEditable(false);
        attached.setForeground(JBColor.WHITE);
        attached.setOpaque(true);
        panel.add(new JBLabel("Attach status:"));
        panel.add(attached);
        panel.add(textField);
        FormBuilder formBuilder = FormBuilder.createFormBuilder();
        JPanel jPanel = formBuilder.addComponentFillVertically(new JPanel(), 0).getPanel();
        JPanel globalHeaderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        globalHeaderPanel.add(new JBLabel("Global header:"));
        JButton addHeaderButton = new JButton("Add");
        addHeaderButton.addActionListener(e -> {
            DebugPowerUIHelper.addHeaderComponentItem(jPanel, formBuilder, 100, 230, null, null, headerItemMap);
            DebugPowerUIHelper.refreshUI(formBuilder);
        });
        globalHeaderPanel.add(addHeaderButton);
        JButton addAuthHeaderButton = new JButton("Add Auth");
        addAuthHeaderButton.addActionListener(e -> {
            DebugPowerUIHelper.addHeaderComponentItem(jPanel, formBuilder, 100, 230, "Authorization", null, headerItemMap);
            DebugPowerUIHelper.refreshUI(formBuilder);
        });
        globalHeaderPanel.add(addAuthHeaderButton);
        JButton saveHeaderButton = new JButton("Save");
        saveHeaderButton.addActionListener(e -> {
            headerItemMap.forEach((k, v) -> {
                String key = k.getText();
                if (StringUtils.isNotBlank(key)) {
                    DebugPowerStoreUtils.putGlobalHeader(key, v.getText());
                }
            });
            DebugPowerStoreUtils.save(project);
            DebugPowerNotifierUtil.notifyInfo(project, "Global header saved successfully");
        });
        globalHeaderPanel.add(saveHeaderButton);
        formBuilder.addComponent(panel);
        formBuilder.addComponent(globalHeaderPanel);
        DebugPowerStoreUtils.getAll(project).forEach((k, v) -> DebugPowerUIHelper.addHeaderComponentItem(jPanel, formBuilder, 100, 230, k, v, headerItemMap));
        DebugPowerUIHelper.refreshUI(formBuilder);

        add(jPanel, DebugPowerUIHelper.northGridBagConstraints());

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
                    ServerDisplayValue attach = settingState.getAttach();
                    if (attach != null && DebugPowerAttachUtils.status(project, attach.getKey())) {
                        textField.setText(DebugPowerClassUtils.getShortClassName(attach.getValue()));
                        textField.setVisible(true);
                        attached.setText("Attached");
                        attached.setBackground(JBColor.GREEN);
                    } else {
                        attached.setText("UnAttached");
                        attached.setBackground(JBColor.RED);
                        textField.setVisible(false);
                        settingState.setAttach(null);
                    }
                },
                0,
                3,
                TimeUnit.SECONDS
        );
    }
}
