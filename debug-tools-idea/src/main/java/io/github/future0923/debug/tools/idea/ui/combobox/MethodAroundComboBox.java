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

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import io.github.future0923.debug.tools.base.hutool.core.io.FileUtil;
import io.github.future0923.debug.tools.base.hutool.core.util.StrUtil;
import io.github.future0923.debug.tools.idea.action.ExecuteLastWithDefaultClassLoaderEditorPopupMenuAction;
import io.github.future0923.debug.tools.idea.constant.IdeaPluginProjectConstants;
import io.github.future0923.debug.tools.idea.setting.DebugToolsSettingState;
import io.github.future0923.debug.tools.idea.ui.main.JavaEditorDialogWrapper;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

/**
 * @author future0923
 */
public class MethodAroundComboBox extends ComboBox<String> {

    private static final Logger logger = Logger.getInstance(ExecuteLastWithDefaultClassLoaderEditorPopupMenuAction.class);

    private final Project project;

    @Getter
    private final JPanel methodAroundPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));

    public MethodAroundComboBox(Project project) {
        this(project, -1);
    }

    public MethodAroundComboBox(Project project, int width) {
        super(width);
        this.project = project;
        DebugToolsSettingState settingState = DebugToolsSettingState.getInstance(project);
        JButton methodAroundRefreshButton = new JButton("Reload");
        methodAroundRefreshButton.addActionListener(e -> reload(project, settingState));
        JButton methodAroundDetailButton = new JButton("Detail");
        methodAroundDetailButton.addActionListener(e -> {
            JavaEditorDialogWrapper javaEditorDialogWrapper = new JavaEditorDialogWrapper(project, this, (String) getSelectedItem());
            javaEditorDialogWrapper.show();
        });
        JButton methodAroundDeleteButton = new JButton("Delete");
        methodAroundDeleteButton.addActionListener(e -> {
            String filePath = settingState.getMethodAroundMap().remove((String) getSelectedItem());
            if (StrUtil.isNotBlank(filePath) && FileUtil.exist(filePath)) {
                FileUtil.del(filePath);
            }
            refresh();
        });
        JButton methodAroundAddButton = new JButton("Add");
        methodAroundAddButton.addActionListener(e -> {
            JavaEditorDialogWrapper javaEditorDialogWrapper = new JavaEditorDialogWrapper(project, this, "");
            javaEditorDialogWrapper.show();
        });
        addActionListener(e -> buttonVisible(methodAroundDetailButton, methodAroundDeleteButton));
        buttonVisible(methodAroundDetailButton, methodAroundDeleteButton);
        methodAroundPanel.add(methodAroundRefreshButton);
        methodAroundPanel.add(methodAroundDetailButton);
        methodAroundPanel.add(methodAroundDeleteButton);
        methodAroundPanel.add(methodAroundAddButton);
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
        methodAroundDetailButton.setVisible(selected);
        methodAroundDeleteButton.setVisible(selected);
    }

}
