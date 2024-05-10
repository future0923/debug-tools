package io.github.future0923.debug.power.idea.ui.convert;

import com.intellij.openapi.project.Project;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBRadioButton;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBDimension;
import io.github.future0923.debug.power.common.utils.DebugPowerJsonUtils;
import io.github.future0923.debug.power.idea.ui.JsonEditor;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;

/**
 * @author future0923
 */
public class ConvertPanel extends JBPanel<ConvertPanel> {

    private final Project project;

    @Getter
    private final JsonEditor jsonEditor;

    @Getter
    private final JBRadioButton json = new JBRadioButton(ConvertDataType.Json.name(), true);

    @Getter
    private final JBRadioButton query = new JBRadioButton(ConvertDataType.Query.name());

    @Getter
    private final EditorTextField editorTextField = new EditorTextField();

    public ConvertPanel(Project project, JsonEditor jsonEditor, ConvertType convertType) {
        super(new GridBagLayout());
        setPreferredSize(new JBDimension(670, 500));
        this.project = project;
        this.jsonEditor = jsonEditor;

        editorTextField.addSettingsProvider(editor -> {
            editor.setVerticalScrollbarVisible(true);
            editor.setHorizontalScrollbarVisible(true);
        });

        if (ConvertType.EXPORT.equals(convertType)) {
            json.addActionListener(e -> changeText());
            query.addActionListener(e -> changeText());
            changeText();
        }

        initLayout();
    }

    private void changeText() {
        if (json.isSelected()) {
            editorTextField.setText(DebugPowerJsonUtils.debugPowerJsonConvertJson(jsonEditor.getText()));
        } else if (query.isSelected()){
            editorTextField.setText(DebugPowerJsonUtils.debugPowerJsonConvertQuery(jsonEditor.getText()));
        }
    }

    private void initLayout() {
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(json);
        buttonGroup.add(query);

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        panel.add(json);
        panel.add(query);

        JPanel jPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent(
                        new JBLabel("Data type:"),
                        panel
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
        gbc.gridwidth = 0;
        gbc.gridheight = -1;
        this.add(editorTextField, gbc);
    }
}
