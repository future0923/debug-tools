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
package io.github.future0923.debug.tools.idea.client.socket.utils;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import io.github.future0923.debug.tools.base.utils.DebugToolsStringUtils;
import io.github.future0923.debug.tools.common.protocal.packet.Packet;
import io.github.future0923.debug.tools.common.protocal.packet.request.ClearRunResultRequestPacket;
import io.github.future0923.debug.tools.idea.client.ApplicationProjectHolder;
import io.github.future0923.debug.tools.idea.tool.DebugToolsToolWindowFactory;
import org.jetbrains.annotations.Nullable;

/**
 * @author future0923
 */
public class SocketSendUtils {

    public static void clearRunResult(String applicationName, String filedOffset, String traceOffset) {
        if (DebugToolsStringUtils.isNotBlank(filedOffset)) {
            ApplicationProjectHolder.Info info = ApplicationProjectHolder.getInfo(applicationName);
            if (info != null) {
                try {
                    info.getClient().send(new ClearRunResultRequestPacket(filedOffset, traceOffset));
                } catch (Exception ignored) {
                }
            }
        }
    }

    public static void sendAsync(Project project, Packet packet) {
        sendAsync(project, packet, null);
    }

    public static void sendThrowException(Project project, Packet packet) throws NullPointerException {
        sendThrowException(project, packet, null);
    }

    public static void sendThrowException(Project project, Packet packet, Runnable runnable) throws NullPointerException {
        ApplicationProjectHolder.Info info = ApplicationProjectHolder.getInfo(project);
        if (info == null) {
            throw new NullPointerException("Project not attach");
        }
        info.getClient().send(packet);
        if (runnable != null) {
            runnable.run();
        }
    }

    public static void sendAsync(Project project, Packet packet, @Nullable Runnable successUiCallback) {
        Application app = ApplicationManager.getApplication();
        app.executeOnPooledThread(() -> {
            ApplicationProjectHolder.Info info = ApplicationProjectHolder.getInfo(project);
            if (info == null) {
                // 回到 UI 线程
                app.invokeLater(() -> {
                    Messages.showErrorDialog(project, "Run attach first", "Send Error");
                    DebugToolsToolWindowFactory.showWindow(project, null);
                });
                return;
            }
            try {
                info.getClient().send(packet);
                if (successUiCallback != null) {
                    app.invokeLater(successUiCallback);
                }
            } catch (Exception e) {
                app.invokeLater(() -> {
                    Messages.showErrorDialog(
                            project,
                            "Socket send error: " + e.getMessage(),
                            "Send Error"
                    );
                    DebugToolsToolWindowFactory.showWindow(project, null);
                });
            }
        });
    }

}
