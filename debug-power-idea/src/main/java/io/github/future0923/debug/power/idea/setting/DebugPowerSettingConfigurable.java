package io.github.future0923.debug.power.idea.setting;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import io.github.future0923.debug.power.base.constants.ProjectConstants;
import io.github.future0923.debug.power.common.enums.PrintResultType;
import io.github.future0923.debug.power.idea.ui.setting.SettingPanel;
import io.github.future0923.debug.power.idea.utils.DebugPowerNotifierUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author future0923
 */
public class DebugPowerSettingConfigurable implements Configurable {

    private Project project;

    private SettingPanel settingPanel;

    public DebugPowerSettingConfigurable(Project project) {
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
        DebugPowerSettingState settingState = DebugPowerSettingState.getInstance(project);
        if (PrintResultType.NO_PRINT.equals(settingState.getPrintResultType()) && !settingPanel.getAttachApplicationNoPrintResult().isSelected()) {
            return true;
        }
        if (PrintResultType.TOSTRING.equals(settingState.getPrintResultType()) && !settingPanel.getAttachApplicationPrintToStringResult().isSelected()) {
            return true;
        }
        if (PrintResultType.JSON.equals(settingState.getPrintResultType()) && !settingPanel.getAttachApplicationPrintJsonResult().isSelected()) {
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
        if (settingState.getPrintSql() && settingPanel.getPrintSqlNo().isSelected()) {
            return true;
        }
        if (!settingState.getPrintSql() && settingPanel.getPrintSqlYes().isSelected()) {
            return true;
        }
        return false;
    }

    @Override
    public void reset() {
        DebugPowerSettingState settingState = DebugPowerSettingState.getInstance(project);
        if (PrintResultType.NO_PRINT.equals(settingState.getPrintResultType())) {
            settingPanel.getAttachApplicationNoPrintResult().setSelected(true);
        }
        if (PrintResultType.TOSTRING.equals(settingState.getPrintResultType())) {
            settingPanel.getAttachApplicationPrintToStringResult().setSelected(true);
        }
        if (PrintResultType.JSON.equals(settingState.getPrintResultType())) {
            settingPanel.getAttachApplicationPrintJsonResult().setSelected(true);
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
        if (settingState.getPrintSql()) {
            settingPanel.getPrintSqlYes().setSelected(true);
        } else {
            settingPanel.getPrintSqlNo().setSelected(true);
        }
    }

    @Override
    public void apply() throws ConfigurationException {
        DebugPowerSettingState settingState = DebugPowerSettingState.getInstance(project);
        if (settingPanel.getAttachApplicationNoPrintResult().isSelected()) {
            settingState.setPrintResultType(PrintResultType.NO_PRINT);
        }
        if (settingPanel.getAttachApplicationPrintToStringResult().isSelected()) {
            settingState.setPrintResultType(PrintResultType.TOSTRING);
        }
        if (settingPanel.getAttachApplicationPrintJsonResult().isSelected()) {
            settingState.setPrintResultType(PrintResultType.JSON);
        }
        if (settingPanel.getDefaultGenParamTypeSimple().isSelected()) {
            settingState.setDefaultGenParamType(GenParamType.SIMPLE);
        }
        if (settingPanel.getDefaultGenParamTypeCurrent().isSelected()) {
            settingState.setDefaultGenParamType(GenParamType.CURRENT);
        }
        if (settingPanel.getDefaultGenParamTypeAll().isSelected()) {
            settingState.setDefaultGenParamType(GenParamType.ALL);
        }
        if (settingPanel.getPrintSqlYes().isSelected()) {
            if (!settingState.getPrintSql()) {
                DebugPowerNotifierUtil.notifyInfo(project, "To start printing SQL statements, you need to restart App Service.");
            }
            settingState.setPrintSql(true);
        }
        if (settingPanel.getPrintSqlNo().isSelected()) {
            settingState.setPrintSql(false);
        }
    }

    @Override
    public void disposeUIResources() {
        project = null;
        settingPanel = null;
    }
}
