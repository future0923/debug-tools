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
public class RunGroovyScriptRequestPacket extends Packet {

    private static final Logger logger = Logger.getLogger(RunGroovyScriptRequestPacket.class);

    /**
     * Groovy脚本内容
     */
    private String script;

    /**
     * 类加载器
     */
    private String identity;

    @Override
    public Byte getCommand() {
        return Command.RUN_GROOVY_SCRIPT_REQUEST;
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
            logger.warning("The data RunGroovyScriptRequestPacket received is not JSON, {}", jsonString);
            return;
        }
        RunGroovyScriptRequestPacket packet = DebugToolsJsonUtils.toBean(jsonString, RunGroovyScriptRequestPacket.class);
        this.setScript(packet.getScript());
        this.setIdentity(packet.getIdentity());
    }
}
