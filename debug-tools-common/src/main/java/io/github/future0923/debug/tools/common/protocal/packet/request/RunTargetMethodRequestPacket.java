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
package io.github.future0923.debug.tools.common.protocal.packet.request;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.common.dto.RunDTO;
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
public class RunTargetMethodRequestPacket extends Packet {

    private static final Logger logger = Logger.getLogger(RunTargetMethodRequestPacket.class);

    private RunDTO runDTO;

    public RunTargetMethodRequestPacket() {
    }

    public RunTargetMethodRequestPacket(RunDTO runDTO) {
        this.runDTO = runDTO;
    }

    @Override
    public Byte getCommand() {
        return Command.RUN_TARGET_METHOD_REQUEST;
    }

    @Override
    public byte[] binarySerialize() {
        if (runDTO == null) {
            return new byte[0];
        } else {
            return DebugToolsJsonUtils.toJsonStr(runDTO).getBytes(StandardCharsets.UTF_8);
        }
    }

    @Override
    public void binaryDeserialization(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return;
        }
        String jsonString = new String(bytes, StandardCharsets.UTF_8);
        if (!DebugToolsJsonUtils.isTypeJSON(jsonString)) {
            logger.warning("The data RunTargetMethodRequestPacket received is not JSON, {}", jsonString);
            return;
        }
        runDTO = DebugToolsJsonUtils.toBean(jsonString, RunDTO.class);
    }
}
