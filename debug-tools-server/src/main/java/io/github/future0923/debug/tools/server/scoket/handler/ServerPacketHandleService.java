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
package io.github.future0923.debug.tools.server.scoket.handler;

import io.github.future0923.debug.tools.common.handler.PacketHandleService;
import io.github.future0923.debug.tools.common.protocal.packet.request.ClearRunResultRequestPacket;
import io.github.future0923.debug.tools.common.protocal.packet.request.RemoteCompilerHotDeployRequestPacket;
import io.github.future0923.debug.tools.common.protocal.packet.request.HeartBeatRequestPacket;
import io.github.future0923.debug.tools.common.protocal.packet.request.LocalCompilerHotDeployRequestPacket;
import io.github.future0923.debug.tools.common.protocal.packet.request.RunGroovyScriptRequestPacket;
import io.github.future0923.debug.tools.common.protocal.packet.request.RunTargetMethodRequestPacket;
import io.github.future0923.debug.tools.common.protocal.packet.request.ServerCloseRequestPacket;

/**
 * 定义数据由哪个 PacketHandler 处理
 *
 * @author future0923
 */
public class ServerPacketHandleService extends PacketHandleService {

    public ServerPacketHandleService() {
        register(HeartBeatRequestPacket.class, HeartBeatRequestHandler.INSTANCE);
        register(ServerCloseRequestPacket.class, ServerCloseRequestHandler.INSTANCE);
        register(RunTargetMethodRequestPacket.class, RunTargetMethodRequestHandler.INSTANCE);
        register(ClearRunResultRequestPacket.class, ClearRunResultRequestHandler.INSTANCE);
        register(RunGroovyScriptRequestPacket.class, RunGroovyScriptRequestHandler.INSTANCE);
        register(LocalCompilerHotDeployRequestPacket.class, LocalCompilerHotDeployRequestHandler.INSTANCE);
        register(RemoteCompilerHotDeployRequestPacket.class, RemoteCompilerHotDeployRequestHandler.INSTANCE);
    }
}
