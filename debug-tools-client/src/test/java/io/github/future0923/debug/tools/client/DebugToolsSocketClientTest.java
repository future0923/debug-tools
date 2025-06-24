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
package io.github.future0923.debug.tools.client;

import io.github.future0923.debug.tools.common.dto.RunContentDTO;
import io.github.future0923.debug.tools.common.dto.RunDTO;
import io.github.future0923.debug.tools.common.enums.RunContentType;
import io.github.future0923.debug.tools.common.exception.SocketCloseException;
import io.github.future0923.debug.tools.common.protocal.packet.request.RunTargetMethodRequestPacket;
import io.github.future0923.debug.tools.common.protocal.packet.request.ServerCloseRequestPacket;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author future0923
 */
class DebugToolsSocketClientTest {

    @Test
    public void connect() throws IOException {

            new Socket("127.0.0.1", 32123);
    }

    @Test
    public void serverClose() throws SocketCloseException, IOException, InterruptedException {
        DebugToolsSocketClient socketClient = new DebugToolsSocketClient();
        socketClient.connect();
        ServerCloseRequestPacket packet = new ServerCloseRequestPacket();
        socketClient.getHolder().send(packet);
        Thread.sleep(1000000000);
    }

    @Test
    public void test() throws SocketCloseException, IOException, InterruptedException {
        DebugToolsSocketClient socketClient = new DebugToolsSocketClient();
        socketClient.connect();
        RunDTO runDTO = new RunDTO();
        runDTO.setTargetClassName("io.github.future0923.debug.tools.test.application.service.TestService");
        runDTO.setTargetMethodName("test");
        runDTO.setTargetMethodParameterTypes(Arrays.asList("java.lang.String", "java.lang.Integer"));
        Map<String, RunContentDTO> contentMap = new HashMap<>();
        contentMap.put("name", RunContentDTO.builder()
                .type(RunContentType.SIMPLE.getType())
                .content("future0923")
                .build());
        contentMap.put("age", RunContentDTO.builder()
                .type(RunContentType.SIMPLE.getType())
                .content("19")
                .build());
        runDTO.setTargetMethodContent(contentMap);
        RunTargetMethodRequestPacket packet = new RunTargetMethodRequestPacket(runDTO);
        socketClient.getHolder().send(packet);
        Thread.sleep(1000000000);
    }

}