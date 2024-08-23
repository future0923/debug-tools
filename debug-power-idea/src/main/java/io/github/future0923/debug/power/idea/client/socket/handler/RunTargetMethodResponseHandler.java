package io.github.future0923.debug.power.idea.client.socket.handler;

import com.intellij.openapi.application.ApplicationManager;
import io.github.future0923.debug.power.common.handler.BasePacketHandler;
import io.github.future0923.debug.power.common.protocal.packet.response.RunTargetMethodResponsePacket;
import io.github.future0923.debug.power.idea.ui.main.ResponseDialog;

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
            ResponseDialog dialog = new ResponseDialog(packet);
            dialog.show();
        });

    }
}
