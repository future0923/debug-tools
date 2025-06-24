/*
 * Copyright (C) 2024-2025 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package io.github.future0923.debug.tools.common.handler;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.common.protocal.packet.Packet;

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
