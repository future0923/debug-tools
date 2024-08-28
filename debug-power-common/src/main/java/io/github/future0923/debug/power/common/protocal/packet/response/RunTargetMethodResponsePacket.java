package io.github.future0923.debug.power.common.protocal.packet.response;

import cn.hutool.core.exceptions.ExceptionUtil;
import io.github.future0923.debug.power.base.logging.Logger;
import io.github.future0923.debug.power.common.dto.RunDTO;
import io.github.future0923.debug.power.common.enums.ResultClassType;
import io.github.future0923.debug.power.common.protocal.Command;
import io.github.future0923.debug.power.common.protocal.packet.Packet;
import io.github.future0923.debug.power.common.utils.DebugPowerJsonUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author future0923
 */
@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
public class RunTargetMethodResponsePacket extends Packet {

    private static final Logger logger = Logger.getLogger(RunTargetMethodResponsePacket.class);

    private String applicationName;

    private String className;

    private String methodName;

    private List<String> methodParameterTypes;

    private ResultClassType resultClassType;

    private String printResult;

    private String throwable;

    private String offsetPath;


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
        this.setApplicationName(packet.getApplicationName());
        this.setClassName(packet.getClassName());
        this.setMethodName(packet.getMethodName());
        this.setMethodParameterTypes(packet.getMethodParameterTypes());
        this.setResultClassType(packet.getResultClassType());
        this.setPrintResult(packet.getPrintResult());
        this.setThrowable(packet.getThrowable());
        this.setOffsetPath(packet.getOffsetPath());
    }

    public static RunTargetMethodResponsePacket of(RunDTO runDTO, Throwable throwable, String offsetPath, String applicationName) {
        RunTargetMethodResponsePacket packet = new RunTargetMethodResponsePacket();
        packet.setRunInfo(runDTO, applicationName);
        packet.setResultFlag(FAIL);
        packet.setThrowableMessage(throwable);
        packet.setOffsetPath(offsetPath);
        return packet;
    }

    public void setThrowableMessage(Throwable throwable) {
        setThrowable(ExceptionUtil.stacktraceToString(throwable, -1));
    }

    public void setRunInfo(RunDTO runDTO, String applicationName) {
        this.setApplicationName(applicationName);
        this.setClassName(runDTO.getTargetClassName());
        this.setMethodName(runDTO.getTargetMethodName());
        this.setMethodParameterTypes(runDTO.getTargetMethodParameterTypes());
    }
}
