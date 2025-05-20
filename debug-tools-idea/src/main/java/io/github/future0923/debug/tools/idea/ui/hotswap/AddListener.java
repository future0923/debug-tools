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
package io.github.future0923.debug.tools.idea.ui.hotswap;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import io.github.future0923.debug.tools.idea.listener.data.BaseDataListener;
import io.github.future0923.debug.tools.idea.listener.data.event.DeployFileDataEvent;

import java.util.List;

/**
 * @author future0923
 */
public class AddListener extends BaseDataListener<DeployFileDataEvent> {

    private final Project project;

    private final HotDeployDialog dialog;

    public AddListener(Project project, HotDeployDialog dialog) {
        this.project = project;
        this.dialog = dialog;
    }

    @Override
    public void onEvent(DeployFileDataEvent event) {
        if (!DeployFileDataEvent.DeployFileType.Add.equals(event.getFileType())) {
            return;
        }
        ApplicationManager.getApplication().invokeLater(() -> {
            FileChooserDescriptor descriptor = new FileChooserDescriptor(true, false, false, false, false, true);
            String basePath = project.getBasePath();
            VirtualFile initialDirectory = io.github.future0923.debug.tools.idea.utils.VirtualFileUtil.getVirtualFileByPath(basePath);
            VirtualFile[] chosenFiles = FileChooser.chooseFiles(descriptor, project, initialDirectory);
            if (null != basePath && !basePath.endsWith("/")) {
                basePath = basePath + "/";
            }
            List<String> hotUndoList = dialog.getHotUndoList();
            for (VirtualFile file : chosenFiles) {
                if (!hotUndoList.contains(file.getPath())) {
                    if (!"java".equals(file.getExtension())) {
                        continue;
                    }
                    String path = file.getPath();
                    if (null != basePath) {
                        path = path.replaceAll(basePath, "");
                    }
                    dialog.getHotUndoShowList().addElement(path);
                    hotUndoList.add(file.getPath());
                }
            }
        });
    }
}
