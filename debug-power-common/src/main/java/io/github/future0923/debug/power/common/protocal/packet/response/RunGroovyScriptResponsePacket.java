package io.github.future0923.debug.power.common.protocal.packet.response;

import cn.hutool.core.exceptions.ExceptionUtil;
import io.github.future0923.debug.power.base.logging.Logger;
import io.github.future0923.debug.power.common.protocal.Command;
import io.github.future0923.debug.power.common.protocal.packet.Packet;
import io.github.future0923.debug.power.common.utils.DebugPowerJsonUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.nio.charset.StandardCharsets;

/**
 * @author future0923
 */
@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
public class RunGroovyScriptResponsePacket extends Packet {

    private static final Logger logger = Logger.getLogger(RunGroovyScriptResponsePacket.class);

    private String applicationName;

    private String printResult;

    private String throwable;

    private String offsetPath;

    @Override
    public Byte getCommand() {
        return Command.RUN_GROOVY_SCRIPT_RESPONSE;
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
            logger.warning("The data RunGroovyScriptResponsePacket received is not JSON, {}", jsonString);
            return;
        }
        RunGroovyScriptResponsePacket packet = DebugPowerJsonUtils.toBean(jsonString, RunGroovyScriptResponsePacket.class);
        this.setApplicationName(packet.getApplicationName());
        this.setPrintResult(packet.getPrintResult());
        this.setThrowable(packet.getThrowable());
        this.setOffsetPath(packet.getOffsetPath());
    }

    public static RunGroovyScriptResponsePacket of(Throwable throwable, String offsetPath, String applicationName) {
        RunGroovyScriptResponsePacket packet = new RunGroovyScriptResponsePacket();
        packet.setApplicationName(applicationName);
        packet.setResultFlag(FAIL);
        packet.setThrowableMessage(throwable);
        packet.setOffsetPath(offsetPath);
        return packet;
    }

    public void setThrowableMessage(Throwable throwable) {
        setThrowable(ExceptionUtil.stacktraceToString(throwable, -1));
    }
}
