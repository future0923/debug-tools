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
package io.github.future0923.debug.tools.idea.ui.editor;

import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypes;
import com.intellij.openapi.project.Project;

/**
 * @author future0923
 */
public class TextEditor extends BaseEditor {

    public static final String FILE_NAME = "DebugToolsEditFile.text";

    public static final FileType TEXT_FILE_TYPE = FileTypes.PLAIN_TEXT;

    public TextEditor(Project project) {
        this(project, null);
    }

    public TextEditor(Project project, String text) {
        super(project, TEXT_FILE_TYPE, text);
    }

    @Override
    protected String fileName() {
        return FILE_NAME;
    }

    @Override
    protected void setting(EditorSettings settings) {
        super.setting(settings);
        settings.setUseSoftWraps(true);
    }
}
