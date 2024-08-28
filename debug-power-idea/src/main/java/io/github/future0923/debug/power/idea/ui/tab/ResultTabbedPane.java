package io.github.future0923.debug.power.idea.ui.tab;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTabbedPane;
import io.github.future0923.debug.power.base.utils.DebugPowerStringUtils;
import io.github.future0923.debug.power.common.dto.RunResultDTO;
import io.github.future0923.debug.power.common.enums.PrintResultType;
import io.github.future0923.debug.power.common.enums.ResultClassType;
import io.github.future0923.debug.power.common.protocal.packet.response.RunTargetMethodResponsePacket;
import io.github.future0923.debug.power.common.utils.DebugPowerJsonUtils;
import io.github.future0923.debug.power.idea.client.http.HttpClientUtils;
import io.github.future0923.debug.power.idea.ui.editor.JsonEditor;
import io.github.future0923.debug.power.idea.ui.editor.TextEditor;
import io.github.future0923.debug.power.idea.ui.tree.ResultTreePanel;
import io.github.future0923.debug.power.idea.ui.tree.node.ResultTreeNode;

import java.awt.*;

/**
 * @author future0923
 */
public class ResultTabbedPane extends JBPanel<ResultTabbedPane> {

    private final Project project;

    private final RunTargetMethodResponsePacket packet;

    private JBTabbedPane tabPane;

    private TextEditor stringTab;

    private JsonEditor jsonTab;

    private ResultTreePanel debugTab;

    private boolean loadJson = false;

    private boolean loadDebug = false;

    public ResultTabbedPane(Project project, RunTargetMethodResponsePacket packet) {
        this.project = project;
        this.packet = packet;
        initView();
        initEvent();
    }

    private void initView() {
        setLayout(new BorderLayout(0, 0));

        tabPane = new JBTabbedPane();

        stringTab = new TextEditor(project, packet.getPrintResult());
        stringTab.setName("STRING");
        tabPane.addTab("toString", stringTab);

        jsonTab = new JsonEditor(project);
        jsonTab.setName("JSON");
        tabPane.addTab("json", jsonTab);

        if (!ResultClassType.VOID.equals(packet.getResultClassType())
                && !ResultClassType.NULL.equals(packet.getResultClassType())) {
            debugTab = new ResultTreePanel(project);
            tabPane.addTab("debug", debugTab);
        }


        add(tabPane, BorderLayout.CENTER);
    }

    private void initEvent() {
        tabPane.addChangeListener(e -> {
            // 获取当前选中的选项卡索引
            int selectedIndex = tabPane.getSelectedIndex();
            // 获取当前选中的选项卡标题
            //String selectedTabTitle = tabPane.getTitleAt(selectedIndex);
            if (selectedIndex == 1 && !loadJson) {
                changeJson();
            } else if (selectedIndex == 2 && !loadDebug) {
                changeDebug();
            }
        });
    }

    private void changeJson() {
        String text = "{}";
        if (ResultClassType.VOID.equals(packet.getResultClassType())) {
            text = "{\n    \"result\": \"Void\"\n}";
        } else if (ResultClassType.NULL.equals(packet.getResultClassType())) {
            text = "{\n    \"result\": \"Null\"\n}";
        } else if (ResultClassType.SIMPLE.equals(packet.getResultClassType())) {
            text = "{\n    \"result\": \"" + packet.getPrintResult() + "\"\n}";
        } else if (ResultClassType.OBJECT.equals(packet.getResultClassType())) {
            text = HttpClientUtils.resultType(project, packet.getOffsetPath(), PrintResultType.JSON.getType());
        }
        jsonTab.setText(text);
        loadJson = true;
    }

    private void changeDebug() {
        if (debugTab == null) {
            return;
        }
        if (ResultClassType.VOID.equals(packet.getResultClassType())) {
            Messages.showErrorDialog(project, "Void does not support viewing", "Debug Result");
        } else if (ResultClassType.NULL.equals(packet.getResultClassType())) {
            Messages.showErrorDialog(project, "Null does not support viewing", "Debug Result");
        } else if (ResultClassType.SIMPLE.equals(packet.getResultClassType())) {
            debugTab.setRoot(new ResultTreeNode(new RunResultDTO("result", packet.getPrintResult())));
            loadDebug = true;
        } else if (ResultClassType.OBJECT.equals(packet.getResultClassType())) {
            String body = HttpClientUtils.resultType(project, packet.getOffsetPath(), PrintResultType.DEBUG.getType());
            if (DebugPowerStringUtils.isNotBlank(body)) {
                debugTab.setRoot(new ResultTreeNode(DebugPowerJsonUtils.toBean(body, RunResultDTO.class)));
                loadDebug = true;
            } else {
                Messages.showErrorDialog(project, "The request failed, please try again later", "Request Result");
            }
        }
    }
}
