package io.github.future0923.debug.power.idea.client.socket.utils;

import io.github.future0923.debug.power.base.utils.DebugPowerStringUtils;
import io.github.future0923.debug.power.common.protocal.packet.request.ClearRunResultRequestPacket;
import io.github.future0923.debug.power.idea.client.ApplicationProjectHolder;

/**
 * @author future0923
 */
public class SocketSendUtils {

    public static void clearRunResult(String applicationName, String filedOffset) {
        if (DebugPowerStringUtils.isNotBlank(filedOffset)) {
            ApplicationProjectHolder.Info info = ApplicationProjectHolder.getInfo(applicationName);
            if (info != null) {
                try {
                    info.getClient().getHolder().send(new ClearRunResultRequestPacket(filedOffset));
                } catch (Exception ignored) {
                }
            }
        }
    }
}
