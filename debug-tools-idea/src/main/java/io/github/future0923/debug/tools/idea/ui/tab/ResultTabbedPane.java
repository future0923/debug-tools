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

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTabbedPane;
import io.github.future0923.debug.tools.base.hutool.core.collection.CollUtil;
import io.github.future0923.debug.tools.base.hutool.core.util.StrUtil;
import io.github.future0923.debug.tools.base.trace.MethodTreeNode;
import io.github.future0923.debug.tools.base.utils.DebugToolsStringUtils;
import io.github.future0923.debug.tools.common.dto.RunResultDTO;
import io.github.future0923.debug.tools.common.enums.PrintResultType;
import io.github.future0923.debug.tools.common.enums.ResultClassType;
import io.github.future0923.debug.tools.common.utils.DebugToolsJsonUtils;
import io.github.future0923.debug.tools.idea.client.http.HttpClientUtils;
import io.github.future0923.debug.tools.idea.ui.console.MyConsolePanel;
import io.github.future0923.debug.tools.idea.ui.editor.JsonEditor;
import io.github.future0923.debug.tools.idea.ui.tree.ResultDebugTreePanel;
import io.github.future0923.debug.tools.idea.ui.tree.ResultTraceTreePanel;
import io.github.future0923.debug.tools.idea.ui.tree.node.ResultDebugTreeNode;
import io.github.future0923.debug.tools.idea.ui.tree.node.ResultTraceTreeNode;

import java.awt.*;
import java.util.List;
import java.util.Objects;

/**
 * @author future0923
 */
public class ResultTabbedPane extends JBPanel<ResultTabbedPane> {

    private final Project project;

    private final String printResult;

    private final String offsetPath;

    private final String traceOffsetPath;

    private final ResultClassType resultClassType;

    private JBTabbedPane tabPane;

    private MyConsolePanel stringTab;

    private JsonEditor jsonTab;

    private ResultDebugTreePanel debugTab;

    private ResultTraceTreePanel traceTab;

    private boolean loadJson = false;

    private boolean loadDebug = false;

    private boolean loadTrace = false;

    public ResultTabbedPane(Project project, String printResult, String offsetPath, String traceOffsetPath, ResultClassType resultClassType) {
        this.project = project;
        this.printResult = printResult;
        this.offsetPath = offsetPath;
        this.traceOffsetPath = traceOffsetPath;
        this.resultClassType = resultClassType;
        initView();
        initEvent();
    }

    private void initView() {
        setLayout(new BorderLayout(0, 0));

        tabPane = new JBTabbedPane();

        stringTab = new MyConsolePanel(project);
        stringTab.setName("STRING");
        stringTab.print(printResult);
        tabPane.addTab("toString", stringTab);

        if (jsonTab()) {
            jsonTab = new JsonEditor(project);
            jsonTab.setName("JSON");
            tabPane.addTab("json", jsonTab);
        }

        if (!ResultClassType.VOID.equals(resultClassType)
                && !ResultClassType.NULL.equals(resultClassType)
                && debugTab()) {
            debugTab = new ResultDebugTreePanel(project);
            tabPane.addTab("debug", debugTab);
        }

        if (StrUtil.isNotBlank(traceOffsetPath)
                && traceTab()) {
            traceTab = new ResultTraceTreePanel(project);
            tabPane.addTab("trace", traceTab);
        }

        add(tabPane, BorderLayout.CENTER);
    }

    protected boolean jsonTab() {
        return false;
    }

    protected boolean debugTab() {
        return false;
    }

    protected boolean traceTab() {
        return false;
    }

    private void initEvent() {
        tabPane.addChangeListener(e -> {
            // 获取当前选中的选项卡索引
            int selectedIndex = tabPane.getSelectedIndex();
            // 获取当前选中的选项卡标题
            String selectedTabTitle = tabPane.getTitleAt(selectedIndex);
            if (Objects.equals(selectedTabTitle, "json") && !loadJson) {
                changeJson();
            } else if (Objects.equals(selectedTabTitle, "debug") && !loadDebug) {
                changeDebug();
            } else if (Objects.equals(selectedTabTitle, "trace") && !loadTrace) {
                changeTrace();
            }
        });
    }

    private void changeJson() {
        String text = "{}";
        if (ResultClassType.VOID.equals(resultClassType)) {
            text = "{\n    \"result\": \"Void\"\n}";
        } else if (ResultClassType.NULL.equals(resultClassType)) {
            text = "{\n    \"result\": \"Null\"\n}";
        } else if (ResultClassType.SIMPLE.equals(resultClassType)) {
            text = "{\n    \"result\": \"" + printResult + "\"\n}";
        } else if (ResultClassType.OBJECT.equals(resultClassType)) {
            text = HttpClientUtils.resultType(project, offsetPath, PrintResultType.JSON.getType());
        }
        jsonTab.setText(text);
        loadJson = true;
    }

    private void changeDebug() {
        if (debugTab == null) {
            return;
        }
        if (ResultClassType.VOID.equals(resultClassType)) {
            Messages.showErrorDialog(project, "Void does not support viewing", "Debug Result");
        } else if (ResultClassType.NULL.equals(resultClassType)) {
            Messages.showErrorDialog(project, "Null does not support viewing", "Debug Result");
        } else if (ResultClassType.SIMPLE.equals(resultClassType)) {
            debugTab.setRoot(new ResultDebugTreeNode(new RunResultDTO("result", printResult) {{
                setLeaf(true);
            }}));
            loadDebug = true;
        } else if (ResultClassType.OBJECT.equals(resultClassType)) {
            String body = HttpClientUtils.resultType(project, offsetPath, PrintResultType.DEBUG.getType());
            if (DebugToolsStringUtils.isNotBlank(body)) {
                debugTab.setRoot(new ResultDebugTreeNode(DebugToolsJsonUtils.toBean(body, RunResultDTO.class)));
                loadDebug = true;
            } else {
                Messages.showErrorDialog(project, "The request failed, please try again later", "Request Result");
            }
        }
    }

    private void changeTrace() {
        if (traceTab == null) {
            return;
        }
        if (StrUtil.isBlank(traceOffsetPath)) {
            Messages.showErrorDialog(project, "Trace does not support viewing", "Debug Result");
        }
        List<MethodTreeNode> methodTreeNodes = HttpClientUtils.resultTrace(project, traceOffsetPath);
        if (CollUtil.isNotEmpty(methodTreeNodes)) {
            traceTab.setRoot(new ResultTraceTreeNode(CollUtil.getFirst(methodTreeNodes)));
        }
        loadTrace = true;
    }
}
