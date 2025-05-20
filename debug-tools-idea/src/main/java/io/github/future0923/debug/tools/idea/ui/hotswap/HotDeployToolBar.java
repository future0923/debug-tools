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
