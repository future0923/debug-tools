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
package io.github.future0923.debug.tools.idea.client.socket;

import io.github.future0923.debug.tools.client.netty.dispatcher.ClientNettyPacketDispatcher;
import io.github.future0923.debug.tools.client.netty.handler.HeartBeatResponseHandler;
import io.github.future0923.debug.tools.common.protocal.packet.response.HeartBeatResponsePacket;
import io.github.future0923.debug.tools.common.protocal.packet.response.HotDeployResponsePacket;
import io.github.future0923.debug.tools.common.protocal.packet.response.RunGroovyScriptResponsePacket;
import io.github.future0923.debug.tools.common.protocal.packet.response.RunTargetMethodResponsePacket;
import io.github.future0923.debug.tools.idea.client.socket.handler.HotDeployResponsePacketHandler;
import io.github.future0923.debug.tools.idea.client.socket.handler.RunGroovyScriptResponseHandler;
import io.github.future0923.debug.tools.idea.client.socket.handler.RunTargetMethodResponseHandler;

public final class ClientDispatcherFactory {

    private ClientDispatcherFactory() {}

    public static ClientNettyPacketDispatcher createDefault() {
        ClientNettyPacketDispatcher d = new ClientNettyPacketDispatcher();
        d.register(HeartBeatResponsePacket.class, HeartBeatResponseHandler.INSTANCE);
        d.register(RunTargetMethodResponsePacket.class, RunTargetMethodResponseHandler.INSTANCE);
        d.register(RunGroovyScriptResponsePacket.class, RunGroovyScriptResponseHandler.INSTANCE);
        d.register(HotDeployResponsePacket.class, HotDeployResponsePacketHandler.INSTANCE);
        return d;
    }
}
