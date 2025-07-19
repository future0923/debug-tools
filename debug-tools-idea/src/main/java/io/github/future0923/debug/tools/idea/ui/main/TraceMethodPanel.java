package io.github.future0923.debug.tools.idea.ui.main;

import com.intellij.openapi.project.Project;
import com.intellij.ui.JBIntSpinner;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTextField;
import io.github.future0923.debug.tools.common.dto.TraceMethodDTO;
import io.github.future0923.debug.tools.idea.setting.DebugToolsSettingState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;

/**
 * @author future0923
 */
public class TraceMethodPanel {

    private final JPanel panel = new JBPanel<>();

    private final JPanel paramPanel = new JBPanel<>();

    private final JBCheckBox traceMethodCheckBox = new JBCheckBox("Trace method");

    private final JBIntSpinner maxDepth = new JBIntSpinner(1, 1, Integer.MAX_VALUE);

    private final JBCheckBox traceMyBatisCheckBox = new JBCheckBox("MyBatis");

    private final JBCheckBox traceSqlCheckBox = new JBCheckBox("SQL");

    private final JBTextField traceIgnorePackage = new JBTextField();

    public TraceMethodPanel() {
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JPanel traceMethodPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        traceMethodPanel.add(traceMethodCheckBox);
        panel.add(traceMethodPanel);
        paramPanel.setLayout(new BoxLayout(paramPanel, BoxLayout.Y_AXIS));
        JPanel maxDepthPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        JBLabel maxDepthLabel = new JBLabel("Max depth:");
        maxDepth.setPreferredSize(new Dimension(80, maxDepth.getPreferredSize().height));

        JPanel ignorePackagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        JBLabel ignorePackageLabel = new JBLabel("Ignore package:");
        JPanel ignorePackageWrapper = new JPanel(new BorderLayout());
        ignorePackageWrapper.add(traceIgnorePackage, BorderLayout.CENTER);
        ignorePackageWrapper.setPreferredSize(new Dimension(500, traceIgnorePackage.getPreferredSize().height));
        ignorePackagePanel.add(ignorePackageLabel);
        ignorePackagePanel.add(ignorePackageWrapper);
        maxDepthPanel.add(maxDepthLabel);
        maxDepthPanel.add(maxDepth);
        maxDepthPanel.add(traceMyBatisCheckBox);
        maxDepthPanel.add(traceSqlCheckBox);
        maxDepthPanel.add(ignorePackagePanel);
        paramPanel.add(maxDepthPanel);
        paramPanel.add(ignorePackagePanel);
        traceMethodCheckBox.addItemListener(e -> paramPanel.setVisible(e.getStateChange() == ItemEvent.SELECTED));
        panel.add(paramPanel);
    }

    public void processDefaultInfo(Project project) {
        DebugToolsSettingState settingState = DebugToolsSettingState.getInstance(project);
        TraceMethodDTO traceMethodDTO = settingState.getTraceMethodDTO();
        if (traceMethodDTO == null) {
            traceMethodDTO = new TraceMethodDTO();
        }
        traceMethodCheckBox.setSelected(traceMethodDTO.getTraceMethod());
        maxDepth.setNumber(traceMethodDTO.getTraceMaxDepth());
        traceMyBatisCheckBox.setSelected(traceMethodDTO.getTraceMyBatis());
        traceSqlCheckBox.setSelected(traceMethodDTO.getTraceSQL());
        traceIgnorePackage.setText(traceMethodDTO.getTraceIgnorePackage());
        paramPanel.setVisible(traceMethodCheckBox.isSelected());
    }

    public void processDefaultInfo(Project project, TraceMethodDTO traceMethodDTO) {
        if (traceMethodDTO != null) {
            if (traceMethodDTO.getTraceMethod() != null) {
                traceMethodCheckBox.setSelected(traceMethodDTO.getTraceMethod());
            }
            if (traceMethodDTO.getTraceMaxDepth() != null) {
                maxDepth.setNumber(traceMethodDTO.getTraceMaxDepth());
            }
            if (traceMethodDTO.getTraceMyBatis() != null) {
                traceMyBatisCheckBox.setSelected(traceMethodDTO.getTraceMyBatis());
            }
            if (traceMethodDTO.getTraceSQL() != null) {
                traceSqlCheckBox.setSelected(traceMethodDTO.getTraceSQL());
            }
            if (traceMethodDTO.getTraceIgnorePackage() != null) {
                traceIgnorePackage.setText(traceMethodDTO.getTraceIgnorePackage());
            }
            paramPanel.setVisible(traceMethodCheckBox.isSelected());
        } else {
            processDefaultInfo(project);
        }
    }

    public JPanel getComponent() {
        return panel;
    }

    public boolean isTraceMethod() {
        return traceMethodCheckBox.isSelected();
    }

    public void setTraceMethod(boolean traceMethod) {
        traceMethodCheckBox.setSelected(traceMethod);
    }

    public int getMaxDepth() {
        return maxDepth.getNumber();
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth.setNumber(maxDepth);
    }

    public boolean isTraceMyBatis() {
        return traceMyBatisCheckBox.isSelected();
    }

    public void setTraceMyBatis(boolean traceMyBatis) {
        traceMyBatisCheckBox.setSelected(traceMyBatis);
    }

    public boolean isTraceSql() {
        return traceSqlCheckBox.isSelected();
    }

    public void setTraceSql(boolean traceSql) {
        traceSqlCheckBox.setSelected(traceSql);
    }

    public String getTraceIgnorePackage() {
        return traceIgnorePackage.getText();
    }

    public void setTraceIgnorePackage(String traceIgnorePackage) {
        this.traceIgnorePackage.setText(traceIgnorePackage);
    }

}
