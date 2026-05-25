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
import io.github.future0923.debug.tools.common.handler.PacketHandler;
import io.github.future0923.debug.tools.common.protocal.packet.request.CancelRunTargetMethodStreamRequestPacket;
import io.netty.channel.ChannelHandlerContext;

/**
 * 取消正在运行的 Flux/ServerSentEvent 订阅。
 *
 * @author future0923
 */
public class CancelRunTargetMethodStreamRequestHandler implements PacketHandler<CancelRunTargetMethodStreamRequestPacket> {

    private static final Logger logger = Logger.getLogger(CancelRunTargetMethodStreamRequestHandler.class);

    public static final CancelRunTargetMethodStreamRequestHandler INSTANCE = new CancelRunTargetMethodStreamRequestHandler();

    @Override
    public void handle(ChannelHandlerContext ctx, CancelRunTargetMethodStreamRequestPacket packet) {
        boolean cancelled = RunTargetMethodRequestHandler.INSTANCE.cancelStream(packet.getIdentity());
        if (!cancelled) {
            logger.debug("stream subscription not found, identity={}", packet.getIdentity());
        }
    }
}
