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
package io.github.future0923.debug.tools.idea.ui.main;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import io.github.future0923.debug.tools.common.protocal.packet.response.RunTargetMethodResponsePacket;
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
        setTitle("Run Result");
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
            SocketSendUtils.clearRunResult(packet.getApplicationName(), packet.getOffsetPath());
        }
    }
}
