package io.github.future0923.debug.tools.idea.ui.convert;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiParameterList;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBRadioButton;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBDimension;
import io.github.future0923.debug.tools.common.utils.DebugToolsJsonUtils;
import io.github.future0923.debug.tools.idea.ui.main.MainJsonEditor;
import io.github.future0923.debug.tools.idea.ui.tool.ToolBar;
import io.github.future0923.debug.tools.idea.utils.DebugToolsIcons;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;

/**
 * @author future0923
 */
public class ConvertPanel extends JBPanel<ConvertPanel> {

    private final Project project;


    @Getter
    private final JBRadioButton json = new JBRadioButton(ConvertDataType.Json.name(), true);

    @Getter
    private final JBRadioButton query = new JBRadioButton(ConvertDataType.Query.name());

    @Getter
    private final JBRadioButton path = new JBRadioButton(ConvertDataType.Path.name());

    private final ToolBar toolBar;

    @Getter
    private final MainJsonEditor jsonEditor;

    @Getter
    private final EditorTextField editorTextField;

    public ConvertPanel(Project project, MainJsonEditor jsonEditor, ConvertType convertType) {
        super(new GridBagLayout());
        setPreferredSize(new JBDimension(670, 500));
        this.project = project;
        this.jsonEditor = jsonEditor;
        editorTextField = new EditorTextField();
        editorTextField.addSettingsProvider(editor -> {
            editor.setVerticalScrollbarVisible(true);
            editor.setHorizontalScrollbarVisible(true);
        });
        toolBar = new ToolBar();
        toolBar.genButton("Pretty Json", DebugToolsIcons.Pretty, DebugToolsIcons.Pretty, actionEvent -> {
            if (json.isSelected()) {
                editorTextField.setText(DebugToolsJsonUtils.pretty(editorTextField.getText()));
            }
        });
        toolBar.genButton("Compress Json", DebugToolsIcons.Compress, DebugToolsIcons.Compress, actionEvent -> {
            if (json.isSelected()) {
                editorTextField.setText(DebugToolsJsonUtils.compress(editorTextField.getText()));
            }
        });
        json.addActionListener(e -> changeText(convertType));
        query.addActionListener(e -> changeText(convertType));
        path.addActionListener(e -> changeText(convertType));
        changeText(convertType);
        initLayout();
    }

    private void changeText(ConvertType convertType) {
        if (json.isSelected()) {
            if (ConvertType.EXPORT.equals(convertType)) {
                editorTextField.setText(DebugToolsJsonUtils.debugToolsJsonConvertJson(jsonEditor.getText()));
            }
        } else if (query.isSelected()) {
            if (ConvertType.EXPORT.equals(convertType)) {
                editorTextField.setText(DebugToolsJsonUtils.debugToolsJsonConvertQuery(jsonEditor.getText()));
            }
        } else if (path.isSelected()) {
            if (ConvertType.EXPORT.equals(convertType)) {
                PsiParameterList psiParameterList = jsonEditor.getPsiParameterList();
                if (psiParameterList != null) {
                    editorTextField.setText(DebugToolsJsonUtils.debugToolsJsonConvertPath(jsonEditor.getText()));
                }
            }
        }
    }

    private void initLayout() {
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(json);
        buttonGroup.add(query);
        buttonGroup.add(path);

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        panel.add(json);
        panel.add(query);
        panel.add(path);

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

        gbc.fill = GridBagConstraints.LINE_START;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(toolBar, gbc);

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
