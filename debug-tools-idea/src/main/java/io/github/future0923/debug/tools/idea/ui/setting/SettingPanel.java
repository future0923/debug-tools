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
package io.github.future0923.debug.tools.idea.ui.setting;

import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBRadioButton;
import com.intellij.ui.components.JBTextArea;
import com.intellij.util.ui.FormBuilder;
import io.github.future0923.debug.tools.base.enums.PrintSqlType;
import io.github.future0923.debug.tools.idea.setting.DebugToolsSettingState;
import io.github.future0923.debug.tools.idea.setting.GenParamType;
import io.github.future0923.debug.tools.idea.ui.main.TraceMethodPanel;
import lombok.Getter;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * @author future0923
 */
public class SettingPanel {

    private final Project project;

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
    private final JBRadioButton printPrettySql = new JBRadioButton(PrintSqlType.PRETTY.getType());
    @Getter
    private final JBRadioButton printCompressSql = new JBRadioButton(PrintSqlType.COMPRESS.getType());
    @Getter
    private final JBRadioButton printNoSql = new JBRadioButton(PrintSqlType.NO.getType());

    @Getter
    private final JBRadioButton autoAttachYes = new JBRadioButton("Yes");
    @Getter
    private final JBRadioButton autoAttachNo = new JBRadioButton("No");

    @Getter
    private final JBTextArea removeContextPath = new JBTextArea();

    // 新增：保存SQL日志相关控件
    @Getter
    private final JCheckBox saveSqlCheckBox = new JCheckBox("Save sql");
    @Getter
    private final JTextField saveSqlDaysField = new JTextField(5);

    @Getter
    private final TraceMethodPanel traceMethodPanel = new TraceMethodPanel();

    public SettingPanel(Project project) {
        this.project = project;
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
        printSqlPanel.add(printPrettySql);
        printSqlPanel.add(printCompressSql);
        printSqlPanel.add(printNoSql);
        ButtonGroup printSqlButtonGroup = new ButtonGroup();
        printSqlButtonGroup.add(printPrettySql);
        printSqlButtonGroup.add(printCompressSql);
        printSqlButtonGroup.add(printNoSql);
        if (PrintSqlType.PRETTY.equals(settingState.getPrintSql()) || PrintSqlType.YES.equals(settingState.getPrintSql())) {
            printPrettySql.setSelected(true);
        } else if (PrintSqlType.COMPRESS.equals(settingState.getPrintSql())) {
            printCompressSql.setSelected(true);
        } else {
            printNoSql.setSelected(true);
        }

        // 新增：自动保存SQL日志配置面板（分为两行，均跟随 print sql 显示）
        JPanel autoSaveSqlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        autoSaveSqlPanel.add(saveSqlCheckBox);
        saveSqlCheckBox.setText("Auto save sql to file");

        JPanel sqlRetentionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        sqlRetentionPanel.add(new JLabel("SQL Retention Days:"));
        sqlRetentionPanel.add(saveSqlDaysField);
        sqlRetentionPanel.add(new JLabel("0 means only keep SQL for the current request."));

        // 初始化控件状态
        saveSqlCheckBox.setSelected(Boolean.TRUE.equals(settingState.getAutoSaveSql()));
        saveSqlDaysField.setText(String.valueOf(settingState.getSqlRetentionDays()));
        saveSqlDaysField.setEnabled(saveSqlCheckBox.isSelected());
        // 监听开关变化
        saveSqlCheckBox.addActionListener(e -> {
            saveSqlDaysField.setEnabled(saveSqlCheckBox.isSelected());
        });

        // 联动逻辑：printSql为NO时隐藏两个面板并禁用saveSqlCheckBox
        Runnable updateSaveSqlPanels = () -> {
            boolean show = !printNoSql.isSelected();
            autoSaveSqlPanel.setVisible(show);
            sqlRetentionPanel.setVisible(show);
            saveSqlCheckBox.setEnabled(show);
            saveSqlDaysField.setEnabled(show && saveSqlCheckBox.isSelected());
            if (!show) {
                saveSqlCheckBox.setSelected(false);
            }
        };
        // 监听printSql单选按钮变化
        printPrettySql.addActionListener(e -> updateSaveSqlPanels.run());
        printCompressSql.addActionListener(e -> updateSaveSqlPanels.run());
        printNoSql.addActionListener(e -> updateSaveSqlPanels.run());
        // 初始化时执行一次
        updateSaveSqlPanels.run();

        JPanel autoAttachPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        autoAttachPanel.add(autoAttachYes);
        autoAttachPanel.add(autoAttachNo);
        ButtonGroup autoAttachButtonGroup = new ButtonGroup();
        autoAttachButtonGroup.add(autoAttachYes);
        autoAttachButtonGroup.add(autoAttachNo);
        if (settingState.getAutoAttach()) {
            autoAttachYes.setSelected(true);
        } else {
            autoAttachNo.setSelected(true);
        }

        removeContextPath.setText(settingState.getRemoveContextPath());
        // 添加边框
        Border border = BorderFactory.createLineBorder(JBColor.GRAY); // 创建灰色线条边框
        removeContextPath.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(5, 5, 5, 5))); // 内外边框组合
        // 自动换行
        removeContextPath.setLineWrap(true);
        // 按单词边界换行
        removeContextPath.setWrapStyleWord(true);

        traceMethodPanel.processDefaultInfo(project);

        settingPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent(
                        new JBLabel("Entity class default param:"),
                        defaultGenParamType
                )
                .addLabeledComponent(
                        new JBLabel("Print sql:"),
                        printSqlPanel
                )
                .addLabeledComponent(
                        new JBLabel(""),
                        autoSaveSqlPanel
                )
                .addLabeledComponent(
                        new JBLabel(""),
                        sqlRetentionPanel
                )
                .addLabeledComponent(
                        new JBLabel("Auto attach start application:"),
                        autoAttachPanel
                )
                .addLabeledComponent(
                        new JBLabel("Remove context path:"),
                        removeContextPath
                )
                .addLabeledComponent(
                        new JBLabel("Trace method:"),
                        traceMethodPanel.getComponent()
                )
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

}
