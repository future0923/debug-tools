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
package io.github.future0923.debug.tools.idea.ui.console;

import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import io.github.future0923.debug.tools.idea.constant.IdeaPluginProjectConstants;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author future0923
 */
public class MyConsolePanel extends JPanel {

    private final ConsoleView consoleView;

    private final SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public MyConsolePanel(Project project) {
        this(TextConsoleBuilderFactory.getInstance().createBuilder(project).getConsole(), null);
    }

    public MyConsolePanel(ConsoleView consoleView, DefaultActionGroup toolbarActions) {
        super(new BorderLayout());
        this.consoleView = consoleView;
        // 先调用getComponent方法确保组建被正确的初始化（editor对象），这样调用createConsoleActions方法才不会报错
        JComponent component = consoleView.getComponent();
        // 创建工具栏面板
        JPanel toolbarPanel = new JPanel(new BorderLayout());
        if (toolbarActions == null) {
            toolbarActions = new DefaultActionGroup();
            // createConsoleActions方法必须保证有editor对象
            toolbarActions.addAll(consoleView.createConsoleActions());
        }
        ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar(IdeaPluginProjectConstants.ROOT_TYPE_ID, toolbarActions, false);
        actionToolbar.setTargetComponent(this);
        toolbarPanel.add(actionToolbar.getComponent());
        // 添加组件到面板
        add(toolbarPanel, BorderLayout.WEST);
        add(component, BorderLayout.CENTER);
    }

    public void print(@NotNull String text) {
        consoleView.print(text, ConsoleViewContentType.NORMAL_OUTPUT);
    }

    public void print(@NotNull String text, @NotNull ConsoleViewContentType contentType) {
        consoleView.print(text, contentType);
    }

    public void printWithTime(@NotNull String text, @NotNull ConsoleViewContentType contentType) {
        consoleView.print(getTime() + text, contentType);
    }

    public void println() {
        consoleView.print("\n", ConsoleViewContentType.NORMAL_OUTPUT);
    }

    public boolean isDisposed() {
        return Disposer.newCheckedDisposable(consoleView).isDisposed();
    }

    private String getTime() {
        return timeFormat.format(new Date()) + " ";
    }
}
