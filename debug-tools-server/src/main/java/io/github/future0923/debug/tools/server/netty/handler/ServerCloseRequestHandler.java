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

import io.github.future0923.debug.tools.common.handler.PacketHandler;
import io.github.future0923.debug.tools.common.protocal.packet.request.ServerCloseRequestPacket;
import io.github.future0923.debug.tools.server.DebugToolsBootstrap;
import io.netty.channel.ChannelHandlerContext;

/**
 * 服务端关闭请求处理器
 *
 * @author future0923
 */
public class ServerCloseRequestHandler implements PacketHandler<ServerCloseRequestPacket> {

    public static final ServerCloseRequestHandler INSTANCE = new ServerCloseRequestHandler();

    private ServerCloseRequestHandler() {}

    @Override
    public void handle(ChannelHandlerContext ctx, ServerCloseRequestPacket packet) {
        ctx.close();
        DebugToolsBootstrap.INSTANCE.stop();
    }
}
