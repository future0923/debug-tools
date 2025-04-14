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
