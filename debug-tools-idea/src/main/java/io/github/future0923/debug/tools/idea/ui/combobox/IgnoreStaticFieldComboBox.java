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

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import io.github.future0923.debug.tools.base.hutool.core.io.FileUtil;
import io.github.future0923.debug.tools.base.hutool.core.util.StrUtil;
import io.github.future0923.debug.tools.idea.bundle.DebugToolsBundle;
import io.github.future0923.debug.tools.idea.constant.IdeaPluginProjectConstants;
import io.github.future0923.debug.tools.idea.setting.DebugToolsSettingState;
import io.github.future0923.debug.tools.idea.ui.main.ConfDialogWrapper;
import io.github.future0923.debug.tools.idea.utils.DebugToolsNotifierUtil;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

/**
 * 热重载忽略指定静态字段配置UI
 *
 * @author future0923
 */
public class IgnoreStaticFieldComboBox extends ComboBox<String> {

    private final Project project;

    @Getter
    private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));

    private final JButton reloadButton;

    private final JButton detailButton;

    private final JButton deleteButton;

    private final JButton addButton;

    public IgnoreStaticFieldComboBox(Project project) {
        this(project, -1);
    }

    public IgnoreStaticFieldComboBox(Project project, int width) {
        super(width);
        this.project = project;
        DebugToolsSettingState settingState = DebugToolsSettingState.getInstance(project);
        reloadButton = new JButton(DebugToolsBundle.message("action.reload"));
        reloadButton.addActionListener(e -> {
            reload(project, settingState);
            DebugToolsNotifierUtil.notifyInfo(project, DebugToolsBundle.message("reload.successful"));
        });
        detailButton = new JButton(DebugToolsBundle.message("action.detail"));
        detailButton.addActionListener(e -> {
            ConfDialogWrapper wrapper = new ConfDialogWrapper(project, this, (String) getSelectedItem());
            wrapper.show();
        });
        deleteButton = new JButton(DebugToolsBundle.message("action.delete"));
        deleteButton.addActionListener(e -> {
            String filePath = settingState.getIgnoreStaticFieldPathMap().remove((String) getSelectedItem());
            if (StrUtil.isNotBlank(filePath) && FileUtil.exist(filePath)) {
                FileUtil.del(filePath);
            }
            refresh();
        });
        addButton = new JButton(DebugToolsBundle.message("action.add"));
        addButton.addActionListener(e -> {
            ConfDialogWrapper wrapper = new ConfDialogWrapper(project, this, "");
            wrapper.show();
        });
        addActionListener(e -> buttonVisible(detailButton, deleteButton));
        buttonVisible(detailButton, deleteButton);
        panel.add(reloadButton);
        panel.add(detailButton);
        panel.add(deleteButton);
        panel.add(addButton);
        reload(project, settingState);
    }

    private void reload(Project project, DebugToolsSettingState settingState) {
        String dir = project.getBasePath() + IdeaPluginProjectConstants.IGNORE_STATIC_FIELD_DIR;
        List<File> files = FileUtil.loopFiles(dir);
        settingState.getIgnoreStaticFieldPathMap().clear();
        for (File file : files) {
            settingState.getIgnoreStaticFieldPathMap().put(FileUtil.mainName(file), file.getAbsolutePath());
        }
        refresh();
    }

    public void refresh() {
        removeAllItems();
        addItem("");
        DebugToolsSettingState settingState = DebugToolsSettingState.getInstance(project);
        settingState.getIgnoreStaticFieldPathMap().forEach((key, value) -> addItem(key));
    }

    public void setSelected(String identity) {
        if (identity == null) {
            return;
        }
        setSelectedItem(identity);
    }

    public void buttonVisible(JButton detailButton, JButton deleteButton) {
        boolean selected = StrUtil.isNotBlank((String) getSelectedItem());
        detailButton.setVisible(selected);
        deleteButton.setVisible(selected);
    }

}
