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

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.common.protocal.packet.Packet;
import io.github.future0923.debug.tools.common.protocal.packet.response.HeartBeatResponsePacket;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.util.concurrent.ExecutorService;

/**
 * 接受到消息并分发处理器
 *
 * @author future0923
 */
public final class ServerDispatchHandler extends SimpleChannelInboundHandler<Packet> {

    private static final Logger logger = Logger.getLogger(ServerDispatchHandler.class);

    /**
     * 业务线程池
     */
    private final ExecutorService bizPool;

    /**
     * 分发器
     */
    private final ServerPacketDispatcher dispatcher;

    public ServerDispatchHandler(ExecutorService bizPool, ServerPacketDispatcher dispatcher) {
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
                logger.warning("biz handle error", t);
            }
        });
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        // 空闲事件
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.READER_IDLE) {
                // Idle 探活：不维护假连接
                ctx.writeAndFlush(new HeartBeatResponsePacket()).addListener((ChannelFutureListener) f -> {
                    if (!f.isSuccess()) {
                        ctx.close();
                    }
                });
            }
        }
    }
}
