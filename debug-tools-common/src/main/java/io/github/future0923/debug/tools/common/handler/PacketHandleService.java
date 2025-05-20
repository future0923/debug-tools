/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
