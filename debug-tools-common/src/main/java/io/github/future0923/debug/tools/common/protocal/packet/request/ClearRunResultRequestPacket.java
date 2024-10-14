package io.github.future0923.debug.tools.common.protocal.packet.request;

import io.github.future0923.debug.tools.base.utils.DebugToolsStringUtils;
import io.github.future0923.debug.tools.common.protocal.Command;
import io.github.future0923.debug.tools.common.protocal.packet.Packet;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.nio.charset.StandardCharsets;

/**
 * @author future0923
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ClearRunResultRequestPacket extends Packet {

    private String fieldOffset;

    public ClearRunResultRequestPacket() {
    }

    public ClearRunResultRequestPacket(String fieldOffset) {
        this.fieldOffset = fieldOffset;
    }

    @Override
    public Byte getCommand() {
        return Command.CLEAR_RUN_RESULT;
    }

    @Override
    public byte[] binarySerialize() {
        if (DebugToolsStringUtils.isBlank(fieldOffset)) {
            return new byte[0];
        } else {
            return fieldOffset.getBytes(StandardCharsets.UTF_8);
        }
    }

    @Override
    public void binaryDeserialization(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return;
        }
        fieldOffset = new String(bytes, StandardCharsets.UTF_8);
    }
}
