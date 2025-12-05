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
package io.github.future0923.debug.tools.idea.utils;

import java.awt.*;
import java.util.Map;

import javax.swing.*;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.components.fields.ExpandableTextField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBDimension;

/**
 * @author future0923
 */
public class DebugToolsUIHelper {

    public static JPanel addHeaderComponentItem(JPanel jPanel, FormBuilder formBuilder, int keyWidth, int valueWidth,
        String k, String v, Map<JBTextField, JBTextField> headerItemMap, Project project) {
        return addHeaderItem(false, jPanel, formBuilder, keyWidth, valueWidth, k, v, headerItemMap, project);
    }

    public static JPanel addHeaderLabelItem(JPanel jPanel, FormBuilder formBuilder, int keyWidth, int valueWidth,
        String k, String v, Map<JBTextField, JBTextField> headerItemMap, Project project) {
        return addHeaderItem(true, jPanel, formBuilder, keyWidth, valueWidth, k, v, headerItemMap, project);
    }

    public static JPanel addHeaderItem(boolean label, JPanel jPanel, FormBuilder formBuilder, int keyWidth,
        int valueWidth, String k, String v, Map<JBTextField, JBTextField> headerItemMap, Project project) {
        JPanel headerItem = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        JBTextField headerKeyField = new JBTextField();
        if (k != null) {
            headerKeyField.setText(k);
        }
        headerKeyField.setToolTipText("Header Key");
        headerKeyField.getEmptyText().setText("Key");
        headerKeyField.setPreferredSize(new JBDimension(keyWidth, headerKeyField.getPreferredSize().height));

        // 使用新的ExpandableTextField替换原来的JBTextFieldproject
        ExpandableTextField headerValueField = new ExpandableTextField();

        // 更新map以适应新的组件类型
        JBTextField valueTextField = new JBTextField(headerValueField.getText());
        headerItemMap.put(headerKeyField, valueTextField);

        JButton removeButton = new JButton("Remove");
        removeButton.addActionListener(e1 -> {
            jPanel.remove(headerItem);
            jPanel.revalidate();
            jPanel.repaint();
            headerItemMap.remove(headerKeyField, valueTextField);
        });

        headerItem.add(headerKeyField);
        headerItem.add(headerValueField, FlowLayout.CENTER);
        headerItem.add(removeButton);
        if (label) {
            formBuilder.addLabeledComponent(new JBLabel(), headerItem);
        } else {
            formBuilder.addComponent(headerItem);
        }
        return headerItem;
    }

    public static void refreshUI(FormBuilder formBuilder) {
        JPanel formBuilderPanel = formBuilder.getPanel();
        formBuilderPanel.revalidate();
        formBuilderPanel.repaint();
    }

    public static GridBagConstraints northGridBagConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        // 将组件的填充方式设置为水平填充。这意味着组件将在水平方向上拉伸以填充其在容器中的可用空间，但不会在垂直方向上拉伸。
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        return gbc;
    }
}