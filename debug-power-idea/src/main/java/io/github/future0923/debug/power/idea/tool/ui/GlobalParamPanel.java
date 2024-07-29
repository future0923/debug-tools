package io.github.future0923.debug.power.idea.tool.ui;

import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import io.github.future0923.debug.power.client.DebugPowerSocketClient;
import io.github.future0923.debug.power.client.holder.ClientSocketHolder;
import io.github.future0923.debug.power.common.utils.DebugPowerClassUtils;
import io.github.future0923.debug.power.idea.client.ApplicationClientHolder;
import io.github.future0923.debug.power.idea.setting.DebugPowerSettingState;
import io.github.future0923.debug.power.idea.utils.DebugPowerNotifierUtil;
import io.github.future0923.debug.power.idea.utils.DebugPowerUIHelper;
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

    private final DebugPowerSettingState settingState;

    @Getter
    private final JTextPane textField = new JTextPane();

    @Getter
    private final JTextPane attached = new JTextPane();

    private final Map<JBTextField, JBTextField> headerItemMap = new HashMap<>();

    private final List<JPanel> headerPanelList = new ArrayList<>();

    private JPanel jPanel;

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
        jPanel = formBuilder.addComponentFillVertically(new JPanel(), 0).getPanel();
        JPanel globalHeaderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        globalHeaderPanel.add(new JBLabel("Global header:"));
        JButton addHeaderButton = new JButton("Add");
        addHeaderButton.setToolTipText("Add global header item");
        addHeaderButton.addActionListener(e -> {
            headerPanelList.add(DebugPowerUIHelper.addHeaderComponentItem(jPanel, formBuilder, 100, 230, null, null, headerItemMap));
            DebugPowerUIHelper.refreshUI(formBuilder);
        });
        globalHeaderPanel.add(addHeaderButton);
        JButton addAuthHeaderButton = new JButton("Auth");
        addAuthHeaderButton.setToolTipText("Add Authorization global header item");
        addAuthHeaderButton.addActionListener(e -> {
            headerPanelList.add(DebugPowerUIHelper.addHeaderComponentItem(jPanel, formBuilder, 100, 230, "Authorization", null, headerItemMap));
            DebugPowerUIHelper.refreshUI(formBuilder);
        });
        globalHeaderPanel.add(addAuthHeaderButton);
        JButton removeAllHeaderButton = new JButton("DelAll");
        removeAllHeaderButton.setToolTipText("Remove all global header item");
        removeAllHeaderButton.addActionListener(e -> {
            clearHeader();
            DebugPowerNotifierUtil.notifyInfo(project, "Global header remove all successfully");
        });
        globalHeaderPanel.add(removeAllHeaderButton);
        JButton saveHeaderButton = new JButton("Save");
        saveHeaderButton.setToolTipText("Save global header item");
        saveHeaderButton.addActionListener(e -> {
            settingState.clearGlobalHeaderCache();
            headerItemMap.forEach((k, v) -> {
                String key = k.getText();
                if (StringUtils.isNotBlank(key)) {
                    settingState.putGlobalHeader(key, v.getText());
                }
            });
            DebugPowerNotifierUtil.notifyInfo(project, "Global header saved successfully");
        });
        globalHeaderPanel.add(saveHeaderButton);
        formBuilder.addComponent(panel);
        formBuilder.addComponent(globalHeaderPanel);
        settingState.getGlobalHeader().forEach((k, v) -> headerPanelList.add(DebugPowerUIHelper.addHeaderComponentItem(jPanel, formBuilder, 100, 230, k, v, headerItemMap)));
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
                    DebugPowerSocketClient client = ApplicationClientHolder.CLIENT;
                    if (client == null) {
                        attached.setText("UnAttached");
                        attached.setBackground(JBColor.RED);
                        textField.setVisible(false);
                    } else {
                        if (!client.isClosed()) {
                            textField.setText(DebugPowerClassUtils.getShortClassName(ApplicationClientHolder.APPLICATION_NAME));
                            textField.setVisible(true);
                            attached.setText("Connected");
                            attached.setBackground(JBColor.GREEN);
                        } else {
                            if (client.getHolder().getRetry() == ClientSocketHolder.FAIL) {
                                attached.setText("Fail");
                            } else if (client.getHolder().getRetry() == ClientSocketHolder.RETRYING) {
                                attached.setText("Reconnect");
                            } else if (client.getHolder().getRetry() == ClientSocketHolder.INIT) {
                                attached.setText("connecting");
                            }
                            attached.setBackground(JBColor.RED);
                            textField.setVisible(true);
                        }
                    }
                },
                0,
                1,
                TimeUnit.SECONDS
        );
    }

    public void clearHeader() {
        for (JPanel panel : headerPanelList) {
            jPanel.remove(panel);
        }
        jPanel.revalidate();
        jPanel.repaint();
        headerItemMap.clear();
    }
}
