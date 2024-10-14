package io.github.future0923.debug.tools.idea.client.socket.utils;

import io.github.future0923.debug.tools.base.utils.DebugToolsStringUtils;
import io.github.future0923.debug.tools.common.protocal.packet.request.ClearRunResultRequestPacket;
import io.github.future0923.debug.tools.idea.client.ApplicationProjectHolder;

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
}
