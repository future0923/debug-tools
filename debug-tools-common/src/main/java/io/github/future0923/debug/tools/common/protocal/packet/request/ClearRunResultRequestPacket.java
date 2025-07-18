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
package io.github.future0923.debug.tools.common.protocal.packet.request;

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
public class ClearRunResultRequestPacket extends Packet {

    private static final Logger logger = Logger.getLogger(ClearRunResultRequestPacket.class);

    private String fieldOffset;

    private String traceOffset;

    public ClearRunResultRequestPacket() {
    }

    public ClearRunResultRequestPacket(String fieldOffset, String traceOffset) {
        this.fieldOffset = fieldOffset;
        this.traceOffset = traceOffset;
    }

    @Override
    public Byte getCommand() {
        return Command.CLEAR_RUN_RESULT;
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
            logger.warning("The data ClearRunResultRequestPacket received is not JSON, {}", jsonString);
            return;
        }
        ClearRunResultRequestPacket packet = DebugToolsJsonUtils.toBean(jsonString, ClearRunResultRequestPacket.class);
        this.setFieldOffset(packet.getFieldOffset());
        this.setTraceOffset(packet.getTraceOffset());
    }
}
