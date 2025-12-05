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
package io.github.future0923.debug.tools.idea.ui.combobox;

import static io.github.future0923.debug.tools.idea.utils.DebugToolsIcons.Around.*;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;

import io.github.future0923.debug.tools.base.hutool.core.io.FileUtil;
import io.github.future0923.debug.tools.base.hutool.core.util.StrUtil;
import io.github.future0923.debug.tools.idea.action.ExecuteLastWithDefaultClassLoaderEditorPopupMenuAction;
import io.github.future0923.debug.tools.idea.bundle.DebugToolsBundle;
import io.github.future0923.debug.tools.idea.constant.IdeaPluginProjectConstants;
import io.github.future0923.debug.tools.idea.setting.DebugToolsSettingState;
import io.github.future0923.debug.tools.idea.ui.button.RoundedIconButton;
import io.github.future0923.debug.tools.idea.ui.main.JavaEditorDialogWrapper;
import io.github.future0923.debug.tools.idea.utils.DebugToolsNotifierUtil;
import lombok.Getter;

/**
 * @author future0923
 */
public class MethodAroundComboBox extends ComboBox<String> {

    private static final Logger logger = Logger.getInstance(ExecuteLastWithDefaultClassLoaderEditorPopupMenuAction.class);

    private final Project project;

    @Getter
    private final JPanel methodAroundPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));

    private final List<JButton> buttons = new ArrayList<>();

    private final JButton methodAroundReloadButton;

    private final JButton methodAroundDetailButton;

    private final JButton methodAroundDeleteButton;

    private final JButton methodAroundAddButton;

    public MethodAroundComboBox(Project project) {
        this(project, -1);
    }

    public MethodAroundComboBox(Project project, int width) {
        super(width);
        this.project = project;
        DebugToolsSettingState settingState = DebugToolsSettingState.getInstance(project);
        methodAroundReloadButton = new RoundedIconButton(Reload);
        methodAroundReloadButton.setToolTipText(DebugToolsBundle.message("action.reload"));
        methodAroundReloadButton.addActionListener(e -> {
            reload(project, settingState);
            DebugToolsNotifierUtil.notifyInfo(project, DebugToolsBundle.message("reload.successful"));
        });
        methodAroundDetailButton = new RoundedIconButton(View);
        methodAroundDetailButton.setDisabledIcon(ViewDisabled);
        methodAroundDetailButton.setDisabledSelectedIcon(DeleteDisabled);
        methodAroundDetailButton.setToolTipText(DebugToolsBundle.message("action.detail"));
        methodAroundDetailButton.addActionListener(e -> {
            JavaEditorDialogWrapper javaEditorDialogWrapper = new JavaEditorDialogWrapper(project, this, (String) getSelectedItem());
            javaEditorDialogWrapper.show();
        });
        methodAroundDeleteButton = new RoundedIconButton(Delete);
        methodAroundDeleteButton.setDisabledIcon(DeleteDisabled);
        methodAroundDeleteButton.setDisabledSelectedIcon(DeleteDisabled);
        methodAroundDeleteButton.setToolTipText(DebugToolsBundle.message("action.delete"));
        methodAroundDeleteButton.addActionListener(e -> {
            String filePath = settingState.getMethodAroundMap().remove((String) getSelectedItem());
            if (StrUtil.isNotBlank(filePath) && FileUtil.exist(filePath)) {
                FileUtil.del(filePath);
            }
            refresh();
        });
        methodAroundAddButton = new RoundedIconButton(Add);
        methodAroundAddButton.setToolTipText(DebugToolsBundle.message("action.add"));
        methodAroundAddButton.addActionListener(e -> {
            JavaEditorDialogWrapper javaEditorDialogWrapper = new JavaEditorDialogWrapper(project, this, "");
            javaEditorDialogWrapper.show();
        });
        addActionListener(e -> buttonVisible(methodAroundDetailButton, methodAroundDeleteButton));
        buttonVisible(methodAroundDetailButton, methodAroundDeleteButton);

        buttons.add(methodAroundReloadButton);
        buttons.add(methodAroundDetailButton);
        buttons.add(methodAroundDeleteButton);
        buttons.add(methodAroundAddButton);
        buttons.forEach(methodAroundPanel::add);
        reload(project, settingState);
    }

    private void reload(Project project, DebugToolsSettingState settingState) {
        String methodAroundDir = project.getBasePath() + IdeaPluginProjectConstants.METHOD_AROUND_DIR;
        List<File> files = FileUtil.loopFiles(methodAroundDir);
        settingState.getMethodAroundMap().clear();
        for (File file : files) {
            settingState.getMethodAroundMap().put(FileUtil.mainName(file), file.getAbsolutePath());
        }
        refresh();
    }

    public void refresh() {
        removeAllItems();
        addItem("");
        DebugToolsSettingState settingState = DebugToolsSettingState.getInstance(project);
        settingState.getMethodAroundMap().forEach((key, value) -> addItem(key));
    }

    public void setSelected(String identity) {
        if (identity == null) {
            return;
        }
        setSelectedItem(identity);
    }

    public void buttonVisible(JButton methodAroundDetailButton, JButton methodAroundDeleteButton) {
        boolean selected = StrUtil.isNotBlank((String) getSelectedItem());
        methodAroundDetailButton.setEnabled(selected);
        methodAroundDeleteButton.setEnabled(selected);
    }

    public void refreshBundle() {
        methodAroundReloadButton.setText(DebugToolsBundle.message("action.reload"));
        methodAroundDetailButton.setText(DebugToolsBundle.message("action.detail"));
        methodAroundDeleteButton.setText(DebugToolsBundle.message("action.delete"));
        methodAroundAddButton.setText(DebugToolsBundle.message("action.add"));
    }

}
