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

import com.intellij.ui.components.panels.HorizontalBox;
import io.github.future0923.debug.tools.idea.listener.data.MulticasterEventPublisher;
import io.github.future0923.debug.tools.idea.listener.data.event.DeployFileDataEvent;
import io.github.future0923.debug.tools.idea.ui.tool.ToolBar;
import io.github.future0923.debug.tools.idea.utils.DebugToolsIcons;

import javax.swing.*;

/**
 * @author future0923
 */
public class HotDeployToolBar extends ToolBar {

    private final MulticasterEventPublisher publisher;


    public HotDeployToolBar(MulticasterEventPublisher publisher) {
        super();
        this.publisher = publisher;
        this.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, this.getBackground().darker()));
        initButtons();
        super.add(new HorizontalBox());
    }

    private void initButtons() {
        genButton("Add", DebugToolsIcons.Action.Add, DebugToolsIcons.Action.Add, actionEvent -> publisher.multicastEvent(DeployFileDataEvent.ofAdd()));
        genButton("Delete", DebugToolsIcons.Action.Delete, DebugToolsIcons.Action.Delete, actionEvent -> publisher.multicastEvent(DeployFileDataEvent.ofDelete()));
        genButton("Clear", DebugToolsIcons.Action.Clear, DebugToolsIcons.Action.Clear, actionEvent -> publisher.multicastEvent(DeployFileDataEvent.ofClear()));
        genButton("Reset", DebugToolsIcons.Action.Reset, DebugToolsIcons.Action.Reset, actionEvent -> publisher.multicastEvent(DeployFileDataEvent.ofReset()));
    }

}
