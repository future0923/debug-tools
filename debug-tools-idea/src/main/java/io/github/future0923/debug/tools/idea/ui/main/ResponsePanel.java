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

import com.intellij.ide.impl.ProjectUtil;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import io.github.future0923.debug.tools.idea.bundle.DebugToolsBundle;
import com.intellij.util.ui.JBDimension;
import io.github.future0923.debug.tools.base.utils.DebugToolsStringUtils;
import io.github.future0923.debug.tools.common.protocal.packet.response.RunTargetMethodResponsePacket;
import io.github.future0923.debug.tools.idea.client.ApplicationProjectHolder;
import io.github.future0923.debug.tools.idea.ui.editor.TextEditor;
import io.github.future0923.debug.tools.idea.ui.tab.ExceptionTabbedPane;
import io.github.future0923.debug.tools.idea.ui.tab.RunResult;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * @author future0923
 */
@Getter
public class ResponsePanel extends JBPanel<ResponsePanel> {

    public ResponsePanel(Project project, RunTargetMethodResponsePacket packet) {
        super(new GridBagLayout());
        setPreferredSize(new JBDimension(800, 600));
        JBTextField classNameField = new JBTextField(packet.getClassName());
        JBTextField methodNameField = new JBTextField(packet.getMethodName());
        List<String> methodParameterTypes = packet.getMethodParameterTypes();
        JTextArea parameterTypesField = new JTextArea(methodParameterTypes.size(), 1);
        parameterTypesField.setText(" " + String.join("\n ", methodParameterTypes));
        Component resultComponent;
        if (project == null) {
            String runResult;
            if (DebugToolsStringUtils.isNotBlank(packet.getThrowable())) {
                runResult = packet.getThrowable();
            } else {
                runResult = packet.getPrintResult() == null ? "NULL" : packet.getPrintResult();
            }
            resultComponent = new TextEditor(null, runResult);
        } else {
            if (packet.isSuccess()) {
                resultComponent = new RunResult(project, packet.getPrintResult(), packet.getOffsetPath(), packet.getTraceOffsetPath(), packet.getResultClassType());
            } else {
                resultComponent = new ExceptionTabbedPane(project, packet.getThrowable(), packet.getOffsetPath());
            }
        }
        FormBuilder formBuilder = FormBuilder.createFormBuilder();
        if (packet.getClassLoaderIdentity() != null) {
            JBTextField classLoaderField = new JBTextField(packet.getClassLoaderIdentity());
            formBuilder.addLabeledComponent(
                    new JBLabel(DebugToolsBundle.message("response.panel.class.loader")),
                    classLoaderField
            );
        }
        JPanel jPanel = formBuilder
                .addLabeledComponent(
                        new JBLabel(DebugToolsBundle.message("response.panel.current.class")),
                        classNameField
                )
                .addLabeledComponent(
                        new JBLabel(DebugToolsBundle.message("response.panel.current.method")),
                        methodNameField
                )
                .addLabeledComponent(
                        new JBLabel(DebugToolsBundle.message("response.panel.parameter.types")),
                        parameterTypesField
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

        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(resultComponent, gbc);
    }
}
