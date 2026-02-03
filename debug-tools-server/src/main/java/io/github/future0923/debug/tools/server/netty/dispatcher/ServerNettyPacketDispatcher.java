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

import io.github.future0923.debug.tools.common.protocal.packet.Packet;
import io.netty.channel.ChannelHandlerContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 【最终版】Netty Packet 分发器
 * - 只支持 Netty
 * - 只认 ChannelHandlerContext
 * - 只分发 Packet -> Handler
 */
public final class ServerNettyPacketDispatcher {

    private final Map<Class<? extends Packet>, io.github.future0923.debug.tools.common.handler.NettyPacketHandler<?>> handlers =
            new ConcurrentHashMap<>();

    public <P extends Packet> void register(
            Class<P> packetType,
            io.github.future0923.debug.tools.common.handler.NettyPacketHandler<P> handler
    ) {
        handlers.put(packetType, handler);
    }

    @SuppressWarnings("unchecked")
    public void dispatch(ChannelHandlerContext ctx, Packet packet) throws Exception {
        io.github.future0923.debug.tools.common.handler.NettyPacketHandler<Packet> handler =
                (io.github.future0923.debug.tools.common.handler.NettyPacketHandler<Packet>) handlers.get(packet.getClass());

        if (handler == null) {
            throw new IllegalStateException(
                    "No handler for packet type: " + packet.getClass().getName()
            );
        }

        handler.handle(ctx, packet);
    }
}
