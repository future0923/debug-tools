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
package io.github.future0923.debug.tools.client.netty.dispatcher;

import io.github.future0923.debug.tools.common.handler.NettyPacketHandler;
import io.github.future0923.debug.tools.common.protocal.packet.Packet;
import io.netty.channel.ChannelHandlerContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ClientNettyPacketDispatcher {

    private final Map<Class<? extends Packet>, NettyPacketHandler<?>> handlers = new ConcurrentHashMap<>();

    public <P extends Packet> ClientNettyPacketDispatcher register(Class<P> type, NettyPacketHandler<P> handler) {
        handlers.put(type, handler);
        return this;
    }

    @SuppressWarnings("unchecked")
    public void dispatch(ChannelHandlerContext ctx, Packet packet) throws Exception {
        NettyPacketHandler<Packet> handler = (NettyPacketHandler<Packet>) handlers.get(packet.getClass());
        if (handler == null) {
            // 不强制 close，允许客户端忽略未知响应（更稳）
            return;
        }
        handler.handle(ctx, packet);
    }
}
