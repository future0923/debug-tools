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

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import io.github.future0923.debug.tools.base.utils.DebugToolsStringUtils;
import io.github.future0923.debug.tools.common.exception.SocketCloseException;
import io.github.future0923.debug.tools.common.protocal.packet.Packet;
import io.github.future0923.debug.tools.common.protocal.packet.request.ClearRunResultRequestPacket;
import io.github.future0923.debug.tools.idea.client.ApplicationProjectHolder;
import io.github.future0923.debug.tools.idea.tool.DebugToolsToolWindowFactory;

/**
 * @author future0923
 */
public class SocketSendUtils {

    public static void clearRunResult(String applicationName, String filedOffset, String traceOffset) {
        if (DebugToolsStringUtils.isNotBlank(filedOffset)) {
            ApplicationProjectHolder.Info info = ApplicationProjectHolder.getInfo(applicationName);
            if (info != null) {
                try {
                    info.getClient().getHolder().send(new ClearRunResultRequestPacket(filedOffset, traceOffset));
                } catch (Exception ignored) {
                }
            }
        }
    }

    public static void send(Project project, Packet packet) {
        send(project, packet, null);
    }

    public static void send(Project project, Packet packet, Runnable runnable) {
        ApplicationProjectHolder.Info info = ApplicationProjectHolder.getInfo(project);
        if (info == null) {
            Messages.showErrorDialog("Run attach first", "Send Error");
            DebugToolsToolWindowFactory.showWindow(project, null);
            return;
        }
        try {
            info.getClient().getHolder().send(packet);
            if (runnable != null) {
                runnable.run();
            }
        } catch (SocketCloseException e) {
            Messages.showErrorDialog("Socket close", "Send Error");
            DebugToolsToolWindowFactory.showWindow(project, null);
        } catch (Exception e) {
            Messages.showErrorDialog("Socket send error " + e.getMessage(), "Send Error");
            DebugToolsToolWindowFactory.showWindow(project, null);
        }
    }
}
