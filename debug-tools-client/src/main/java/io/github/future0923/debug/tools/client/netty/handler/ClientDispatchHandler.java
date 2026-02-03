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
package io.github.future0923.debug.tools.client.netty.handler;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.client.netty.dispatcher.ClientNettyPacketDispatcher;
import io.github.future0923.debug.tools.common.protocal.packet.Packet;
import io.github.future0923.debug.tools.common.protocal.packet.request.HeartBeatRequestPacket;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public final class ClientDispatchHandler extends SimpleChannelInboundHandler<Packet> {

    private static final Logger logger = Logger.getLogger(ClientDispatchHandler.class);

    private final ClientNettyPacketDispatcher dispatcher;

    public ClientDispatchHandler(ClientNettyPacketDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        logger.info("Netty client channel active: {}", ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        logger.warning("Netty client channel inactive: {}", ctx.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet packet) throws Exception {
        dispatcher.dispatch(ctx, packet);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.WRITER_IDLE) {
                // 客户端：写空闲 -> 发心跳请求
                ctx.writeAndFlush(HeartBeatRequestPacket.INSTANCE)
                        .addListener((ChannelFutureListener) f -> {
                            if (!f.isSuccess()) ctx.close();
                        });
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.warning("client exception, close channel: {}", cause);
        ctx.close();
    }
}
