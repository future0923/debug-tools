package io.github.future0923.debug.tools.idea.client.socket.handler;

import com.intellij.ide.impl.ProjectUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import io.github.future0923.debug.tools.common.handler.BasePacketHandler;
import io.github.future0923.debug.tools.common.protocal.packet.response.RunTargetMethodResponsePacket;
import io.github.future0923.debug.tools.idea.client.ApplicationProjectHolder;
import io.github.future0923.debug.tools.idea.ui.main.ResponseDialog;

import java.io.OutputStream;

/**
 * @author future0923
 */
public class RunTargetMethodResponseHandler extends BasePacketHandler<RunTargetMethodResponsePacket> {

    public static final RunTargetMethodResponseHandler INSTANCE = new RunTargetMethodResponseHandler();

    private RunTargetMethodResponseHandler() {
    }

    @Override
    public void handle(OutputStream outputStream, RunTargetMethodResponsePacket packet) throws Exception {
        ApplicationManager.getApplication().invokeLater(() -> {
            ApplicationProjectHolder.Info info = ApplicationProjectHolder.getInfo(packet.getApplicationName());
            Project project;
            if (info != null && info.getProject() != null) {
                project = info.getProject();
            } else {
                project = ProjectUtil.getActiveProject();
            }
            ResponseDialog dialog = new ResponseDialog(project, packet);
            dialog.show();
        });

    }
}
