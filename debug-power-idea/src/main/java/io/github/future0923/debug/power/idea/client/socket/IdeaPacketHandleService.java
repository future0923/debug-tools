package io.github.future0923.debug.power.idea.client.socket;

import io.github.future0923.debug.power.client.handler.ClientPacketHandleService;
import io.github.future0923.debug.power.common.protocal.packet.response.ConnectSuccessResponsePacket;
import io.github.future0923.debug.power.common.protocal.packet.response.RunGroovyScriptResponsePacket;
import io.github.future0923.debug.power.common.protocal.packet.response.RunTargetMethodResponsePacket;
import io.github.future0923.debug.power.idea.client.socket.handler.ConnectSuccessResponseHandler;
import io.github.future0923.debug.power.idea.client.socket.handler.RunGroovyScriptResponseHandler;
import io.github.future0923.debug.power.idea.client.socket.handler.RunTargetMethodResponseHandler;

/**
 * @author future0923
 */
public class IdeaPacketHandleService extends ClientPacketHandleService {

    public static final IdeaPacketHandleService INSTANCE = new IdeaPacketHandleService();

    private IdeaPacketHandleService() {
        register(RunTargetMethodResponsePacket.class, RunTargetMethodResponseHandler.INSTANCE);
        register(ConnectSuccessResponsePacket.class, ConnectSuccessResponseHandler.INSTANCE);
        register(RunGroovyScriptResponsePacket.class, RunGroovyScriptResponseHandler.INSTANCE);
    }
}
