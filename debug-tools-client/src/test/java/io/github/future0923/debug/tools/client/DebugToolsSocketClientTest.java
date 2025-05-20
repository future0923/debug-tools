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