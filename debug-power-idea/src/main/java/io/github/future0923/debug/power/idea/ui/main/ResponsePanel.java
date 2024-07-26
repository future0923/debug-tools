package io.github.future0923.debug.power.idea.ui.main;

import com.intellij.ui.EditorTextField;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBDimension;
import io.github.future0923.debug.power.base.utils.DebugPowerStringUtils;
import io.github.future0923.debug.power.common.protocal.packet.response.RunTargetMethodResponsePacket;
import io.github.future0923.debug.power.idea.navigation.ClassNameHighlighter;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * @author future0923
 */
@Getter
public class ResponsePanel extends JBPanel<ResponsePanel> {

    private final EditorTextField result;

    public ResponsePanel(RunTargetMethodResponsePacket packet) {
        super(new GridBagLayout());
        setPreferredSize(new JBDimension(670, 500));
        JBTextField classNameField = new JBTextField(packet.getClassName());
        JBTextField methodNameField = new JBTextField(packet.getMethodName());
        List<String> methodParameterTypes = packet.getMethodParameterTypes();
        JTextArea parameterTypesField = new JTextArea(methodParameterTypes.size(), 1);
        parameterTypesField.setText(" " + String.join("\n ", methodParameterTypes));
        String runResult;
        if (DebugPowerStringUtils.isNotBlank(packet.getThrowable())) {
            runResult = packet.getThrowable();
        } else {
            runResult = packet.getPrintResult() == null ? "NULL" : packet.getPrintResult();
        }
        result = new EditorTextField(runResult);
        if (DebugPowerStringUtils.isNotBlank(packet.getThrowable())) {
            ClassNameHighlighter.highlightClassNames(result);
        }
        JBScrollPane resultScroll = new JBScrollPane(result);
        resultScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        resultScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        FormBuilder formBuilder = FormBuilder.createFormBuilder();
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
        add(resultScroll, gbc);
    }
}
