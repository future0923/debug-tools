package io.github.future0923.debug.tools.server.scoket.handler;

import io.github.future0923.debug.tools.common.handler.BasePacketHandler;
import io.github.future0923.debug.tools.common.protocal.packet.request.ClearRunResultRequestPacket;
import io.github.future0923.debug.tools.server.utils.DebugToolsResultUtils;

import java.io.OutputStream;

/**
 * @author future0923
 */
public class ClearRunResultRequestHandler extends BasePacketHandler<ClearRunResultRequestPacket> {

    public static final ClearRunResultRequestHandler INSTANCE = new ClearRunResultRequestHandler();

    private ClearRunResultRequestHandler() {

    }

    @Override
    public void handle(OutputStream outputStream, ClearRunResultRequestPacket packet) throws Exception {
        DebugToolsResultUtils.removeCache(packet.getFieldOffset());
    }
}
