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
package io.github.future0923.debug.tools.idea.listener.data.impl;

import com.intellij.openapi.project.Project;
import io.github.future0923.debug.tools.idea.listener.data.BaseDataListener;
import io.github.future0923.debug.tools.idea.listener.data.event.ConvertDataEvent;
import io.github.future0923.debug.tools.idea.ui.main.MainJsonEditor;
import io.github.future0923.debug.tools.idea.ui.convert.ConvertDialog;

/**
 * @author future0923
 */
public class ConvertDataListener extends BaseDataListener<ConvertDataEvent> {

    private final Project project;

    private final MainJsonEditor jsonEditor;

    public ConvertDataListener(Project project, MainJsonEditor jsonEditor) {
        this.project = project;
        this.jsonEditor = jsonEditor;
    }

    @Override
    public void onEvent(ConvertDataEvent event) {
        ConvertDialog convertDialog = new ConvertDialog(project, jsonEditor, event.getConvertType());
        convertDialog.show();
    }

}
