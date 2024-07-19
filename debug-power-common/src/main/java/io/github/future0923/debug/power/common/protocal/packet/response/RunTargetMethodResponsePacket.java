package io.github.future0923.debug.power.common.protocal.packet.response;

import cn.hutool.core.exceptions.ExceptionUtil;
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
@EqualsAndHashCode(callSuper = true)
public class RunTargetMethodResponsePacket extends Packet {

    private static final Logger logger = Logger.getLogger(RunTargetMethodResponsePacket.class);

    private Payload payload;

    @Override
    public Byte getCommand() {
        return Command.RUN_TARGET_METHOD_RESPONSE;
    }

    @Override
    public byte[] binarySerialize() {
        if (payload == null) {
            return new byte[0];
        } else {
            return DebugPowerJsonUtils.toJsonStr(payload).getBytes(StandardCharsets.UTF_8);
        }
    }

    @Override
    public void binaryDeserialization(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return;
        }
        String jsonString = new String(bytes, StandardCharsets.UTF_8);
        if (!DebugPowerJsonUtils.isTypeJSON(jsonString)) {
            logger.warning("The data RunTargetMethodResponsePacket received is not JSON, {}", jsonString);
            return;
        }
        payload = DebugPowerJsonUtils.toBean(jsonString, Payload.class);
    }

    @Data
    static class Payload {

        private String printResult;

        private String throwable;
    }

    public static RunTargetMethodResponsePacket of(Throwable throwable) {
        RunTargetMethodResponsePacket packet = new RunTargetMethodResponsePacket();
        packet.setResultFlag(FAIL);
        packet.setThrowable(throwable);
        return packet;
    }

    public String getPrintResult() {
        return payload == null ? null : payload.getPrintResult();
    }

    public void setPrintResult(String printResult) {
        if (payload == null) {
            payload = new Payload();
        }
        payload.setPrintResult(printResult);
    }

    public String getThrowable() {
        return payload == null ? null : payload.getThrowable();
    }

    public void setThrowable(Throwable throwable) {
        if (payload == null) {
            payload = new Payload();
        }
        payload.setThrowable(ExceptionUtil.stacktraceToString(throwable));
    }
}
