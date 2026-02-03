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
package io.github.future0923.debug.tools.idea.client.socket.handler;

import com.intellij.execution.Executor;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.execution.ui.RunContentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowManager;
import io.github.future0923.debug.tools.common.handler.NettyPacketHandler;
import io.github.future0923.debug.tools.common.protocal.packet.response.RunGroovyScriptResponsePacket;
import io.github.future0923.debug.tools.idea.client.ApplicationProjectHolder;
import io.github.future0923.debug.tools.idea.client.socket.utils.SocketSendUtils;
import io.github.future0923.debug.tools.idea.ui.tab.ExceptionTabbedPane;
import io.github.future0923.debug.tools.idea.ui.tab.GroovyResult;
import io.github.future0923.debug.tools.idea.utils.DebugToolsIcons;
import io.github.future0923.debug.tools.idea.utils.DebugToolsNotifierUtil;
import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @author future0923
 */
public class RunGroovyScriptResponseHandler implements NettyPacketHandler<RunGroovyScriptResponsePacket> {

    public static final RunGroovyScriptResponseHandler INSTANCE = new RunGroovyScriptResponseHandler();

    private RunGroovyScriptResponseHandler() {

    }

    @Override
    public void handle(ChannelHandlerContext ctx, RunGroovyScriptResponsePacket packet) throws Exception {
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
                SocketSendUtils.clearRunResult(packet.getApplicationName(), packet.getOffsetPath(), null);
            }
        };
    }
}
