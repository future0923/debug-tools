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
package io.github.future0923.debug.tools.idea.ui.editor;

import com.intellij.json.JsonFileType;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author future0923
 */
public class JsonEditor extends BaseEditor {

    public static final String FILE_NAME = "DebugToolsEditFile.json";

    /**
     * json格式
     */
    public static final FileType JSON_FILE_TYPE = JsonFileType.INSTANCE;

    public JsonEditor(@NotNull Project project) {
        this(project, null);
    }

    public JsonEditor(Project project, String text) {
        super(project, JSON_FILE_TYPE, text);
    }

    @Override
    protected String fileName() {
        return FILE_NAME;
    }

    @Override
    public void setText(@Nullable String text) {
        setText(text, JSON_FILE_TYPE);
    }
}
