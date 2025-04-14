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
public class RemoteCompilerResponsePacket extends Packet {

    private static final Logger logger = Logger.getLogger(RemoteCompilerResponsePacket.class);

    private String applicationName;

    private String printResult;

    @Override
    public Byte getCommand() {
        return Command.REMOTE_COMPILER_RESPONSE;
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
            logger.warning("The data RemoteCompilerResponsePacket received is not JSON, {}", jsonString);
            return;
        }
        RemoteCompilerResponsePacket packet = DebugToolsJsonUtils.toBean(jsonString, RemoteCompilerResponsePacket.class);
        this.setApplicationName(packet.getApplicationName());
        this.setPrintResult(packet.getPrintResult());
    }

    public static RemoteCompilerResponsePacket of(boolean isSuccess, String printResult, String applicationName) {
        RemoteCompilerResponsePacket packet = new RemoteCompilerResponsePacket();
        packet.setResultFlag(isSuccess ? SUCCESS : FAIL);
        packet.setApplicationName(applicationName);
        packet.setPrintResult(printResult);
        return packet;
    }


}
