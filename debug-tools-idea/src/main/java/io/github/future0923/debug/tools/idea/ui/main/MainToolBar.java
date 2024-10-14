package io.github.future0923.debug.tools.idea.ui.main;

import com.intellij.ui.components.panels.HorizontalBox;
import io.github.future0923.debug.tools.idea.listener.data.MulticasterEventPublisher;
import io.github.future0923.debug.tools.idea.listener.data.event.ConvertDataEvent;
import io.github.future0923.debug.tools.idea.listener.data.event.ExampleDataEvent;
import io.github.future0923.debug.tools.idea.listener.data.event.PrettyDataEvent;
import io.github.future0923.debug.tools.idea.setting.GenParamType;
import io.github.future0923.debug.tools.idea.ui.convert.ConvertType;
import io.github.future0923.debug.tools.idea.ui.tool.ToolBar;
import io.github.future0923.debug.tools.idea.utils.DebugToolsIcons;

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
        genButton(ConvertType.IMPORT.getDescription(), DebugToolsIcons.import_icon, DebugToolsIcons.import_icon, actionEvent -> {
            publisher.multicastEvent(new ConvertDataEvent(ConvertType.IMPORT));
        });
        genButton(ConvertType.EXPORT.getDescription(), DebugToolsIcons.export_icon, DebugToolsIcons.export_icon, actionEvent -> {
            publisher.multicastEvent(new ConvertDataEvent(ConvertType.EXPORT));
        });
        genButton("Pretty Json", DebugToolsIcons.pretty_icon, DebugToolsIcons.pretty_icon, actionEvent -> {
            publisher.multicastEvent(new PrettyDataEvent());
        });
        genButton("Gen Param", DebugToolsIcons.example_simple_icon, DebugToolsIcons.example_simple_icon, actionEvent -> {
            publisher.multicastEvent(new ExampleDataEvent(GenParamType.SIMPLE));
        });
        genButton("Gen Param With Default Current Entity Class", DebugToolsIcons.example_current_icon, DebugToolsIcons.example_current_icon, actionEvent -> {
            publisher.multicastEvent(new ExampleDataEvent(GenParamType.CURRENT));
        });
        genButton("Gen Param With Default All", DebugToolsIcons.example_all_icon, DebugToolsIcons.example_all_icon, actionEvent -> {
            publisher.multicastEvent(new ExampleDataEvent(GenParamType.ALL));
        });
    }
}
