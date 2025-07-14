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

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import io.github.future0923.debug.tools.base.utils.DebugToolsThreadUtils;
import io.github.future0923.debug.tools.common.protocal.http.AllClassLoaderRes;
import io.github.future0923.debug.tools.idea.action.ExecuteLastWithDefaultClassLoaderEditorPopupMenuAction;
import io.github.future0923.debug.tools.idea.client.http.HttpClientUtils;
import io.github.future0923.debug.tools.idea.utils.StateUtils;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.TimeUnit;

/**
 * @author future0923
 */
public class ClassLoaderComboBox extends ComboBox<AllClassLoaderRes.Item> {

    private static final Logger logger = Logger.getInstance(ExecuteLastWithDefaultClassLoaderEditorPopupMenuAction.class);

    private final Project project;

    public ClassLoaderComboBox(Project project) {
        this(project, -1, true);
    }

    public ClassLoaderComboBox(Project project, int width, boolean isDefault) {
        super(width);
        this.project = project;
        setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                AllClassLoaderRes.Item item = (AllClassLoaderRes.Item) value;
                if (item == null) {
                    return new JLabel();
                }
                String classLoader = item.getName();
                JLabel jLabel = (JLabel) super.getListCellRendererComponent(list, classLoader, index, isSelected, cellHasFocus);
                jLabel.setText(classLoader + "@" + item.getIdentity());
                return jLabel;
            }
        });
        if (isDefault) {
            addActionListener(e -> {
                AllClassLoaderRes.Item selectedItem = (AllClassLoaderRes.Item) getSelectedItem();
                if (selectedItem != null) {
                    StateUtils.setProjectDefaultClassLoader(project, selectedItem);
                }
            });
        }
    }

    /**
     * 刷新ClassLoader下拉框
     *
     * @param changeDefaultClassLoader 是否修改默认ClassLoader
     */
    public void refreshClassLoader(boolean changeDefaultClassLoader) {
        removeAllItems();
        ApplicationManager.getApplication().invokeLater(() -> {
            AllClassLoaderRes allClassLoaderRes = null;
            int retryCount = 0;
            while (!Thread.currentThread().isInterrupted() && retryCount < 20) {
                try {
                    allClassLoaderRes = HttpClientUtils.allClassLoader(project);
                    break;
                } catch (Exception e) {
                    retryCount++;
                }
                if (!DebugToolsThreadUtils.sleep(1, TimeUnit.SECONDS)) {
                    return;
                }
            }
            if (allClassLoaderRes == null) {
                return;
            }
            AllClassLoaderRes.Item defaultClassLoader = null;
            for (AllClassLoaderRes.Item item : allClassLoaderRes.getItemList()) {
                if (item.getIdentity().equals(allClassLoaderRes.getDefaultIdentity())) {
                    defaultClassLoader = item;
                }
                addItem(item);
            }
            if (changeDefaultClassLoader && defaultClassLoader != null) {
                setSelectedItem(defaultClassLoader);
                StateUtils.setProjectDefaultClassLoader(project, defaultClassLoader);
            }
        });
    }

    public void setSelectedClassLoader(AllClassLoaderRes.Item identity) {
        if (identity == null) {
            return;
        }
        setSelectedItem(identity);
    }

}
