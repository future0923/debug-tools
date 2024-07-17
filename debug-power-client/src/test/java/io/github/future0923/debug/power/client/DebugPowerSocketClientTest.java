package io.github.future0923.debug.power.client;

import io.github.future0923.debug.power.client.holder.ClientSocketHolder;
import io.github.future0923.debug.power.common.dto.RunContentDTO;
import io.github.future0923.debug.power.common.dto.RunDTO;
import io.github.future0923.debug.power.common.enums.RunContentType;
import io.github.future0923.debug.power.common.exception.SocketCloseException;
import io.github.future0923.debug.power.common.protocal.packet.request.RunTargetMethodRequestPacket;
import io.github.future0923.debug.power.common.protocal.packet.request.ServerCloseRequestPacket;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author future0923
 */
class DebugPowerSocketClientTest {

    @Test
    public void serverClose() throws SocketCloseException, IOException, InterruptedException {
        DebugPowerSocketClient socketClient = new DebugPowerSocketClient();
        socketClient.connect();
        ServerCloseRequestPacket packet = new ServerCloseRequestPacket();
        ClientSocketHolder.INSTANCE.send(packet);
        Thread.sleep(1000000000);
    }

    @Test
    public void test() throws SocketCloseException, IOException, InterruptedException {
        DebugPowerSocketClient socketClient = new DebugPowerSocketClient();
        socketClient.connect();
        RunDTO runDTO = new RunDTO();
        runDTO.setTargetClassName("io.github.future0923.debug.power.test.application.service.TestService");
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
        ClientSocketHolder.INSTANCE.send(packet);
        Thread.sleep(1000000000);
    }

}