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
package io.github.future0923.debug.tools.idea.ui.main;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import io.github.future0923.debug.tools.common.protocal.packet.response.RunTargetMethodResponsePacket;
import io.github.future0923.debug.tools.idea.bundle.DebugToolsBundle;
import io.github.future0923.debug.tools.idea.client.socket.utils.SocketSendUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author future0923
 */
public class ResponseDialog extends DialogWrapper {

    private final RunTargetMethodResponsePacket packet;

    private final Project project;

    public ResponseDialog(Project project, RunTargetMethodResponsePacket packet) {
        super(project, true, IdeModalityType.MODELESS);
        this.packet = packet;
        this.project = project;
        setTitle(DebugToolsBundle.message("response.dialog.title"));
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return new ResponsePanel(project, packet);
    }

    @Override
    protected Action @NotNull [] createActions() {
        return new Action[]{getOKAction()};
    }

    @Override
    protected void dispose() {
        super.dispose();
        close();
    }

    private void close() {
        if (packet.isSuccess()) {
            SocketSendUtils.clearRunResult(packet.getApplicationName(), packet.getOffsetPath(), packet.getTraceOffsetPath());
        }
    }
}
