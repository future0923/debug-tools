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

import io.github.future0923.debug.tools.base.hutool.core.util.BooleanUtil;
import io.github.future0923.debug.tools.common.protocal.packet.request.ChangeTraceMethodRequestPacket;
import io.github.future0923.debug.tools.common.handler.NettyPacketHandler;
import io.github.future0923.debug.tools.server.trace.TraceMethodClassFileTransformer;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author future0923
 */
public final class ChangeTraceMethodRequestHandler implements NettyPacketHandler<ChangeTraceMethodRequestPacket> {

    public static final ChangeTraceMethodRequestHandler INSTANCE = new ChangeTraceMethodRequestHandler();

    private ChangeTraceMethodRequestHandler() {

    }

    @Override
    public void handle(ChannelHandlerContext ctx,
                       ChangeTraceMethodRequestPacket packet) throws Exception {
        if (BooleanUtil.isTrue(packet.getTrace())) {
            TraceMethodClassFileTransformer.traceMethod(packet.getClassName(), packet.getMethodName(), packet.getMethodDescription());
        } else {
            TraceMethodClassFileTransformer.cancelTraceMethod(packet.getClassName(), packet.getMethodName(), packet.getMethodDescription());
        }
    }
}
