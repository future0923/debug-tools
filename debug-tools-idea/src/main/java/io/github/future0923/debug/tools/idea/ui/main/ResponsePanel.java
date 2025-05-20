/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.future0923.debug.tools.idea.ui.main;

import com.intellij.ide.impl.ProjectUtil;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
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
                resultComponent = new RunResult(project, packet.getPrintResult(), packet.getOffsetPath(), packet.getResultClassType());
            } else {
                resultComponent = new ExceptionTabbedPane(project, packet.getThrowable(), packet.getOffsetPath());
            }
        }
        FormBuilder formBuilder = FormBuilder.createFormBuilder();
        if (packet.getClassLoaderIdentity() != null) {
            JBTextField classLoaderField = new JBTextField(packet.getClassLoaderIdentity());
            formBuilder.addLabeledComponent(
                    new JBLabel("Class loader:"),
                    classLoaderField
            );
        }
        JPanel jPanel = formBuilder
                .addLabeledComponent(
                        new JBLabel("Current class:"),
                        classNameField
                )
                .addLabeledComponent(
                        new JBLabel("Current method:"),
                        methodNameField
                )
                .addLabeledComponent(
                        new JBLabel("Parameter types:"),
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
