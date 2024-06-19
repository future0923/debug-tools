package io.github.future0923.debug.power.client.handler;

import io.github.future0923.debug.power.common.handler.BasePacketHandler;
import io.github.future0923.debug.power.common.protocal.packet.response.RunTargetMethodResponsePacket;

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
        System.out.println(packet.getPrintResult());
    }
}
