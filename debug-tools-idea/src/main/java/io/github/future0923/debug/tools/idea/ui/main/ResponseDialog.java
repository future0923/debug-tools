package io.github.future0923.debug.tools.idea.ui.main;

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

    public ResponseDialog(RunTargetMethodResponsePacket packet) {
        super(null, true, IdeModalityType.MODELESS);
        this.packet = packet;
        setTitle("Run Result");
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return new ResponsePanel(packet);
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
