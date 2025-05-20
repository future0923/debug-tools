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

import com.intellij.ui.components.JBList;
import io.github.future0923.debug.tools.idea.listener.data.BaseDataListener;
import io.github.future0923.debug.tools.idea.listener.data.event.DeployFileDataEvent;

/**
 * @author future0923
 */
public class DeleteListener extends BaseDataListener<DeployFileDataEvent> {

    private final HotDeployDialog dialog;

    private final JBList<String> jbList;

    public DeleteListener(HotDeployDialog dialog, JBList<String> jbList) {
        this.dialog = dialog;
        this.jbList = jbList;
    }

    @Override
    public void onEvent(DeployFileDataEvent event) {
        if (!DeployFileDataEvent.DeployFileType.Delete.equals(event.getFileType())) {
            return;
        }
        int selectedIndex = jbList.getSelectedIndex();
        if (selectedIndex != -1) {
            dialog.getHotUndoList().remove(selectedIndex);
            dialog.getHotUndoShowList().remove(selectedIndex);
            jbList.setSelectedIndex(selectedIndex - 1);
        }
    }
}
