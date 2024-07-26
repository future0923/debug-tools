package io.github.future0923.debug.power.common.protocal.packet.response;

import cn.hutool.core.exceptions.ExceptionUtil;
import io.github.future0923.debug.power.base.logging.Logger;
import io.github.future0923.debug.power.common.dto.RunDTO;
import io.github.future0923.debug.power.common.protocal.Command;
import io.github.future0923.debug.power.common.protocal.packet.Packet;
import io.github.future0923.debug.power.common.utils.DebugPowerJsonUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author future0923
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class RunTargetMethodResponsePacket extends Packet {

    private static final Logger logger = Logger.getLogger(RunTargetMethodResponsePacket.class);

    private String className;

    private String methodName;

    private List<String> methodParameterTypes;

    private Payload payload;

    @Override
    public Byte getCommand() {
        return Command.RUN_TARGET_METHOD_RESPONSE;
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
            logger.warning("The data RunTargetMethodResponsePacket received is not JSON, {}", jsonString);
            return;
        }
        RunTargetMethodResponsePacket packet = DebugPowerJsonUtils.toBean(jsonString, RunTargetMethodResponsePacket.class);
        this.setClassName(packet.getClassName());
        this.setMethodName(packet.getMethodName());
        this.setMethodParameterTypes(packet.getMethodParameterTypes());
        this.setPayload(packet.getPayload());
    }

    @Data
    static class Payload {

        private String printResult;

        private String throwable;
    }

    public static RunTargetMethodResponsePacket of(RunDTO runDTO, Throwable throwable) {
        RunTargetMethodResponsePacket packet = new RunTargetMethodResponsePacket();
        packet.setRunInfo(runDTO);
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

    public void setRunInfo(RunDTO runDTO) {
        this.setClassName(runDTO.getTargetClassName());
        this.setMethodName(runDTO.getTargetMethodName());
        this.setMethodParameterTypes(runDTO.getTargetMethodParameterTypes());
    }
}
