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

import javax.swing.*;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.panels.HorizontalBox;

import io.github.future0923.debug.tools.idea.bundle.DebugToolsBundle;
import io.github.future0923.debug.tools.idea.listener.data.MulticasterEventPublisher;
import io.github.future0923.debug.tools.idea.listener.data.event.ConvertDataEvent;
import io.github.future0923.debug.tools.idea.listener.data.event.ExampleDataEvent;
import io.github.future0923.debug.tools.idea.listener.data.event.PrettyDataEvent;
import io.github.future0923.debug.tools.idea.listener.data.event.ToggleViewEvent;
import io.github.future0923.debug.tools.idea.setting.GenParamType;
import io.github.future0923.debug.tools.idea.ui.convert.ConvertType;
import io.github.future0923.debug.tools.idea.ui.tool.ToolBar;
import io.github.future0923.debug.tools.idea.utils.DebugToolsIcons;

/**
 * @author future0923
 */
public class MainToolBar extends ToolBar {

    private final MulticasterEventPublisher publisher;
    // 可选：直接切换回调（用于绕过事件总线，提升可靠性）
    private final Runnable toggleViewHandler;

    public MainToolBar(MulticasterEventPublisher publisher, Project project) {
        this(publisher, project, null);
    }

    public MainToolBar(MulticasterEventPublisher publisher, Project project, Runnable toggleViewHandler) {
        super();
        this.publisher = publisher;
        this.toggleViewHandler = toggleViewHandler;
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
        genButton(DebugToolsBundle.message("main.toolbar.pretty.json"), DebugToolsIcons.Pretty, DebugToolsIcons.Pretty, actionEvent -> {
            publisher.multicastEvent(new PrettyDataEvent());
        });
        genButton(DebugToolsBundle.message("main.toolbar.gen.param"), DebugToolsIcons.ExampleSimple, DebugToolsIcons.ExampleSimple, actionEvent -> {
            publisher.multicastEvent(new ExampleDataEvent(GenParamType.SIMPLE));
        });
        genButton(DebugToolsBundle.message("main.toolbar.gen.param.with.default.current.entity.class"), DebugToolsIcons.ExampleCurrent, DebugToolsIcons.ExampleCurrent, actionEvent -> {
            publisher.multicastEvent(new ExampleDataEvent(GenParamType.CURRENT));
        });
        genButton(DebugToolsBundle.message("main.toolbar.gen.param.with.default.all"), DebugToolsIcons.ExampleAll, DebugToolsIcons.ExampleAll, actionEvent -> {
            publisher.multicastEvent(new ExampleDataEvent(GenParamType.ALL));
        });
        // JSON ⇄ Table 视图切换按钮（需求：放在 MainToolBar 内）
        JButton toggleBtn = genButton("JSON ⇄ Table", DebugToolsIcons.Convert, DebugToolsIcons.Convert, actionEvent -> {
            // 优先直接回调本地切换逻辑，避免某些环境下事件系统分发不到监听器
            if (toggleViewHandler != null) {
                toggleViewHandler.run();
            } else {
                publisher.multicastEvent(new ToggleViewEvent());
            }
        });
        // 明确显示文字，避免仅图标导致用户找不到
        toggleBtn.setText("JSON ⇄ Table");
        toggleBtn.setHorizontalTextPosition(SwingConstants.RIGHT);
        toggleBtn.setIconTextGap(6);
        toggleBtn.setPreferredSize(new com.intellij.util.ui.JBDimension(120, 30));
    }
}
