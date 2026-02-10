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

import io.github.future0923.debug.tools.common.protocal.packet.request.ChangeTraceMethodRequestPacket;
import io.github.future0923.debug.tools.common.protocal.packet.request.ClearRunResultRequestPacket;
import io.github.future0923.debug.tools.common.protocal.packet.request.HeartBeatRequestPacket;
import io.github.future0923.debug.tools.common.protocal.packet.request.LocalCompilerHotDeployRequestPacket;
import io.github.future0923.debug.tools.common.protocal.packet.request.RemoteCompilerHotDeployRequestPacket;
import io.github.future0923.debug.tools.common.protocal.packet.request.ResourceHotDeployRequestPacket;
import io.github.future0923.debug.tools.common.protocal.packet.request.RunGroovyScriptRequestPacket;
import io.github.future0923.debug.tools.common.protocal.packet.request.RunTargetMethodRequestPacket;
import io.github.future0923.debug.tools.common.protocal.packet.request.ServerCloseRequestPacket;
import io.github.future0923.debug.tools.server.netty.handler.ChangeTraceMethodRequestHandler;
import io.github.future0923.debug.tools.server.netty.handler.ClearRunResultRequestHandler;
import io.github.future0923.debug.tools.server.netty.handler.HeartBeatRequestHandler;
import io.github.future0923.debug.tools.server.netty.handler.LocalCompilerHotDeployRequestHandler;
import io.github.future0923.debug.tools.server.netty.handler.RemoteCompilerHotDeployRequestHandler;
import io.github.future0923.debug.tools.server.netty.handler.ResourceHotDeployRequestHandler;
import io.github.future0923.debug.tools.server.netty.handler.RunGroovyScriptRequestHandler;
import io.github.future0923.debug.tools.server.netty.handler.ServerCloseRequestHandler;
import io.github.future0923.debug.tools.server.netty.handler.RunTargetMethodRequestHandler;

/**
 * 分发工厂
 *
 * @author future0923
 */
public final class ServerDispatcherFactory {

    /**
     * 创建分发器
     *
     * @return 分发器
     */
    public static ServerPacketDispatcher create() {
        ServerPacketDispatcher dispatcher = new ServerPacketDispatcher();
        dispatcher.register(HeartBeatRequestPacket.class, HeartBeatRequestHandler.INSTANCE);
        dispatcher.register(ServerCloseRequestPacket.class, ServerCloseRequestHandler.INSTANCE);
        dispatcher.register(RunTargetMethodRequestPacket.class, RunTargetMethodRequestHandler.INSTANCE);
        dispatcher.register(ClearRunResultRequestPacket.class, ClearRunResultRequestHandler.INSTANCE);
        dispatcher.register(RunGroovyScriptRequestPacket.class, RunGroovyScriptRequestHandler.INSTANCE);
        dispatcher.register(LocalCompilerHotDeployRequestPacket.class, LocalCompilerHotDeployRequestHandler.INSTANCE);
        dispatcher.register(RemoteCompilerHotDeployRequestPacket.class, RemoteCompilerHotDeployRequestHandler.INSTANCE);
        dispatcher.register(ResourceHotDeployRequestPacket.class, ResourceHotDeployRequestHandler.INSTANCE);
        dispatcher.register(ChangeTraceMethodRequestPacket.class, ChangeTraceMethodRequestHandler.INSTANCE);
        return dispatcher;
    }
}
