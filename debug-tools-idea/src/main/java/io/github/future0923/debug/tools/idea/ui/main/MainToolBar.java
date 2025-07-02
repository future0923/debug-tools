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
package io.github.future0923.debug.tools.idea.ui.main;

import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
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
    private final Project project;

    public MainToolBar(MulticasterEventPublisher publisher, Project project) {
        super();
        this.publisher = publisher;
        this.project = project;
        this.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, this.getBackground().darker()));
        initButtons();
        super.add(new HorizontalBox());
    }

    private void initButtons() {
        genButton(ConvertType.IMPORT.getDescription(), DebugToolsIcons.Import, DebugToolsIcons.Import, actionEvent -> {
            publisher.multicastEvent(new ConvertDataEvent(ConvertType.IMPORT));
        });
        genButton(ConvertType.EXPORT.getDescription(), DebugToolsIcons.Export, DebugToolsIcons.Export, actionEvent -> {
            publisher.multicastEvent(new ConvertDataEvent(ConvertType.EXPORT));
        });
        genButton("Pretty Json", DebugToolsIcons.Pretty, DebugToolsIcons.Pretty, actionEvent -> {
            publisher.multicastEvent(new PrettyDataEvent());
        });
        genButton("Gen Param", DebugToolsIcons.ExampleSimple, DebugToolsIcons.ExampleSimple, actionEvent -> {
            publisher.multicastEvent(new ExampleDataEvent(GenParamType.SIMPLE));
        });
        genButton("Gen Param With Default Current Entity Class", DebugToolsIcons.ExampleCurrent, DebugToolsIcons.ExampleCurrent, actionEvent -> {
            publisher.multicastEvent(new ExampleDataEvent(GenParamType.CURRENT));
        });
        genButton("Gen Param With Default All", DebugToolsIcons.ExampleAll, DebugToolsIcons.ExampleAll, actionEvent -> {
            publisher.multicastEvent(new ExampleDataEvent(GenParamType.ALL));
        });
        genButton("Settings", DebugToolsIcons.Setting, DebugToolsIcons.Setting, actionEvent -> {
            ShowSettingsUtil.getInstance().showSettingsDialog(project, "DebugToolsTool.Setting");
        });
        genButton("Help", DebugToolsIcons.Help, DebugToolsIcons.Help, actionEvent -> {
            ShowSettingsUtil.getInstance().showSettingsDialog(project, "DebugToolsTool.Setting");
        });
    }
}
