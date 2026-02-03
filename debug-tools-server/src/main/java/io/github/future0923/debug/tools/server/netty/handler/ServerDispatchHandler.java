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
package io.github.future0923.debug.tools.server.netty.handler;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.common.protocal.packet.Packet;
import io.github.future0923.debug.tools.common.protocal.packet.response.HeartBeatResponsePacket;
import io.github.future0923.debug.tools.server.netty.dispatcher.ServerNettyPacketDispatcher;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.util.concurrent.ExecutorService;

/**
 * 【最终版】Netty Server 分发 Handler
 * - 只负责 IO -> 业务投递
 * - 不兼容 BIO
 * - 不出现 OutputStream
 */
public final class ServerDispatchHandler extends SimpleChannelInboundHandler<Packet> {

    private static final Logger logger = Logger.getLogger(ServerDispatchHandler.class);

    private final ExecutorService bizPool;

    private final ServerNettyPacketDispatcher dispatcher;

    public ServerDispatchHandler(ExecutorService bizPool, ServerNettyPacketDispatcher dispatcher) {
        this.bizPool = bizPool;
        this.dispatcher = dispatcher;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet packet) {
        // IO 线程只做分发，不做业务
        bizPool.submit(() -> {
            try {
                dispatcher.dispatch(ctx, packet);
            } catch (Throwable t) {
                logger.warning("biz handle error, close channel: {}", t);
                ctx.close();
            }
        });
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.READER_IDLE) {
                // Idle 探活：不维护假连接
                ctx.writeAndFlush(new HeartBeatResponsePacket())
                        .addListener((ChannelFutureListener) f -> {
                            if (!f.isSuccess()) ctx.close();
                        });
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.warning("channel exception, close: {}", cause);
        ctx.close();
    }
}
