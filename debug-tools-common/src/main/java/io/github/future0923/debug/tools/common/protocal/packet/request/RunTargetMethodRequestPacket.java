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
