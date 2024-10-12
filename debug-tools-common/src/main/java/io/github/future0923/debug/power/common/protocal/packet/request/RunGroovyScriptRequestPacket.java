package io.github.future0923.debug.power.common.protocal.packet.request;

import io.github.future0923.debug.power.base.logging.Logger;
import io.github.future0923.debug.power.common.protocal.Command;
import io.github.future0923.debug.power.common.protocal.packet.Packet;
import io.github.future0923.debug.power.common.utils.DebugPowerJsonUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.nio.charset.StandardCharsets;

/**
 * @author future0923
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RunGroovyScriptRequestPacket extends Packet {

    private static final Logger logger = Logger.getLogger(RunGroovyScriptRequestPacket.class);

    private String script;

    @Override
    public Byte getCommand() {
        return Command.RUN_GROOVY_SCRIPT_REQUEST;
    }

    @Override
    public byte[] binarySerialize() {
        return DebugPowerJsonUtils.toJsonStr(this).getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public void binaryDeserialization(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return;
        }
        String jsonString = new String(bytes, StandardCharsets.UTF_8);
        if (!DebugPowerJsonUtils.isTypeJSON(jsonString)) {
            logger.warning("The data RunGroovyScriptRequestPacket received is not JSON, {}", jsonString);
            return;
        }
        RunGroovyScriptRequestPacket packet = DebugPowerJsonUtils.toBean(jsonString, RunGroovyScriptRequestPacket.class);
        this.setScript(packet.getScript());
    }
}
