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
package io.github.future0923.debug.tools.common.protocal.packet.response;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.common.protocal.Command;
import io.github.future0923.debug.tools.common.protocal.packet.Packet;
import io.github.future0923.debug.tools.common.utils.DebugToolsJsonUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.nio.charset.StandardCharsets;

/**
 * @author future0923
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HotDeployResponsePacket extends Packet {

    private static final Logger logger = Logger.getLogger(HotDeployResponsePacket.class);

    private String applicationName;

    private String printResult;

    @Override
    public Byte getCommand() {
        return Command.REMOTE_COMPILER_HOT_DEPLOY_RESPONSE;
    }

    @Override
    public byte[] binarySerialize() {
        return DebugToolsJsonUtils.toJsonStr(this).getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public void binaryDeserialization(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return;
        }
        String jsonString = new String(bytes, StandardCharsets.UTF_8);
        if (!DebugToolsJsonUtils.isTypeJSON(jsonString)) {
            logger.warning("The data HotDeployResponsePacket received is not JSON, {}", jsonString);
            return;
        }
        HotDeployResponsePacket packet = DebugToolsJsonUtils.toBean(jsonString, HotDeployResponsePacket.class);
        this.setApplicationName(packet.getApplicationName());
        this.setPrintResult(packet.getPrintResult());
    }

    public static HotDeployResponsePacket of(boolean isSuccess, String printResult, String applicationName) {
        HotDeployResponsePacket packet = new HotDeployResponsePacket();
        packet.setResultFlag(isSuccess ? SUCCESS : FAIL);
        packet.setApplicationName(applicationName);
        packet.setPrintResult(printResult);
        return packet;
    }


}
