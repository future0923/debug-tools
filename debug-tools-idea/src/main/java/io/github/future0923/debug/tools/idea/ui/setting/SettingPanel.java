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
import com.intellij.ui.JBIntSpinner;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBRadioButton;
import com.intellij.ui.components.JBTextArea;
import com.intellij.util.ui.FormBuilder;
import io.github.future0923.debug.tools.base.enums.PrintSqlType;
import io.github.future0923.debug.tools.base.hutool.core.util.BooleanUtil;
import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.idea.bundle.DebugToolsBundle;
import io.github.future0923.debug.tools.idea.setting.DebugToolsGlobalSettingState;
import io.github.future0923.debug.tools.idea.setting.DebugToolsSettingState;
import io.github.future0923.debug.tools.idea.setting.GenParamType;
import io.github.future0923.debug.tools.idea.setting.LanguageSetting;
import io.github.future0923.debug.tools.idea.ui.combobox.IgnoreStaticFieldComboBox;
import io.github.future0923.debug.tools.idea.ui.main.IgnoreSqlConfDialogWrapper;
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
    private final JBRadioButton defaultGenParamTypeSimple = new JBRadioButton(DebugToolsBundle.message(GenParamType.SIMPLE.getBundleKey()));
    @Getter
    private final JBRadioButton defaultGenParamTypeCurrent = new JBRadioButton(DebugToolsBundle.message(GenParamType.CURRENT.getBundleKey()));
    @Getter
    private final JBRadioButton defaultGenParamTypeAll = new JBRadioButton(DebugToolsBundle.message(GenParamType.ALL.getBundleKey()));

    @Getter
    private final JBRadioButton printPrettySql = new JBRadioButton(DebugToolsBundle.message(PrintSqlType.PRETTY.getBundleKey()));
    @Getter
    private final JBRadioButton printCompressSql = new JBRadioButton(DebugToolsBundle.message(PrintSqlType.COMPRESS.getBundleKey()));
    @Getter
    private final JBRadioButton printNoSql = new JBRadioButton(DebugToolsBundle.message(PrintSqlType.NO.getBundleKey()));

    @Getter
    private final JBRadioButton autoAttachYes = new JBRadioButton(DebugToolsBundle.message("common.yes"));
    @Getter
    private final JBRadioButton autoAttachNo = new JBRadioButton(DebugToolsBundle.message("common.no"));

    @Getter
    private final JBRadioButton showLineMarker = new JBRadioButton(DebugToolsBundle.message("common.yes"));
    @Getter
    private final JBRadioButton hideLineMarker = new JBRadioButton(DebugToolsBundle.message("common.no"));

    @Getter
    private final JBRadioButton searchLib = new JBRadioButton(DebugToolsBundle.message("common.yes"));
    @Getter
    private final JBRadioButton noSearchLib = new JBRadioButton(DebugToolsBundle.message("common.no"));

    @Getter
    private final JBRadioButton invokeMethodRecordYes = new JBRadioButton(DebugToolsBundle.message("common.yes"));
    @Getter
    private final JBRadioButton invokeMethodRecordNo = new JBRadioButton(DebugToolsBundle.message("common.no"));

    @Getter
    private final JBTextArea removeContextPath = new JBTextArea();

    @Getter
    private final JBCheckBox saveSqlCheckBox = new JBCheckBox(DebugToolsBundle.message("setting.panel.auto.save.sql"));
    @Getter
    private final JBIntSpinner saveSqlDaysField = new JBIntSpinner(1, 1, Integer.MAX_VALUE);

    @Getter
    private final TraceMethodPanel traceMethodPanel = new TraceMethodPanel();

    @Getter
    private final JBRadioButton languageIde = new JBRadioButton(LanguageSetting.IDE.getDisplayName());

    @Getter
    private final JBRadioButton languageEnglish = new JBRadioButton(LanguageSetting.ENGLISH.getDisplayName());

    @Getter
    private final JBRadioButton languageChinese = new JBRadioButton(LanguageSetting.CHINESE.getDisplayName());

    @Getter
    private final JBRadioButton logLevelError = new JBRadioButton("Error");
    @Getter
    private final JBRadioButton logLevelReload = new JBRadioButton("Reload");
    @Getter
    private final JBRadioButton logLevelWarning = new JBRadioButton("Warning");
    @Getter
    private final JBRadioButton logLevelInfo = new JBRadioButton("Info");
    @Getter
    private final JBRadioButton logLevelDebug = new JBRadioButton("Debug");
    @Getter
    private final JBRadioButton logLevelTrace = new JBRadioButton("Trace");

    @Getter
    private IgnoreStaticFieldComboBox ignoreStaticFieldComboBox;

    @Getter
    private final JButton filterSqlConfigButton = new JButton(DebugToolsBundle.message("action.filter.sql.config"));

    public SettingPanel(Project project) {
        this.project = project;
        this.settingState = DebugToolsSettingState.getInstance(project);
        initLayout();
    }

    /**
     * Refresh the language display text
     */
    public void refreshLanguageDisplay() {
        languageIde.setText(LanguageSetting.IDE.getDisplayName());
        languageEnglish.setText(LanguageSetting.ENGLISH.getDisplayName());
        languageChinese.setText(LanguageSetting.CHINESE.getDisplayName());
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
        printSqlPanel.add(saveSqlCheckBox);
        printSqlPanel.add(filterSqlConfigButton);

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

        JPanel sqlRetentionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        sqlRetentionPanel.add(new JLabel(DebugToolsBundle.message("setting.panel.sql.retention.days")));
        sqlRetentionPanel.add(saveSqlDaysField);
        sqlRetentionPanel.add(new JLabel(DebugToolsBundle.message("setting.panel.minimum.settable.value")));

        saveSqlCheckBox.setVisible(!printNoSql.isSelected());
        filterSqlConfigButton.setVisible(!printNoSql.isSelected());
        saveSqlCheckBox.setSelected(BooleanUtil.isTrue(settingState.getAutoSaveSql()));
        if (settingState.getSqlRetentionDays() != null) {
            saveSqlDaysField.setNumber(settingState.getSqlRetentionDays());
        }
        sqlRetentionPanel.setVisible(saveSqlCheckBox.isSelected());
        // 监听开关变化
        saveSqlCheckBox.addItemListener(e -> sqlRetentionPanel.setVisible(saveSqlCheckBox.isSelected()));
        filterSqlConfigButton.addActionListener(e -> {
            IgnoreSqlConfDialogWrapper wrapper = new IgnoreSqlConfDialogWrapper(project);
            wrapper.show();
        });
        Runnable updateSaveSqlPanels = () -> {
            if (printNoSql.isSelected()) {
                saveSqlCheckBox.setVisible(false);
                filterSqlConfigButton.setVisible(false);
                saveSqlCheckBox.setSelected(false);
            } else {
                saveSqlCheckBox.setVisible(true);
                filterSqlConfigButton.setVisible(true);
            }
        };
        // 监听printSql单选按钮变化
        printPrettySql.addItemListener(e -> updateSaveSqlPanels.run());
        printCompressSql.addItemListener(e -> updateSaveSqlPanels.run());
        printNoSql.addItemListener(e -> updateSaveSqlPanels.run());

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

        // 初始化语言设置单选按钮
        JPanel languagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        languagePanel.add(languageIde);
        languagePanel.add(languageEnglish);
        languagePanel.add(languageChinese);
        ButtonGroup languageButtonGroup = new ButtonGroup();
        languageButtonGroup.add(languageIde);
        languageButtonGroup.add(languageEnglish);
        languageButtonGroup.add(languageChinese);
        LanguageSetting languageSetting = DebugToolsGlobalSettingState.getInstance().getLanguage();
        switch (languageSetting) {
            case IDE:
                languageIde.setSelected(true);
                break;
            case ENGLISH:
                languageEnglish.setSelected(true);
                break;
            case CHINESE:
                languageChinese.setSelected(true);
                break;
            default:
                languageIde.setSelected(true);
                break;
        }

        JPanel lineMarkerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        lineMarkerPanel.add(showLineMarker);
        lineMarkerPanel.add(hideLineMarker);
        ButtonGroup LineMarkerButtonGroup = new ButtonGroup();
        LineMarkerButtonGroup.add(showLineMarker);
        LineMarkerButtonGroup.add(hideLineMarker);
        if (BooleanUtil.isTrue(settingState.getLineMarkerVisible())) {
            showLineMarker.setSelected(true);
        } else {
            hideLineMarker.setSelected(true);
        }

        JPanel searchLibPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        searchLibPanel.add(searchLib);
        searchLibPanel.add(noSearchLib);
        ButtonGroup searchLibButtonGroup = new ButtonGroup();
        searchLibButtonGroup.add(searchLib);
        searchLibButtonGroup.add(noSearchLib);
        if (BooleanUtil.isTrue(settingState.getSearchLibrary())) {
            searchLib.setSelected(true);
        } else {
            noSearchLib.setSelected(true);
        }

        JPanel invokeMethodRecordPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        invokeMethodRecordPanel.add(invokeMethodRecordYes);
        invokeMethodRecordPanel.add(invokeMethodRecordNo);
        ButtonGroup invokeMethodRecordButtonGroup = new ButtonGroup();
        invokeMethodRecordButtonGroup.add(invokeMethodRecordYes);
        invokeMethodRecordButtonGroup.add(invokeMethodRecordNo);
        if (BooleanUtil.isTrue(settingState.getInvokeMethodRecord())) {
            invokeMethodRecordYes.setSelected(true);
        } else {
            invokeMethodRecordNo.setSelected(true);
        }

        JBRadioButton[] logLevelButtons = {
                logLevelTrace,
                logLevelDebug,
                logLevelInfo,
                logLevelWarning,
                logLevelReload,
                logLevelError,
        };

        JPanel logLevel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        ButtonGroup logLevelButtonGroup = new ButtonGroup();

        for (JBRadioButton logLevelButton : logLevelButtons) {
            logLevel.add(logLevelButton);
            logLevelButtonGroup.add(logLevelButton);
        }

        Logger.Level[] levels = Logger.Level.values();
        for (int i = 0; i < levels.length; i++) {
            if (levels[i] == settingState.getLogLevel()) {
                logLevelButtons[i].setSelected(true);
                break;
            }
        }

        if (Logger.Level.ERROR.equals(settingState.getLogLevel())) {
            logLevelError.setSelected(true);
        } else if (GenParamType.CURRENT.equals(settingState.getDefaultGenParamType())) {
            defaultGenParamTypeCurrent.setSelected(true);
        } else if (GenParamType.ALL.equals(settingState.getDefaultGenParamType())) {
            defaultGenParamTypeAll.setSelected(true);
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

        JPanel ignoreStaticFieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        ignoreStaticFieldComboBox = new IgnoreStaticFieldComboBox(project, 220);
        ignoreStaticFieldPanel.add(ignoreStaticFieldComboBox);
        ignoreStaticFieldPanel.add(ignoreStaticFieldComboBox.getPanel());

        settingPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent(
                        new JBLabel(DebugToolsBundle.message("setting.panel.language")),
                        languagePanel
                )
                .addLabeledComponent(
                        new JBLabel(DebugToolsBundle.message("setting.panel.log.level")),
                        logLevel
                )
                .addLabeledComponent(
                        new JBLabel(DebugToolsBundle.message("setting.panel.entity.class.default.param")),
                        defaultGenParamType
                )
                .addLabeledComponent(
                        new JBLabel(DebugToolsBundle.message("setting.panel.invoke.method.record")),
                        invokeMethodRecordPanel
                )
                .addLabeledComponent(
                        new JBLabel(DebugToolsBundle.message("setting.panel.quick.action.line.marker")),
                        lineMarkerPanel
                )
                .addLabeledComponent(
                        new JBLabel(DebugToolsBundle.message("setting.panel.search.library")),
                        searchLibPanel
                )
                .addLabeledComponent(
                        new JBLabel(DebugToolsBundle.message("setting.panel.print.sql")),
                        printSqlPanel
                )
                .addLabeledComponent(
                        new JBLabel(""),
                        sqlRetentionPanel
                )
                .addLabeledComponent(
                        new JBLabel(DebugToolsBundle.message("setting.panel.auto.attach.start.application")),
                        autoAttachPanel
                )
                .addLabeledComponent(
                        new JBLabel(DebugToolsBundle.message("setting.panel.remove.context.path")),
                        removeContextPath
                )
                .addLabeledComponent(
                        new JBLabel(DebugToolsBundle.message("setting.panel.trace.method")),
                        traceMethodPanel.getComponent()
                )
                .addLabeledComponent(
                        new JBLabel(DebugToolsBundle.message("setting.panel.ignore.static.field")),
                        ignoreStaticFieldPanel
                )
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

}
