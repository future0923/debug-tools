package io.github.future0923.debug.power.idea.ui.main;

import com.intellij.ui.components.panels.HorizontalBox;
import io.github.future0923.debug.power.idea.listener.data.MulticasterEventPublisher;
import io.github.future0923.debug.power.idea.listener.data.event.ConvertDataEvent;
import io.github.future0923.debug.power.idea.listener.data.event.PrettyDataEvent;
import io.github.future0923.debug.power.idea.listener.data.event.SimpleDataEvent;
import io.github.future0923.debug.power.idea.ui.convert.ConvertType;
import io.github.future0923.debug.power.idea.ui.tool.ToolBar;
import io.github.future0923.debug.power.idea.utils.DebugPowerIconUtil;

import javax.swing.*;

/**
 * @author future0923
 */
public class MainToolBar extends ToolBar {

    private final MulticasterEventPublisher publisher;

    public MainToolBar(MulticasterEventPublisher publisher) {
        super();
        this.publisher = publisher;
        this.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, this.getBackground().darker()));
        initButtons();
        super.add(new HorizontalBox());
    }

    private void initButtons() {
        genButton(ConvertType.IMPORT.getDescription(), DebugPowerIconUtil.import_icon, DebugPowerIconUtil.import_icon, actionEvent -> {
            publisher.multicastEvent(new ConvertDataEvent(ConvertType.IMPORT));
        });
        genButton(ConvertType.EXPORT.getDescription(), DebugPowerIconUtil.export_icon, DebugPowerIconUtil.export_icon, actionEvent -> {
            publisher.multicastEvent(new ConvertDataEvent(ConvertType.EXPORT));
        });
        genButton("Pretty Json", DebugPowerIconUtil.pretty_icon, DebugPowerIconUtil.pretty_icon, actionEvent -> {
            publisher.multicastEvent(new PrettyDataEvent());
        });
        genButton("Gen Param", DebugPowerIconUtil.example_icon, DebugPowerIconUtil.example_icon, actionEvent -> {
            publisher.multicastEvent(new SimpleDataEvent());
        });
    }
}
