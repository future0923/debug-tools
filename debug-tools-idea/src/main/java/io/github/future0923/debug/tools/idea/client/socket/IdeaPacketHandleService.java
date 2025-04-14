package io.github.future0923.debug.tools.idea.client.socket;

import io.github.future0923.debug.tools.client.handler.ClientPacketHandleService;
import io.github.future0923.debug.tools.common.protocal.packet.response.RemoteCompilerResponsePacket;
import io.github.future0923.debug.tools.common.protocal.packet.response.RunGroovyScriptResponsePacket;
import io.github.future0923.debug.tools.common.protocal.packet.response.RunTargetMethodResponsePacket;
import io.github.future0923.debug.tools.idea.client.socket.handler.RemoteCompilerResponseHandler;
import io.github.future0923.debug.tools.idea.client.socket.handler.RunGroovyScriptResponseHandler;
import io.github.future0923.debug.tools.idea.client.socket.handler.RunTargetMethodResponseHandler;

/**
 * @author future0923
 */
public class IdeaPacketHandleService extends ClientPacketHandleService {

    public static final IdeaPacketHandleService INSTANCE = new IdeaPacketHandleService();

    private IdeaPacketHandleService() {
        register(RunTargetMethodResponsePacket.class, RunTargetMethodResponseHandler.INSTANCE);
        register(RunGroovyScriptResponsePacket.class, RunGroovyScriptResponseHandler.INSTANCE);
        register(RemoteCompilerResponsePacket.class, RemoteCompilerResponseHandler.INSTANCE);
    }
}
