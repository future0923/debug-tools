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
package io.github.future0923.debug.tools.server.netty.dispatcher;

import io.github.future0923.debug.tools.common.handler.PacketHandler;
import io.github.future0923.debug.tools.common.protocal.packet.Packet;
import io.netty.channel.ChannelHandlerContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务端消息分发器
 *
 * @author future0923
 */
public final class ServerPacketDispatcher {

    /**
     * 消息处理器映射
     */
    private final Map<Class<? extends Packet>, PacketHandler<?>> handlers = new ConcurrentHashMap<>();

    /**
     * 注册消息处理器
     *
     * @param packetType 消息类型
     * @param handler    消息处理器
     */
    public <P extends Packet> void register(Class<P> packetType, PacketHandler<P> handler) {
        handlers.put(packetType, handler);
    }

    /**
     * 分发消息
     *
     * @param ctx   通道处理器上下文
     * @param packet 消息
     */
    @SuppressWarnings("unchecked")
    public void dispatch(ChannelHandlerContext ctx, Packet packet) throws Exception {
        PacketHandler<Packet> handler = (PacketHandler<Packet>) handlers.get(packet.getClass());
        if (handler == null) {
            throw new IllegalStateException(
                    "No handler for packet type: " + packet.getClass().getName()
            );
        }
        handler.handle(ctx, packet);
    }
}
