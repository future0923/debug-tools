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
package io.github.future0923.debug.tools.idea.ui.tab;

import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTabbedPane;
import io.github.future0923.debug.tools.base.utils.DebugToolsStringUtils;
import io.github.future0923.debug.tools.common.dto.RunResultDTO;
import io.github.future0923.debug.tools.common.enums.PrintResultType;
import io.github.future0923.debug.tools.common.utils.DebugToolsJsonUtils;
import io.github.future0923.debug.tools.idea.client.http.HttpClientUtils;
import io.github.future0923.debug.tools.idea.ui.console.MyConsolePanel;
import io.github.future0923.debug.tools.idea.ui.tree.ResultDebugTreePanel;
import io.github.future0923.debug.tools.idea.ui.tree.node.ResultDebugTreeNode;

import java.awt.*;

/**
 * @author future0923
 */
public class ExceptionTabbedPane extends JBPanel<ExceptionTabbedPane> {

    private final Project project;

    private final String throwable;

    private final String offsetPath;

    private JBTabbedPane tabPane;

    private MyConsolePanel consoleView;

    private ResultDebugTreePanel debugTab;

    private boolean loadDebug = false;

    public ExceptionTabbedPane(Project project, String throwable, String offsetPath) {
        this.project = project;
        this.throwable = throwable;
        this.offsetPath = offsetPath;
        initView();
        initEvent();
    }

    private void initView() {
        setLayout(new BorderLayout(0, 0));

        tabPane = new JBTabbedPane();

        consoleView = new MyConsolePanel(project);
        consoleView.print(throwable, ConsoleViewContentType.ERROR_OUTPUT);
        tabPane.addTab("console", consoleView);

        debugTab = new ResultDebugTreePanel(project);
        tabPane.addTab("debug", debugTab);


        add(tabPane, BorderLayout.CENTER);
    }

    private void initEvent() {
        tabPane.addChangeListener(e -> {
            // 获取当前选中的选项卡索引
            int selectedIndex = tabPane.getSelectedIndex();
            // 获取当前选中的选项卡标题
            //String selectedTabTitle = tabPane.getTitleAt(selectedIndex);
            if (selectedIndex == 1 && !loadDebug) {
                changeDebug();
            }
        });
    }

    private void changeDebug() {
        String body = HttpClientUtils.resultType(project, offsetPath, PrintResultType.DEBUG.getType());
        if (DebugToolsStringUtils.isNotBlank(body)) {
            debugTab.setRoot(new ResultDebugTreeNode(DebugToolsJsonUtils.toBean(body, RunResultDTO.class)));
            loadDebug = true;
        } else {
            Messages.showErrorDialog(project, "The request failed, please try again later", "Exception Result");
        }
    }
}
