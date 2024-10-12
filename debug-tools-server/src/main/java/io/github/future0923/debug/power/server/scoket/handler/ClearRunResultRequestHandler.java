package io.github.future0923.debug.power.server.scoket.handler;

import io.github.future0923.debug.power.common.handler.BasePacketHandler;
import io.github.future0923.debug.power.common.protocal.packet.request.ClearRunResultRequestPacket;
import io.github.future0923.debug.power.server.utils.DebugPowerResultUtils;

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
        DebugPowerResultUtils.removeCache(packet.getFieldOffset());
    }
}
