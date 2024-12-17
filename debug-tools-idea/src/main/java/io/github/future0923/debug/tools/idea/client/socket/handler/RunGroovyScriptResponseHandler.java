package io.github.future0923.debug.tools.idea.client.socket.handler;

import com.intellij.execution.Executor;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.execution.ui.RunContentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowManager;
import io.github.future0923.debug.tools.common.handler.BasePacketHandler;
import io.github.future0923.debug.tools.common.protocal.packet.response.RunGroovyScriptResponsePacket;
import io.github.future0923.debug.tools.idea.client.ApplicationProjectHolder;
import io.github.future0923.debug.tools.idea.client.socket.utils.SocketSendUtils;
import io.github.future0923.debug.tools.idea.ui.tab.ExceptionTabbedPane;
import io.github.future0923.debug.tools.idea.ui.tab.GroovyResult;
import io.github.future0923.debug.tools.idea.utils.DebugToolsIcons;
import io.github.future0923.debug.tools.idea.utils.DebugToolsNotifierUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.OutputStream;

/**
 * @author future0923
 */
public class RunGroovyScriptResponseHandler extends BasePacketHandler<RunGroovyScriptResponsePacket> {

    public static final RunGroovyScriptResponseHandler INSTANCE = new RunGroovyScriptResponseHandler();

    private RunGroovyScriptResponseHandler() {

    }

    @Override
    public void handle(OutputStream outputStream, RunGroovyScriptResponsePacket packet) throws Exception {
        ApplicationProjectHolder.Info info = ApplicationProjectHolder.getInfo(packet.getApplicationName());
        if (info == null) {
            DebugToolsNotifierUtil.notifyError(null, "未找到应用" + packet.getApplicationName() + "的链接信息");
            return;
        }
        Project project = info.getProject();
        String consoleTitle = "Groovy Result";
        ToolWindowManager.getInstance(project).invokeLater(() -> {
            RunContentManager runContentManager = RunContentManager.getInstance(project);
            Executor executor = DefaultRunExecutor.getRunExecutorInstance();
            runContentManager.getAllDescriptors().stream()
                    .filter(descriptor -> descriptor.getDisplayName().equals(consoleTitle))
                    .findFirst()
                    .ifPresent(contentDescriptor -> runContentManager.removeRunContent(executor, contentDescriptor));
            RunContentDescriptor descriptor = getRunContentDescriptor(packet, project, consoleTitle);
            runContentManager.showRunContent(executor, descriptor);
        });
    }

    private static @NotNull RunContentDescriptor getRunContentDescriptor(RunGroovyScriptResponsePacket packet, Project project, String consoleTitle) {
        JComponent resultTabbedPane = packet.isSuccess()
                ? new GroovyResult(project, packet.getPrintResult(), packet.getOffsetPath(), packet.getResultClassType())
                : new ExceptionTabbedPane(project, packet.getThrowable(), packet.getOffsetPath());
        return new RunContentDescriptor(null, null, resultTabbedPane, consoleTitle) {
            @Override
            public boolean isContentReuseProhibited() {
                return true;
            }

            @Override
            public Icon getIcon() {
                return DebugToolsIcons.DebugTools;
            }

            @Override
            public void dispose() {
                SocketSendUtils.clearRunResult(packet.getApplicationName(), packet.getOffsetPath());
            }
        };
    }
}
