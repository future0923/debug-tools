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

    public static void clearRunResult(String applicationName, String filedOffset) {
        if (DebugToolsStringUtils.isNotBlank(filedOffset)) {
            ApplicationProjectHolder.Info info = ApplicationProjectHolder.getInfo(applicationName);
            if (info != null) {
                try {
                    info.getClient().getHolder().send(new ClearRunResultRequestPacket(filedOffset));
                } catch (Exception ignored) {
                }
            }
        }
    }

    public static void send(Project project, Packet packet) {
        ApplicationProjectHolder.Info info = ApplicationProjectHolder.getInfo(project);
        if (info == null) {
            Messages.showErrorDialog("Run attach first", "Send Error");
            DebugToolsToolWindowFactory.showWindow(project, null);
            return;
        }
        try {
            info.getClient().getHolder().send(packet);
        } catch (SocketCloseException e) {
            Messages.showErrorDialog("Socket close", "Send Error");
            DebugToolsToolWindowFactory.showWindow(project, null);
        } catch (Exception e) {
            Messages.showErrorDialog("Socket send error " + e.getMessage(), "Send Error");
            DebugToolsToolWindowFactory.showWindow(project, null);
        }
    }
}
