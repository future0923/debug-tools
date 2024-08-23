package io.github.future0923.debug.power.idea.client.socket.handler;

import com.intellij.openapi.project.Project;
import io.github.future0923.debug.power.base.utils.DebugPowerStringUtils;
import io.github.future0923.debug.power.common.handler.BasePacketHandler;
import io.github.future0923.debug.power.common.protocal.packet.response.ConnectSuccessResponsePacket;
import io.github.future0923.debug.power.idea.client.ApplicationProjectHolder;
import io.github.future0923.debug.power.idea.setting.DebugPowerSettingState;

import java.io.OutputStream;

/**
 * @author future0923
 */
public class ConnectSuccessResponseHandler extends BasePacketHandler<ConnectSuccessResponsePacket> {

    public static final ConnectSuccessResponseHandler INSTANCE = new ConnectSuccessResponseHandler();

    private ConnectSuccessResponseHandler() {

    }

    @Override
    public void handle(OutputStream outputStream, ConnectSuccessResponsePacket packet) throws Exception {
        if (DebugPowerStringUtils.isNotBlank(packet.getApplicationName()) && packet.getHttpListenPort() != null) {
            ApplicationProjectHolder.Info info = ApplicationProjectHolder.getInfo(packet.getApplicationName());
            if (info != null) {
                Project project = info.getProject();
                if (project != null) {
                    DebugPowerSettingState settingState = DebugPowerSettingState.getInstance(project);
                    settingState.setHttpPort(packet.getHttpListenPort());
                }
            }
        }
    }
}
