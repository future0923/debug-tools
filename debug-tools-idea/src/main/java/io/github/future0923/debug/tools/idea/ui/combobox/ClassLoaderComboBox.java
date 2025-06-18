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
package io.github.future0923.debug.tools.idea.ui.combobox;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import io.github.future0923.debug.tools.common.protocal.http.AllClassLoaderRes;
import io.github.future0923.debug.tools.idea.client.http.HttpClientUtils;
import io.github.future0923.debug.tools.idea.utils.StateUtils;

import javax.swing.*;
import java.awt.*;

/**
 * @author future0923
 */
public class ClassLoaderComboBox extends ComboBox<AllClassLoaderRes.Item> {

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
            addActionListener(e-> {
                AllClassLoaderRes.Item selectedItem = (AllClassLoaderRes.Item) getSelectedItem();
                if (selectedItem != null) {
                    StateUtils.setProjectDefaultClassLoader(project, selectedItem);
                }
            });
        }
    }

    public void getAllClassLoader() {
        removeAllItems();
        AllClassLoaderRes allClassLoaderRes;
        try {
            allClassLoaderRes = HttpClientUtils.allClassLoader(project);
        } catch (Exception ignored) {
            return;
        }
        AllClassLoaderRes.Item defaultClassLoader = null;
        for (AllClassLoaderRes.Item item : allClassLoaderRes.getItemList()) {
            if (item.getIdentity().equals(allClassLoaderRes.getDefaultIdentity())) {
                defaultClassLoader = item;
            }
            addItem(item);
        }
        if (defaultClassLoader != null) {
            setSelectedItem(defaultClassLoader);
        }
    }

    public void setSelectedClassLoader(AllClassLoaderRes.Item identity) {
        if (identity == null) {
            return;
        }
        setSelectedItem(identity);
    }

}
