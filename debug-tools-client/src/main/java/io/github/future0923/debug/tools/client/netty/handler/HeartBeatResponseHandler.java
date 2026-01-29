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
import io.github.future0923.debug.tools.common.handler.NettyPacketHandler;
import io.github.future0923.debug.tools.common.protocal.packet.response.HeartBeatResponsePacket;
import io.netty.channel.ChannelHandlerContext;

public final class HeartBeatResponseHandler implements NettyPacketHandler<HeartBeatResponsePacket> {

    private static final Logger logger = Logger.getLogger(HeartBeatResponseHandler.class);

    public static final HeartBeatResponseHandler INSTANCE = new HeartBeatResponseHandler();

    private HeartBeatResponseHandler() {}

    @Override
    public void handle(ChannelHandlerContext ctx, HeartBeatResponsePacket packet) {
        // 可选：记录、更新状态、统计 RTT 等
        logger.debug("heartbeat response ok: {}", packet);
    }
}
