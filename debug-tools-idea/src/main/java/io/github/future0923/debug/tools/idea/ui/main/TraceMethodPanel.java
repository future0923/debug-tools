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

    private final JBCheckBox traceSkipStartGetSetCheckBox = new JBCheckBox("Skip get/set method");

    private final JBTextField traceBusinessPackage = new JBTextField();

    private final JBTextField traceIgnorePackage = new JBTextField();

    public TraceMethodPanel() {
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JPanel traceMethodPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        traceMethodPanel.add(traceMethodCheckBox);
        panel.add(traceMethodPanel);
        paramPanel.setLayout(new BoxLayout(paramPanel, BoxLayout.Y_AXIS));
        JPanel traceParamPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        JBLabel maxDepthLabel = new JBLabel("Max depth:");
        maxDepth.setPreferredSize(new Dimension(80, maxDepth.getPreferredSize().height));

        JPanel ignorePackagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        JPanel businessPackageWrapper = new JPanel(new BorderLayout());
        businessPackageWrapper.add(traceBusinessPackage, BorderLayout.CENTER);
        businessPackageWrapper.setPreferredSize(new Dimension(200, traceBusinessPackage.getPreferredSize().height));
        JPanel ignorePackageWrapper = new JPanel(new BorderLayout());
        ignorePackageWrapper.add(traceIgnorePackage, BorderLayout.CENTER);
        ignorePackageWrapper.setPreferredSize(new Dimension(200, traceIgnorePackage.getPreferredSize().height));
        ignorePackagePanel.add(new JBLabel("Business package:"));
        ignorePackagePanel.add(businessPackageWrapper);
        ignorePackagePanel.add(new JBLabel("Ignore package:"));
        ignorePackagePanel.add(ignorePackageWrapper);

        traceParamPanel.add(maxDepthLabel);
        traceParamPanel.add(maxDepth);
        traceParamPanel.add(traceMyBatisCheckBox);
        traceParamPanel.add(traceSqlCheckBox);
        traceParamPanel.add(traceSkipStartGetSetCheckBox);
        traceParamPanel.add(ignorePackagePanel);
        paramPanel.add(traceParamPanel);
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
        traceSkipStartGetSetCheckBox.setSelected(traceMethodDTO.getTraceSkipStartGetSetCheckBox());
        traceBusinessPackage.setText(traceMethodDTO.getTraceBusinessPackageRegexp());
        traceIgnorePackage.setText(traceMethodDTO.getTraceIgnorePackageRegexp());
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
            if (traceMethodDTO.getTraceSkipStartGetSetCheckBox() != null) {
                traceSkipStartGetSetCheckBox.setSelected(traceMethodDTO.getTraceSkipStartGetSetCheckBox());
            }
            if (traceMethodDTO.getTraceBusinessPackageRegexp() != null) {
                traceBusinessPackage.setText(traceMethodDTO.getTraceBusinessPackageRegexp());
            }
            if (traceMethodDTO.getTraceIgnorePackageRegexp() != null) {
                traceIgnorePackage.setText(traceMethodDTO.getTraceIgnorePackageRegexp());
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

    public boolean isTraceSkipStartGetSetCheckBox() {
        return traceSkipStartGetSetCheckBox.isSelected();
    }

    public void setTraceSkipStartGetSetCheckBox(boolean traceSkipStartGetSetCheckBox) {
        this.traceSkipStartGetSetCheckBox.setSelected(traceSkipStartGetSetCheckBox);
    }

    public String getTraceBusinessPackage() {
        return traceBusinessPackage.getText();
    }

    public void setTraceBusinessPackage(String traceBusinessPackage) {
        this.traceBusinessPackage.setText(traceBusinessPackage);
    }

    public String getTraceIgnorePackage() {
        return traceIgnorePackage.getText();
    }

    public void setTraceIgnorePackage(String traceIgnorePackage) {
        this.traceIgnorePackage.setText(traceIgnorePackage);
    }

}
