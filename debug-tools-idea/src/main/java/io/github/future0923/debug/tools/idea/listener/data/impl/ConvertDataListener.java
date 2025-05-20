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
