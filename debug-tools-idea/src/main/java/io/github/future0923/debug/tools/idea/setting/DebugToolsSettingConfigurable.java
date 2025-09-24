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
package io.github.future0923.debug.tools.idea.setting;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import io.github.future0923.debug.tools.base.constants.ProjectConstants;
import io.github.future0923.debug.tools.base.enums.PrintSqlType;
import io.github.future0923.debug.tools.base.hutool.core.util.BooleanUtil;
import io.github.future0923.debug.tools.base.hutool.core.util.ObjectUtil;
import io.github.future0923.debug.tools.base.hutool.core.util.StrUtil;
import io.github.future0923.debug.tools.common.dto.TraceMethodDTO;
import io.github.future0923.debug.tools.idea.tool.DebugToolsToolWindow;
import io.github.future0923.debug.tools.idea.tool.DebugToolsToolWindowFactory;
import io.github.future0923.debug.tools.idea.ui.setting.SettingPanel;
import io.github.future0923.debug.tools.idea.utils.DebugToolsNotifierUtil;
import io.github.future0923.debug.tools.idea.utils.LanguageUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Objects;

/**
 * @author future0923
 */
public class DebugToolsSettingConfigurable implements Configurable {

    private Project project;

    private SettingPanel settingPanel;

    public DebugToolsSettingConfigurable(Project project) {
        this.project = project;
    }

    @Override
    @Nls(capitalization = Nls.Capitalization.Title)
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return ProjectConstants.NAME;
    }

    @Override
    public @Nullable JComponent createComponent() {
        settingPanel = new SettingPanel(project);
        return settingPanel.getSettingPanel();
    }

    /**
     * 在保存配置之前，系统可以调用 isModified 方法来检查配置是否需要保存。如果配置没有修改，就可以避免不必要的保存操作，从而提高性能。
     * 在用户界面（UI）中，当用户修改配置但还未保存时，可以利用 isModified 方法提示用户“是否需要保存更改”。
     */
    @Override
    public boolean isModified() {
        DebugToolsSettingState settingState = DebugToolsSettingState.getInstance(project);
        DebugToolsGlobalSettingState globalSettingState = DebugToolsGlobalSettingState.getInstance();
        // 检查语言设置是否修改
        LanguageSetting currentLanguage = globalSettingState.getLanguage();
        if (currentLanguage != LanguageSetting.IDE && !settingPanel.getLanguageIde().isSelected()) {
            return true;
        }
        if (currentLanguage != LanguageSetting.ENGLISH && !settingPanel.getLanguageEnglish().isSelected()) {
            return true;
        }
        if (currentLanguage != LanguageSetting.CHINESE && !settingPanel.getLanguageChinese().isSelected()) {
            return true;
        }
        if (GenParamType.SIMPLE.equals(settingState.getDefaultGenParamType()) && !settingPanel.getDefaultGenParamTypeSimple().isSelected()) {
            return true;
        }
        if (GenParamType.CURRENT.equals(settingState.getDefaultGenParamType()) && !settingPanel.getDefaultGenParamTypeCurrent().isSelected()) {
            return true;
        }
        if (GenParamType.ALL.equals(settingState.getDefaultGenParamType()) && !settingPanel.getDefaultGenParamTypeAll().isSelected()) {
            return true;
        }
        if ((PrintSqlType.PRETTY.equals(settingState.getPrintSql()) || PrintSqlType.YES.equals(settingState.getPrintSql())) && !settingPanel.getPrintPrettySql().isSelected()) {
            return true;
        }
        if (PrintSqlType.COMPRESS.equals(settingState.getPrintSql()) && !settingPanel.getPrintCompressSql().isSelected()) {
            return true;
        }
        if (PrintSqlType.NO.equals(settingState.getPrintSql()) && !settingPanel.getPrintNoSql().isSelected()) {
            return true;
        }
        if (settingState.getAutoAttach() && settingPanel.getAutoAttachNo().isSelected()) {
            return true;
        }
        if (!settingState.getAutoAttach() && settingPanel.getAutoAttachYes().isSelected()) {
            return true;
        }
        if (!Objects.equals(settingState.getRemoveContextPath(), settingPanel.getRemoveContextPath().getText())) {
            return true;
        }
        // 新增 saveSql 配置项判断
        if (!Objects.equals(settingState.getAutoSaveSql(), settingPanel.getSaveSqlCheckBox().isSelected())) {
            return true;
        }
        // 新增 saveSqlDays 配置项判断（只有开启时才判断）
        if (settingPanel.getSaveSqlCheckBox().isSelected()) {
            if (!Objects.equals(settingState.getSqlRetentionDays(), settingPanel.getSaveSqlDaysField().getNumber())) {
                return true;
            }
        }
        if (settingState.getTraceMethodDTO() == null) {
            return true;
        }
        if (!Objects.equals(settingState.getTraceMethodDTO().getTraceMethod(), settingPanel.getTraceMethodPanel().isTraceMethod())) {
            return true;
        }
        if (!Objects.equals(settingState.getTraceMethodDTO().getTraceMaxDepth(), settingPanel.getTraceMethodPanel().getMaxDepth())) {
            return true;
        }
        if (!Objects.equals(settingState.getTraceMethodDTO().getTraceMyBatis(), settingPanel.getTraceMethodPanel().isTraceMyBatis())) {
            return true;
        }
        if (!Objects.equals(settingState.getTraceMethodDTO().getTraceSQL(), settingPanel.getTraceMethodPanel().isTraceSql())) {
            return true;
        }
        if (!Objects.equals(settingState.getTraceMethodDTO().getTraceSkipStartGetSetCheckBox(), settingPanel.getTraceMethodPanel().isTraceSkipStartGetSetCheckBox())) {
            return true;
        }
        if (!StrUtil.equals(settingState.getTraceMethodDTO().getTraceBusinessPackageRegexp(), settingPanel.getTraceMethodPanel().getTraceBusinessPackage())) {
            return true;
        }
        if (!StrUtil.equals(settingState.getTraceMethodDTO().getTraceIgnorePackageRegexp(), settingPanel.getTraceMethodPanel().getTraceIgnorePackage())) {
            return true;
        }

        if (BooleanUtil.isTrue(settingState.getLineMarkerVisible()) && settingPanel.getHideLineMarker().isSelected()) {
            return true;
        }
        if (BooleanUtil.isFalse(settingState.getLineMarkerVisible()) && settingPanel.getShowLineMarker().isSelected()) {
            return true;
        }

        return false;
    }

    @Override
    public void reset() {
        DebugToolsSettingState settingState = DebugToolsSettingState.getInstance(project);
        DebugToolsGlobalSettingState globalSettingState = DebugToolsGlobalSettingState.getInstance();
        // 重置语言设置
        LanguageSetting languageSetting = globalSettingState.getLanguage();
        switch (languageSetting) {
            case IDE:
                settingPanel.getLanguageIde().setSelected(true);
                break;
            case ENGLISH:
                settingPanel.getLanguageEnglish().setSelected(true);
                break;
            case CHINESE:
                settingPanel.getLanguageChinese().setSelected(true);
                break;
            default:
                settingPanel.getLanguageIde().setSelected(true);
                break;
        }
        
        if (GenParamType.SIMPLE.equals(settingState.getDefaultGenParamType())) {
            settingPanel.getDefaultGenParamTypeSimple().setSelected(true);
        }
        if (GenParamType.CURRENT.equals(settingState.getDefaultGenParamType())) {
            settingPanel.getDefaultGenParamTypeCurrent().setSelected(true);
        }
        if (GenParamType.ALL.equals(settingState.getDefaultGenParamType())) {
            settingPanel.getDefaultGenParamTypeAll().setSelected(true);
        }

        if (PrintSqlType.PRETTY.equals(settingState.getPrintSql()) || PrintSqlType.YES.equals(settingState.getPrintSql())) {
            settingPanel.getPrintPrettySql().setSelected(true);
        }
        if (PrintSqlType.COMPRESS.equals(settingState.getPrintSql())) {
            settingPanel.getPrintCompressSql().setSelected(true);
        }
        if (PrintSqlType.NO.equals(settingState.getPrintSql())) {
            settingPanel.getPrintNoSql().setSelected(true);
        }

        if (settingState.getAutoAttach()) {
            settingPanel.getAutoAttachYes().setSelected(true);
        } else {
            settingPanel.getAutoAttachNo().setSelected(true);
        }
        settingPanel.getRemoveContextPath().setText(settingState.getRemoveContextPath());

        settingPanel.getSaveSqlCheckBox().setSelected(BooleanUtil.isTrue(settingState.getAutoSaveSql()));
        settingPanel.getSaveSqlDaysField().setNumber(settingState.getSqlRetentionDays());

        TraceMethodDTO traceMethodDTO = ObjectUtil.defaultIfNull(settingState.getTraceMethodDTO(), new TraceMethodDTO());
        settingPanel.getTraceMethodPanel().setTraceMethod(traceMethodDTO.getTraceMethod());
        settingPanel.getTraceMethodPanel().setMaxDepth(traceMethodDTO.getTraceMaxDepth());
        settingPanel.getTraceMethodPanel().setTraceMyBatis(traceMethodDTO.getTraceMyBatis());
        settingPanel.getTraceMethodPanel().setTraceSql(traceMethodDTO.getTraceSQL());
        settingPanel.getTraceMethodPanel().setTraceSkipStartGetSetCheckBox(traceMethodDTO.getTraceSkipStartGetSetCheckBox());
        settingPanel.getTraceMethodPanel().setTraceBusinessPackage(traceMethodDTO.getTraceBusinessPackageRegexp());
        settingPanel.getTraceMethodPanel().setTraceIgnorePackage(traceMethodDTO.getTraceIgnorePackageRegexp());
    }

    @Override
    public void apply() throws ConfigurationException {
        DebugToolsSettingState settingState = DebugToolsSettingState.getInstance(project);
        DebugToolsGlobalSettingState globalSettingState = DebugToolsGlobalSettingState.getInstance();
        LanguageSetting oldLanguage = globalSettingState.getLanguage();
        // 保存语言设置
        if (settingPanel.getLanguageIde().isSelected()) {
            globalSettingState.setLanguage(LanguageSetting.IDE);
        }
        if (settingPanel.getLanguageEnglish().isSelected()) {
            globalSettingState.setLanguage(LanguageSetting.ENGLISH);
        }
        if (settingPanel.getLanguageChinese().isSelected()) {
            globalSettingState.setLanguage(LanguageSetting.CHINESE);
        }
        
        LanguageSetting newLanguage = globalSettingState.getLanguage();
        boolean languageChanged = oldLanguage != newLanguage;
        
        if (settingPanel.getDefaultGenParamTypeSimple().isSelected()) {
            settingState.setDefaultGenParamType(GenParamType.SIMPLE);
        }
        if (settingPanel.getDefaultGenParamTypeCurrent().isSelected()) {
            settingState.setDefaultGenParamType(GenParamType.CURRENT);
        }
        if (settingPanel.getDefaultGenParamTypeAll().isSelected()) {
            settingState.setDefaultGenParamType(GenParamType.ALL);
        }

        settingState.setLineMarkerVisible(settingPanel.getShowLineMarker().isSelected());

        if (settingPanel.getPrintPrettySql().isSelected() && !PrintSqlType.PRETTY.equals(settingState.getPrintSql())) {
            settingState.setPrintSql(PrintSqlType.PRETTY);
            DebugToolsNotifierUtil.notifyInfo(project, "You've set it to pretty sql, you need to restart App Service.");
        }
        if (settingPanel.getPrintCompressSql().isSelected() && !PrintSqlType.COMPRESS.equals(settingState.getPrintSql())) {
            settingState.setPrintSql(PrintSqlType.COMPRESS);
            DebugToolsNotifierUtil.notifyInfo(project, "You've set it to compress sql, you need to restart App Service.");
        }
        if (settingPanel.getPrintNoSql().isSelected() && !PrintSqlType.NO.equals(settingState.getPrintSql())) {
            settingState.setPrintSql(PrintSqlType.NO);
            DebugToolsNotifierUtil.notifyInfo(project, "You've set it to no print sql, you need to restart App Service.");
        }

        if (settingPanel.getAutoAttachYes().isSelected()) {
            settingState.setAutoAttach(true);
        }
        if (settingPanel.getAutoAttachNo().isSelected()) {
            settingState.setAutoAttach(false);
        }
        settingState.setRemoveContextPath(settingPanel.getRemoveContextPath().getText());

        settingState.setAutoSaveSql(settingPanel.getSaveSqlCheckBox().isSelected());
        settingState.setSqlRetentionDays(Math.max(1,settingPanel.getSaveSqlDaysField().getNumber()));

        DebugToolsToolWindowFactory.consumerToolWindow(project, DebugToolsToolWindow::refreshToolBar);

        TraceMethodDTO traceMethodDTO = new TraceMethodDTO();
        traceMethodDTO.setTraceMethod(settingPanel.getTraceMethodPanel().isTraceMethod());
        traceMethodDTO.setTraceMaxDepth(settingPanel.getTraceMethodPanel().getMaxDepth());
        traceMethodDTO.setTraceMyBatis(settingPanel.getTraceMethodPanel().isTraceMyBatis());
        traceMethodDTO.setTraceSQL(settingPanel.getTraceMethodPanel().isTraceSql());
        traceMethodDTO.setTraceSkipStartGetSetCheckBox(settingPanel.getTraceMethodPanel().isTraceSkipStartGetSetCheckBox());
        traceMethodDTO.setTraceBusinessPackageRegexp(settingPanel.getTraceMethodPanel().getTraceBusinessPackage());
        traceMethodDTO.setTraceIgnorePackageRegexp(settingPanel.getTraceMethodPanel().getTraceIgnorePackage());
        settingState.setTraceMethodDTO(traceMethodDTO);
        
        // 如果语言设置发生了变化，刷新UI
        if (languageChanged) {
            // 刷新设置面板的语言显示
            settingPanel.refreshLanguageDisplay();
            LanguageUtils.refreshUI(project);
        }
    }

    @Override
    public void disposeUIResources() {
        project = null;
        settingPanel = null;
    }
}
