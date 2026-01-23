package io.github.future0923.debug.tools.idea.tool.ui;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBRadioButton;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;
import com.intellij.util.ui.JBUI;
import io.github.future0923.debug.tools.base.utils.DebugToolsStringUtils;
import io.github.future0923.debug.tools.idea.bundle.DebugToolsBundle;
import io.github.future0923.debug.tools.idea.model.ServerDisplayValue;
import io.github.future0923.debug.tools.idea.model.VirtualMachineDescriptorDTO;
import io.github.future0923.debug.tools.idea.setting.DebugToolsSettingState;
import io.github.future0923.debug.tools.idea.utils.DebugToolsAttachUtils;
import io.github.future0923.debug.tools.idea.utils.StateUtils;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author future0923
 */
public class AttachServerPopup {
    private final Project project;

    private final JPanel rootPanel = new JPanel(new BorderLayout());
    private final JPanel listPanel = new JPanel(new BorderLayout());

    private final List<JBRadioButton> radioButtons = new ArrayList<>();
    private final ButtonGroup buttonGroup = new ButtonGroup();

    private JBPopup popup;

    public AttachServerPopup(Project project) {
        this.project = project;
        listPanel.add(new JBLabel(DebugToolsBundle.message("attach.server.scanning")), BorderLayout.CENTER);
        JScrollPane scrollPane = new JBScrollPane(listPanel);
        scrollPane.setBorder(JBUI.Borders.empty());
        scrollPane.setPreferredSize(new Dimension(420, 160));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancel = new JButton(DebugToolsBundle.message("action.cancel"));
        cancel.addActionListener(e -> popup.cancel());
        buttonPanel.add(cancel);
        JButton refresh = new JButton(DebugToolsBundle.message("action.refresh"));
        refresh.addActionListener(e -> loadVmAsync());
        buttonPanel.add(refresh);
        JButton attach = new JButton(DebugToolsBundle.message("action.attach"));
        attach.addActionListener(e -> doAttach());
        buttonPanel.add(attach);
        rootPanel.add(scrollPane, BorderLayout.CENTER);
        rootPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    public void show(Point location) {
        popup = JBPopupFactory.getInstance()
                .createComponentPopupBuilder(rootPanel, null)
                .setTitle(DebugToolsBundle.message("attach.server.title"))
                .setResizable(true)
                .setMovable(false)
                .setRequestFocus(true)
                .setCancelOnClickOutside(true)
                .setCancelOnOtherWindowOpen(true)
                .createPopup();
        loadVmAsync();
        popup.show(new RelativePoint(location));
    }

    /**
     * 后台异步加载 JVM（绝不阻塞 EDT）
     */
    private void loadVmAsync() {
        listPanel.removeAll();
        listPanel.add(new JBLabel(DebugToolsBundle.message("attach.server.scanning")), BorderLayout.CENTER);
        listPanel.revalidate();
        listPanel.repaint();

        radioButtons.clear();
        buttonGroup.clearSelection();

        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            List<VirtualMachineDescriptorDTO> jvmList = DebugToolsAttachUtils.vmList();
            ApplicationManager.getApplication().invokeLater(() -> {
                listPanel.removeAll();
                if (jvmList.isEmpty()) {
                    JBTextArea empty = new JBTextArea(DebugToolsBundle.message("attach.server.menu.no.server.found"));
                    empty.setEnabled(false);
                    listPanel.add(empty, BorderLayout.CENTER);
                } else {
                    JPanel radios = new JPanel(new GridLayout(jvmList.size(), 1, 3, 3));
                    for (VirtualMachineDescriptorDTO descriptor : jvmList) {
                        JBRadioButton radio = new JBRadioButton(ServerDisplayValue.display(descriptor.getId(), descriptor.getDisplayName()));
                        buttonGroup.add(radio);
                        radios.add(radio);
                        radioButtons.add(radio);
                    }
                    listPanel.add(radios, BorderLayout.NORTH);
                }
                listPanel.revalidate();
                listPanel.repaint();
            }, project.getDisposed());
        });
    }

    /**
     * 执行 Attach
     */
    private void doAttach() {
        radioButtons.stream()
                .filter(AbstractButton::isSelected)
                .findFirst()
                .ifPresent(button -> {
                    popup.cancel();
                    ApplicationManager.getApplication().executeOnPooledThread(()->{
                        ServerDisplayValue value = ServerDisplayValue.of(button.getText());
                        if (value == null) {
                            return;
                        }
                        DebugToolsSettingState settingState = DebugToolsSettingState.getInstance(project);
                        String agentPath = settingState.loadAgentPath();
                        if (DebugToolsStringUtils.isBlank(agentPath)) {
                            return;
                        }
                        DebugToolsAttachUtils.attachLocal(
                                project,
                                value.getKey(),
                                value.getValue(),
                                agentPath,
                                () -> {
                                    StateUtils.getClassLoaderComboBox(project).refreshClassLoaderLater(true);
                                    StateUtils.getPrintSqlPanel(project).refresh();
                                    settingState.setLocal(true);
                                }
                        );
                    });
                });
    }
}
