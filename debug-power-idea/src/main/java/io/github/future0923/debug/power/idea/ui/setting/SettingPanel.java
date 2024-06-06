package io.github.future0923.debug.power.idea.ui.setting;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBRadioButton;
import com.intellij.util.ui.FormBuilder;
import io.github.future0923.debug.power.common.enums.PrintResultType;
import io.github.future0923.debug.power.idea.setting.DebugPowerSettingState;
import io.github.future0923.debug.power.idea.setting.GenParamType;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;

/**
 * @author future0923
 */
public class SettingPanel {

    private final DebugPowerSettingState settingState;
    @Getter
    private JPanel settingPanel;
    @Getter
    private final JBRadioButton runApplicationYes = new JBRadioButton("Yes");
    @Getter
    private final JBRadioButton runApplicationNo = new JBRadioButton("No");

    @Getter
    private final JBRadioButton attachApplicationNoPrintResult = new JBRadioButton(PrintResultType.NO_PRINT.getType());
    @Getter
    private final JBRadioButton attachApplicationPrintToStringResult = new JBRadioButton(PrintResultType.TOSTRING.getType());
    @Getter
    private final JBRadioButton attachApplicationPrintJsonResult = new JBRadioButton(PrintResultType.JSON.getType());

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
        this.settingState = DebugPowerSettingState.getInstance(project);
        initLayout();
    }

    private void initLayout() {
        JPanel runApplicationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        runApplicationPanel.add(runApplicationYes);
        runApplicationPanel.add(runApplicationNo);
        ButtonGroup runApplicationButtonGroup = new ButtonGroup();
        runApplicationButtonGroup.add(runApplicationYes);
        runApplicationButtonGroup.add(runApplicationNo);
        if (settingState.getRunApplicationAttach()) {
            runApplicationYes.setSelected(true);
        } else {
            runApplicationNo.setSelected(true);
        }

        JPanel attachApplicationResult = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        attachApplicationResult.add(attachApplicationNoPrintResult);
        attachApplicationResult.add(attachApplicationPrintToStringResult);
        attachApplicationResult.add(attachApplicationPrintJsonResult);
        ButtonGroup attachApplicationResultButtonGroup = new ButtonGroup();
        attachApplicationResultButtonGroup.add(attachApplicationNoPrintResult);
        attachApplicationResultButtonGroup.add(attachApplicationPrintToStringResult);
        attachApplicationResultButtonGroup.add(attachApplicationPrintJsonResult);
        if (PrintResultType.NO_PRINT.equals(settingState.getPrintResultType())) {
            attachApplicationNoPrintResult.setSelected(true);
        } else if (PrintResultType.TOSTRING.equals(settingState.getPrintResultType())) {
            attachApplicationPrintToStringResult.setSelected(true);
        } else if (PrintResultType.JSON.equals(settingState.getPrintResultType())) {
            attachApplicationPrintJsonResult.setSelected(true);
        }

        JPanel defaultGenParamType = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        defaultGenParamType.add(defaultGenParamTypeSimple);
        defaultGenParamType.add(defaultGenParamTypeCurrent);
        defaultGenParamType.add(defaultGenParamTypeAll);
        ButtonGroup defaultGenParamTypeButtonGroup = new ButtonGroup();
        defaultGenParamTypeButtonGroup.add(defaultGenParamTypeSimple);
        defaultGenParamTypeButtonGroup.add(defaultGenParamTypeCurrent);
        defaultGenParamTypeButtonGroup.add(defaultGenParamTypeAll);
        if (GenParamType.SIMPLE.equals(settingState.getDefaultGenParamType())) {
            attachApplicationNoPrintResult.setSelected(true);
        } else if (GenParamType.CURRENT.equals(settingState.getDefaultGenParamType())) {
            attachApplicationPrintToStringResult.setSelected(true);
        } else if (GenParamType.ALL.equals(settingState.getDefaultGenParamType())) {
            attachApplicationPrintJsonResult.setSelected(true);
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
                        new JBLabel("Does it automatically attach when the program starts:"),
                        runApplicationPanel
                )
                .addLabeledComponent(
                        new JBLabel("Target program execution result:"),
                        attachApplicationResult
                )
                .addLabeledComponent(
                        new JBLabel("Entity class default param:"),
                        defaultGenParamType
                )
                .addLabeledComponent(
                        new JBLabel("Print pretty mysql:"),
                        printSqlPanel
                )
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

}
