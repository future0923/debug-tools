package io.github.future0923.debug.tools.idea.ui.setting;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBRadioButton;
import com.intellij.util.ui.FormBuilder;
import io.github.future0923.debug.tools.idea.setting.DebugToolsSettingState;
import io.github.future0923.debug.tools.idea.setting.GenParamType;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;

/**
 * @author future0923
 */
public class SettingPanel {

    private final DebugToolsSettingState settingState;
    @Getter
    private JPanel settingPanel;

    @Getter
    private final JBRadioButton defaultGenParamTypeSimple = new JBRadioButton(GenParamType.SIMPLE.getType());
    @Getter
    private final JBRadioButton defaultGenParamTypeCurrent = new JBRadioButton(GenParamType.CURRENT.getType());
    @Getter
    private final JBRadioButton defaultGenParamTypeAll = new JBRadioButton(GenParamType.ALL.getType());

    @Getter
    private final JBRadioButton printSqlYes = new JBRadioButton("Yes");
    @Getter
    private final JBRadioButton printSqlNo = new JBRadioButton("No");

    public SettingPanel(Project project) {
        this.settingState = DebugToolsSettingState.getInstance(project);
        initLayout();
    }

    private void initLayout() {
        JPanel defaultGenParamType = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        defaultGenParamType.add(defaultGenParamTypeSimple);
        defaultGenParamType.add(defaultGenParamTypeCurrent);
        defaultGenParamType.add(defaultGenParamTypeAll);
        ButtonGroup defaultGenParamTypeButtonGroup = new ButtonGroup();
        defaultGenParamTypeButtonGroup.add(defaultGenParamTypeSimple);
        defaultGenParamTypeButtonGroup.add(defaultGenParamTypeCurrent);
        defaultGenParamTypeButtonGroup.add(defaultGenParamTypeAll);
        if (GenParamType.SIMPLE.equals(settingState.getDefaultGenParamType())) {
            defaultGenParamTypeSimple.setSelected(true);
        } else if (GenParamType.CURRENT.equals(settingState.getDefaultGenParamType())) {
            defaultGenParamTypeCurrent.setSelected(true);
        } else if (GenParamType.ALL.equals(settingState.getDefaultGenParamType())) {
            defaultGenParamTypeAll.setSelected(true);
        }

        JPanel printSqlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        printSqlPanel.add(printSqlYes);
        printSqlPanel.add(printSqlNo);
        ButtonGroup printSqlButtonGroup = new ButtonGroup();
        printSqlButtonGroup.add(printSqlYes);
        printSqlButtonGroup.add(printSqlNo);
        if (settingState.getPrintSql()) {
            printSqlYes.setSelected(true);
        } else {
            printSqlNo.setSelected(true);
        }

        settingPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent(
                        new JBLabel("Entity class default param:"),
                        defaultGenParamType
                )
                .addLabeledComponent(
                        new JBLabel("Print pretty sql:"),
                        printSqlPanel
                )
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

}
