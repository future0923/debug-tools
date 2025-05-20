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
package io.github.future0923.debug.tools.idea.client.socket;

import io.github.future0923.debug.tools.client.handler.ClientPacketHandleService;
import io.github.future0923.debug.tools.common.protocal.packet.response.HotDeployResponsePacket;
import io.github.future0923.debug.tools.common.protocal.packet.response.RunGroovyScriptResponsePacket;
import io.github.future0923.debug.tools.common.protocal.packet.response.RunTargetMethodResponsePacket;
import io.github.future0923.debug.tools.idea.client.socket.handler.HotDeployResponsePacketHandler;
import io.github.future0923.debug.tools.idea.client.socket.handler.RunGroovyScriptResponseHandler;
import io.github.future0923.debug.tools.idea.client.socket.handler.RunTargetMethodResponseHandler;

/**
 * @author future0923
 */
public class IdeaPacketHandleService extends ClientPacketHandleService {

    public static final IdeaPacketHandleService INSTANCE = new IdeaPacketHandleService();

    private IdeaPacketHandleService() {
        register(RunTargetMethodResponsePacket.class, RunTargetMethodResponseHandler.INSTANCE);
        register(RunGroovyScriptResponsePacket.class, RunGroovyScriptResponseHandler.INSTANCE);
        register(HotDeployResponsePacket.class, HotDeployResponsePacketHandler.INSTANCE);
    }
}
