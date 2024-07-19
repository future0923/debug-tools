package io.github.future0923.debug.power.idea.tool.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.JBPopupMenu;
import com.intellij.ui.components.JBRadioButton;
import com.intellij.ui.components.JBTextArea;
import io.github.future0923.debug.power.idea.model.ServerDisplayValue;
import io.github.future0923.debug.power.idea.setting.DebugPowerSettingState;
import io.github.future0923.debug.power.idea.utils.DebugPowerAttachUtils;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author future0923
 */
public class AttachServerMenu extends JBPopupMenu {

    private final List<JBRadioButton> radioButtonList = new ArrayList<>();

    private final JPanel radioPanel = new JPanel();

    public AttachServerMenu(Project project) {
        super();
        this.setLayout(new BorderLayout());
        initToolbar(project);
    }

    private void initToolbar(Project project) {
        radioPanel.setMinimumSize(new Dimension(500, 100));
        initVmServer();
        JPanel buttonPane = new JPanel();
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(e -> this.setVisible(false));
        buttonPane.add(cancel);
        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(e -> {
            radioPanel.removeAll();
            initVmServer();
        });
        buttonPane.add(refresh);
        JButton attach = new JButton("Attach");
        attach.addActionListener(e -> {
            radioButtonList.stream().filter(AbstractButton::isSelected).findFirst().ifPresent(button -> {
                ServerDisplayValue serverDisplayValue = ServerDisplayValue.of(button.getText());
                if (serverDisplayValue != null) {
                    DebugPowerSettingState settingState = DebugPowerSettingState.getInstance(project);
                    settingState.setAttach(serverDisplayValue);
                    DebugPowerAttachUtils.attach("127.0.0.1", 12345, project, serverDisplayValue.getKey(), serverDisplayValue.getValue(), settingState.loadAgentPath());
                }
                this.setVisible(false);
            });
        });
        buttonPane.add(attach);
        this.add(radioPanel, BorderLayout.CENTER);
        this.add(buttonPane, BorderLayout.SOUTH);
    }

    private void initVmServer() {
        ButtonGroup radioGroup = new ButtonGroup();
        DebugPowerAttachUtils.vmConsumer(size -> {
                    if (size == 0) {
                        JBTextArea textArea = new JBTextArea("No server found");
                        textArea.setEnabled(false);
                        radioPanel.add(textArea);
                    } else {
                        radioPanel.setLayout(new GridLayout(size, 1, 3, 3));
                    }
                },
                descriptor -> {
                    JBRadioButton radioButton = new JBRadioButton(ServerDisplayValue.display(descriptor.id(), descriptor.displayName()));
                    radioPanel.add(radioButton);
                    radioGroup.add(radioButton);
                    radioButtonList.add(radioButton);
                });
    }
}
