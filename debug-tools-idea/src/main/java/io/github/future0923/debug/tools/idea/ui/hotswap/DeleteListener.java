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
