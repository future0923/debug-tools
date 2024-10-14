package io.github.future0923.debug.tools.idea.ui.tab;

import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTabbedPane;
import io.github.future0923.debug.tools.base.utils.DebugToolsStringUtils;
import io.github.future0923.debug.tools.common.dto.RunResultDTO;
import io.github.future0923.debug.tools.common.enums.PrintResultType;
import io.github.future0923.debug.tools.common.enums.ResultClassType;
import io.github.future0923.debug.tools.common.protocal.packet.response.RunTargetMethodResponsePacket;
import io.github.future0923.debug.tools.common.utils.DebugToolsJsonUtils;
import io.github.future0923.debug.tools.idea.client.http.HttpClientUtils;
import io.github.future0923.debug.tools.idea.ui.console.MyConsolePanel;
import io.github.future0923.debug.tools.idea.ui.tree.ResultTreePanel;
import io.github.future0923.debug.tools.idea.ui.tree.node.ResultTreeNode;

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

    private ResultTreePanel debugTab;

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

        debugTab = new ResultTreePanel(project);
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
            debugTab.setRoot(new ResultTreeNode(DebugToolsJsonUtils.toBean(body, RunResultDTO.class)));
            loadDebug = true;
        } else {
            Messages.showErrorDialog(project, "The request failed, please try again later", "Exception Result");
        }
    }
}
