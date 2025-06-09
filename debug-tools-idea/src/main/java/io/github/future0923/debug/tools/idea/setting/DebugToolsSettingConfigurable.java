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
package io.github.future0923.debug.tools.idea.setting;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import io.github.future0923.debug.tools.base.constants.ProjectConstants;
import io.github.future0923.debug.tools.base.enums.PrintSqlType;
import io.github.future0923.debug.tools.idea.ui.setting.SettingPanel;
import io.github.future0923.debug.tools.idea.utils.DebugToolsNotifierUtil;
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
        return false;
    }

    @Override
    public void reset() {
        DebugToolsSettingState settingState = DebugToolsSettingState.getInstance(project);
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
    }

    @Override
    public void apply() throws ConfigurationException {
        DebugToolsSettingState settingState = DebugToolsSettingState.getInstance(project);
        if (settingPanel.getDefaultGenParamTypeSimple().isSelected()) {
            settingState.setDefaultGenParamType(GenParamType.SIMPLE);
        }
        if (settingPanel.getDefaultGenParamTypeCurrent().isSelected()) {
            settingState.setDefaultGenParamType(GenParamType.CURRENT);
        }
        if (settingPanel.getDefaultGenParamTypeAll().isSelected()) {
            settingState.setDefaultGenParamType(GenParamType.ALL);
        }


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
    }

    @Override
    public void disposeUIResources() {
        project = null;
        settingPanel = null;
    }
}
