package io.github.future0923.debug.tools.idea.tool.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import io.github.future0923.debug.tools.client.holder.ClientSocketHolder;
import io.github.future0923.debug.tools.common.protocal.packet.request.ServerCloseRequestPacket;
import io.github.future0923.debug.tools.common.utils.DebugToolsClassUtils;
import io.github.future0923.debug.tools.idea.client.ApplicationProjectHolder;
import io.github.future0923.debug.tools.idea.setting.DebugToolsSettingState;
import io.github.future0923.debug.tools.idea.utils.DebugToolsNotifierUtil;
import io.github.future0923.debug.tools.idea.utils.DebugToolsUIHelper;
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

    @Getter
    private final JTextPane attached = new JTextPane();

    private final Map<JBTextField, JBTextField> headerItemMap = new HashMap<>();

    private final List<JPanel> headerPanelList = new ArrayList<>();

    private JPanel jPanel;

    public GlobalParamPanel(Project project) {
        super(new GridBagLayout());
        this.project = project;
        this.settingState = DebugToolsSettingState.getInstance(project);
        initLayout();
    }

    private void initLayout() {
        JPanel attachStatusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        attached.setEditable(false);
        attached.setForeground(JBColor.WHITE);
        attached.setOpaque(true);
        attachStatusPanel.add(new JBLabel("Attach status:"));
        attachStatusPanel.add(local);
        attachStatusPanel.add(attached);
        attachStatusPanel.add(textField);
        JPanel attachButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        JButton closeButton = new JButton("Close");
        attachButtonPanel.add(closeButton);
        closeButton.addActionListener(e -> ApplicationProjectHolder.close(project));
        JButton stopButton = new JButton("Stop");
        attachButtonPanel.add(stopButton);
        stopButton.addActionListener(e -> {
            try {
                ApplicationProjectHolder.send(project, new ServerCloseRequestPacket());
            } catch (Exception ex) {
                Messages.showErrorDialog(project, ex.getMessage(), "Stop Server Fail");
            }
            ApplicationProjectHolder.close(project);
            unAttached(attachButtonPanel);
        });
        FormBuilder formBuilder = FormBuilder.createFormBuilder();
        jPanel = formBuilder.addComponentFillVertically(new JPanel(), 0).getPanel();
        JPanel globalHeaderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        globalHeaderPanel.add(new JBLabel("Global header:"));
        JButton addHeaderButton = new JButton("Add");
        addHeaderButton.setToolTipText("Add global header item");
        addHeaderButton.addActionListener(e -> {
            headerPanelList.add(DebugToolsUIHelper.addHeaderComponentItem(jPanel, formBuilder, 100, 230, null, null, headerItemMap));
            DebugToolsUIHelper.refreshUI(formBuilder);
        });
        globalHeaderPanel.add(addHeaderButton);
        JButton addAuthHeaderButton = new JButton("Auth");
        addAuthHeaderButton.setToolTipText("Add Authorization global header item");
        addAuthHeaderButton.addActionListener(e -> {
            headerPanelList.add(DebugToolsUIHelper.addHeaderComponentItem(jPanel, formBuilder, 100, 230, "Authorization", null, headerItemMap));
            DebugToolsUIHelper.refreshUI(formBuilder);
        });
        globalHeaderPanel.add(addAuthHeaderButton);
        JButton removeAllHeaderButton = new JButton("DelAll");
        removeAllHeaderButton.setToolTipText("Remove all global header item");
        removeAllHeaderButton.addActionListener(e -> {
            clearHeader();
            DebugToolsNotifierUtil.notifyInfo(project, "Global header remove all successfully");
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
            DebugToolsNotifierUtil.notifyInfo(project, "Global header saved successfully");
        });
        globalHeaderPanel.add(saveHeaderButton);
        formBuilder.addComponent(attachStatusPanel);
        formBuilder.addComponent(attachButtonPanel);
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
                    ApplicationProjectHolder.Info info = ApplicationProjectHolder.getInfo(project);
                    if (info == null || info.getClient() == null) {
                        unAttached(attachButtonPanel);
                    } else {
                        local.setText(settingState.isLocal() ? "L" : "R");
                        local.setVisible(true);
                        if (!info.getClient().isClosed()) {
                            textField.setText(DebugToolsClassUtils.getShortClassName(info.getApplicationName()));
                            textField.setVisible(true);
                            attached.setText("Connected");
                            attached.setBackground(JBColor.GREEN);
                            attachButtonPanel.setVisible(true);
                        } else {
                            if (info.getClient().getHolder().getRetry() == ClientSocketHolder.FAIL) {
                                attached.setText("Fail");
                            } else if (info.getClient().getHolder().getRetry() == ClientSocketHolder.RETRYING) {
                                attached.setText("Reconnect");
                            } else if (info.getClient().getHolder().getRetry() == ClientSocketHolder.INIT) {
                                attached.setText("connecting");
                            }
                            attached.setBackground(JBColor.RED);
                            textField.setVisible(true);
                            attachButtonPanel.setVisible(true);
                        }
                    }
                },
                0,
                1,
                TimeUnit.SECONDS
        );
    }

    private void unAttached(JPanel attachButtonPanel) {
        local.setVisible(false);
        attached.setText("UnAttached");
        attached.setBackground(JBColor.RED);
        textField.setVisible(false);
        attachButtonPanel.setVisible(false);
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