package io.github.future0923.debug.power.idea.tool.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.JBPopupMenu;
import com.intellij.ui.components.JBCheckBox;
import io.github.future0923.debug.power.idea.setting.DebugPowerSettingState;
import io.github.future0923.debug.power.idea.tool.DebugPowerToolWindow;
import io.github.future0923.debug.power.idea.tool.action.ClearCacheType;
import io.github.future0923.debug.power.idea.utils.DebugPowerNotifierUtil;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author future0923
 */
public class ClearCacheMenu extends JBPopupMenu {

    private final Project project;

    private final List<JBCheckBox> checkBoxList = new ArrayList<>();

    public ClearCacheMenu(Project project, DebugPowerToolWindow toolWindow) {
        super();
        this.project = project;
        this.setLayout(new BorderLayout());
        DebugPowerSettingState settingState = DebugPowerSettingState.getInstance(project);
        initItem();
        initButton(settingState, toolWindow);
    }

    private void initButton(DebugPowerSettingState settingState, DebugPowerToolWindow toolWindow) {
        JPanel buttonPane = new JPanel();
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(e -> this.setVisible(false));
        buttonPane.add(cancel);
        JButton clear = new JButton("Clear");
        clear.addActionListener(e -> {
            checkBoxList.stream().filter(AbstractButton::isSelected).forEach(box -> {
                if (ClearCacheType.CORE_JAR.getType().equals(box.getText())) {
                    settingState.clearAgentCache();
                }
                if (ClearCacheType.METHOD_PARAM.getType().equals(box.getText())) {
                    settingState.clearMethodParamCache();
                }
                if (ClearCacheType.GLOBAL_HEADER.getType().equals(box.getText())) {
                    settingState.clearGlobalHeaderCache();
                    toolWindow.clearHeader();
                }
            });
            this.setVisible(false);
            DebugPowerNotifierUtil.notifyInfo(project, "Clear cache successful");
        });
        buttonPane.add(clear);
        JButton clearAll = new JButton("Clear all");
        clearAll.addActionListener(e -> {
            settingState.clearAllCache();
            toolWindow.clearHeader();
            this.setVisible(false);
            DebugPowerNotifierUtil.notifyInfo(project, "Cache all successful");
        });
        buttonPane.add(clearAll);
        this.add(buttonPane, BorderLayout.SOUTH);
    }

    private void initItem() {
        checkBoxList.add(new JBCheckBox(ClearCacheType.CORE_JAR.getType()));
        checkBoxList.add(new JBCheckBox(ClearCacheType.METHOD_PARAM.getType()));
        checkBoxList.add(new JBCheckBox(ClearCacheType.GLOBAL_HEADER.getType()));
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1, 3, 3));
        for (JBCheckBox box : checkBoxList) {
            panel.add(box);
        }
        this.add(panel, BorderLayout.CENTER);
    }
}
