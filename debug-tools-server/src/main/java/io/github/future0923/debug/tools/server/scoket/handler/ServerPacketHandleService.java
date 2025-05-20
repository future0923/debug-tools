/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
