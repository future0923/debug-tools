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
package io.github.future0923.debug.tools.idea.tool.ui;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import io.github.future0923.debug.tools.base.enums.PrintSqlType;
import io.github.future0923.debug.tools.base.hutool.core.util.ObjectUtil;
import io.github.future0923.debug.tools.base.utils.DebugToolsThreadUtils;
import io.github.future0923.debug.tools.idea.bundle.DebugToolsBundle;
import io.github.future0923.debug.tools.idea.client.http.HttpClientUtils;
import io.github.future0923.debug.tools.idea.setting.DebugToolsSettingState;
import io.github.future0923.debug.tools.idea.utils.DebugToolsNotifierUtil;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.TimeUnit;

/**
 * @author future0923
 */
public class PrintSqlPanel extends JBPanel<PrintSqlPanel> {

    private final Project project;

    private final DebugToolsSettingState settingState;

    private final JBLabel printSqlLabel;

    private final ComboBox<PrintSqlType> printSqlComboBox;

    public PrintSqlPanel(Project project) {
        super(new FlowLayout(FlowLayout.LEFT, 5, 5));
        this.project = project;
        this.settingState = DebugToolsSettingState.getInstance(project);
        printSqlLabel = new JBLabel(DebugToolsBundle.message("setting.panel.print.sql"));
        this.add(printSqlLabel);
        printSqlComboBox = new ComboBox<>(new PrintSqlType[]{PrintSqlType.NO, PrintSqlType.COMPRESS, PrintSqlType.PRETTY}, 130);
        printSqlComboBox.setRenderer(new DefaultListCellRenderer(){
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof PrintSqlType type) {
                    setText(DebugToolsBundle.message(type.getBundleKey()));
                }
                return this;
            }
        });
        printSqlComboBox.setSelectedItem(settingState.getPrintSql());
        printSqlComboBox.addActionListener(e -> ApplicationManager.getApplication().executeOnPooledThread(() -> {
            PrintSqlType selectedItem = (PrintSqlType) printSqlComboBox.getSelectedItem();
            if (selectedItem == null) {
                return;
            }
            try {
                String changePrintSqlType = HttpClientUtils.changePrintSqlType(project, selectedItem.getType());
                ApplicationManager.getApplication().invokeLater(() -> DebugToolsNotifierUtil.notifyInfo(project, DebugToolsBundle.message("print.sql.type.change", DebugToolsBundle.message(PrintSqlType.of(changePrintSqlType).getBundleKey()))));
            } catch (Exception ex) {
                ApplicationManager.getApplication().invokeLater(() -> DebugToolsNotifierUtil.notifyError(project, DebugToolsBundle.message("print.sql.type.change.fail", ex.getMessage())));
            }
        }));
        this.add(printSqlComboBox);
    }

    public void refresh() {
        boolean visible = !PrintSqlType.NO.equals(settingState.getPrintSql());
        this.setVisible(visible);
        if (visible) {
            PrintSqlType selectedItem = (PrintSqlType) printSqlComboBox.getSelectedItem();
            // 异步执行请求
            ApplicationManager.getApplication().executeOnPooledThread(() -> {
                int retryCount = 0;
                while (!Thread.currentThread().isInterrupted() && retryCount < 20) {
                    try {
                        String result = HttpClientUtils.getPrintSqlType(project);
                        if (selectedItem == null || ObjectUtil.notEqual(selectedItem.getType(), result)) {
                            ApplicationManager.getApplication().invokeLater(() -> printSqlComboBox.setSelectedItem(PrintSqlType.of(result)));
                        }
                        break;
                    } catch (Exception e) {
                        retryCount++;
                    }
                    if (!DebugToolsThreadUtils.sleep(1, TimeUnit.SECONDS)) {
                        return;
                    }
                }
            });
        }
    }

    public void refreshBundle() {
        printSqlLabel.setText(DebugToolsBundle.message("setting.panel.print.sql"));
        printSqlComboBox.repaint();
    }

}
