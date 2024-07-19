package io.github.future0923.debug.power.idea.client;

import io.github.future0923.debug.power.client.handler.ClientPacketHandleService;
import io.github.future0923.debug.power.common.protocal.packet.response.RunTargetMethodResponsePacket;
import io.github.future0923.debug.power.idea.client.handler.RunTargetMethodResponseHandler;

/**
 * @author future0923
 */
public class IdeaPacketHandleService extends ClientPacketHandleService {

    public static final IdeaPacketHandleService INSTANCE = new IdeaPacketHandleService();

    private IdeaPacketHandleService() {
        register(RunTargetMethodResponsePacket.class, RunTargetMethodResponseHandler.INSTANCE);
    }
}
