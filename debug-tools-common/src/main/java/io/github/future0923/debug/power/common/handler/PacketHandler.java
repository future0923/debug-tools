package io.github.future0923.debug.power.common.handler;

import io.github.future0923.debug.power.common.protocal.packet.Packet;

import java.io.OutputStream;

/**
 * 指定命令的请求处理，要加入到对应的{@link PacketHandleService}中
 *
 * @author future0923
 */
public interface PacketHandler<T extends Packet> {

    void handle(OutputStream outputStream, T packet) throws Exception;
}
