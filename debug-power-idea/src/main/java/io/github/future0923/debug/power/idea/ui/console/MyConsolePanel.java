package io.github.future0923.debug.power.idea.ui.console;

import com.intellij.execution.actions.ClearConsoleAction;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBPanel;
import io.github.future0923.debug.power.idea.constant.IdeaPluginProjectConstants;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 * @author future0923
 */
public class MyConsolePanel extends JPanel {

    private final ConsoleView consoleView;

    public MyConsolePanel(Project project) {
        this(TextConsoleBuilderFactory.getInstance().createBuilder(project).getConsole(), null);
    }

    public MyConsolePanel(ConsoleView consoleView, DefaultActionGroup toolbarActions) {
        super(new BorderLayout());
        this.consoleView = consoleView;
        // 创建工具栏面板
        JPanel toolbarPanel = new JPanel(new BorderLayout());
        if (toolbarActions == null) {
            toolbarActions = new DefaultActionGroup();
            toolbarActions.addAll(consoleView.createConsoleActions());
        }
        ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar(IdeaPluginProjectConstants.ROOT_TYPE_ID, toolbarActions, false);
        actionToolbar.setTargetComponent(this);
        toolbarPanel.add(actionToolbar.getComponent());

        // 添加组件到面板
        add(toolbarPanel, BorderLayout.WEST);
        add(consoleView.getComponent(), BorderLayout.CENTER);
    }

    public void print(@NotNull String text) {
        consoleView.print(text, ConsoleViewContentType.NORMAL_OUTPUT);
    }

    public void print(@NotNull String text, @NotNull ConsoleViewContentType contentType) {
        consoleView.print(text, contentType);
    }
}
