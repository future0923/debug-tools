package io.github.future0923.debug.power.common.handler;

import io.github.future0923.debug.power.base.logging.Logger;
import io.github.future0923.debug.power.common.protocal.packet.Packet;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 定义数据由哪个 PacketHandler 处理
 *
 * @author future0923
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public abstract class PacketHandleService {

    private static final Logger logger = Logger.getLogger(PacketHandleService.class);

    private final Map<Class<? extends Packet>, PacketHandler> classPacketHandlerMap = new HashMap<>();

    public void register(Class<? extends Packet> clazz, PacketHandler packetHandler) {
        classPacketHandlerMap.put(clazz, packetHandler);
    }

    public void handle(OutputStream outputStream, Packet packet) {
        PacketHandler packetHandler = classPacketHandlerMap.get(packet.getClass());
        if (packetHandler != null) {
            try {
                packetHandler.handle(outputStream, packet);
            } catch (Exception e) {
                logger.error("{}} packet {} happen error", e, packetHandler.getClass().getSimpleName(), packet);
            }
        }
    }
}
