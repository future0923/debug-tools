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

import javax.swing.*;

/**
 * 热重载忽略指定静态字段配置UI
 *
 * @author future0923
 */
public class IgnoreStaticFieldComboBox extends ComboBox<Object> {

    private final Project project;

    private IgnoreStaticFieldComboBoxV2 delegate;

    public IgnoreStaticFieldComboBox(Project project) {
        this(project, -1);
    }

    public IgnoreStaticFieldComboBox(Project project, int width) {
        super(width);
        this.project = project;
        delegate = new IgnoreStaticFieldComboBoxV2(project);
    }

    public JPanel getPanel() {
        return delegate != null ? delegate.getComponent() : new JPanel();
    }

    public void refresh() {
        if (delegate != null) {
            delegate.refresh();
        }
    }

    public void setSelected(String identity) {
        if (delegate != null) {
            delegate.setSelected(identity);
        }
    }

    @Override
    public Object getSelectedItem() {
        if (delegate != null) {
            return delegate.getSelectedItem();
        }
        return super.getSelectedItem();
    }
}
