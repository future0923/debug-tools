package io.github.future0923.debug.power.idea.ui.main;

import com.intellij.ui.components.panels.HorizontalBox;
import io.github.future0923.debug.power.idea.listener.data.MulticasterEventPublisher;
import io.github.future0923.debug.power.idea.listener.data.event.ConvertDataEvent;
import io.github.future0923.debug.power.idea.listener.data.event.PrettyDataEvent;
import io.github.future0923.debug.power.idea.listener.data.event.ExampleDataEvent;
import io.github.future0923.debug.power.idea.setting.GenParamType;
import io.github.future0923.debug.power.idea.ui.convert.ConvertType;
import io.github.future0923.debug.power.idea.ui.tool.ToolBar;
import io.github.future0923.debug.power.idea.utils.DebugPowerIcons;

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
        genButton(ConvertType.IMPORT.getDescription(), DebugPowerIcons.import_icon, DebugPowerIcons.import_icon, actionEvent -> {
            publisher.multicastEvent(new ConvertDataEvent(ConvertType.IMPORT));
        });
        genButton(ConvertType.EXPORT.getDescription(), DebugPowerIcons.export_icon, DebugPowerIcons.export_icon, actionEvent -> {
            publisher.multicastEvent(new ConvertDataEvent(ConvertType.EXPORT));
        });
        genButton("Pretty Json", DebugPowerIcons.pretty_icon, DebugPowerIcons.pretty_icon, actionEvent -> {
            publisher.multicastEvent(new PrettyDataEvent());
        });
        genButton("Gen Param", DebugPowerIcons.example_simple_icon, DebugPowerIcons.example_simple_icon, actionEvent -> {
            publisher.multicastEvent(new ExampleDataEvent(GenParamType.SIMPLE));
        });
        genButton("Gen Param With Default Current Entity Class", DebugPowerIcons.example_current_icon, DebugPowerIcons.example_current_icon, actionEvent -> {
            publisher.multicastEvent(new ExampleDataEvent(GenParamType.CURRENT));
        });
        genButton("Gen Param With Default All", DebugPowerIcons.example_all_icon, DebugPowerIcons.example_all_icon, actionEvent -> {
            publisher.multicastEvent(new ExampleDataEvent(GenParamType.ALL));
        });
    }
}
