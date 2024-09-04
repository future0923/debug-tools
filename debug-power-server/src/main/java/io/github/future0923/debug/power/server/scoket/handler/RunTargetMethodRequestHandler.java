package io.github.future0923.debug.power.server.scoket.handler;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import io.github.future0923.debug.power.base.logging.Logger;
import io.github.future0923.debug.power.base.utils.DebugPowerStringUtils;
import io.github.future0923.debug.power.common.dto.RunDTO;
import io.github.future0923.debug.power.common.dto.RunResultDTO;
import io.github.future0923.debug.power.common.enums.ResultClassType;
import io.github.future0923.debug.power.common.exception.ArgsParseException;
import io.github.future0923.debug.power.common.handler.BasePacketHandler;
import io.github.future0923.debug.power.common.protocal.packet.request.RunTargetMethodRequestPacket;
import io.github.future0923.debug.power.common.protocal.packet.response.RunTargetMethodResponsePacket;
import io.github.future0923.debug.power.common.utils.DebugPowerClassUtils;
import io.github.future0923.debug.power.server.DebugPowerBootstrap;
import io.github.future0923.debug.power.server.jvm.VmToolsUtils;
import io.github.future0923.debug.power.server.utils.DebugPowerEnvUtils;
import io.github.future0923.debug.power.server.utils.DebugPowerResultUtils;

import java.io.OutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author future0923
 */
public class RunTargetMethodRequestHandler extends BasePacketHandler<RunTargetMethodRequestPacket> {

    private static final Logger logger = Logger.getLogger(RunTargetMethodRequestHandler.class);

    public static final RunTargetMethodRequestHandler INSTANCE = new RunTargetMethodRequestHandler();

    @Override
    public void handle(OutputStream outputStream, RunTargetMethodRequestPacket packet) throws Exception {
        RunDTO runDTO = packet.getRunDTO();
        String targetClassName = runDTO.getTargetClassName();
        if (DebugPowerStringUtils.isBlank(targetClassName)) {
            ArgsParseException exception = new ArgsParseException("目标类为空");
            String offsetPath = RunResultDTO.genOffsetPathRandom(exception);
            DebugPowerResultUtils.putCache(offsetPath, exception);
            writeAndFlushNotException(outputStream, RunTargetMethodResponsePacket.of(runDTO, exception, offsetPath, DebugPowerBootstrap.serverConfig.getApplicationName()));
            return;
        }
        Class<?> targetClass;
        try {
            targetClass = DebugPowerClassUtils.loadClass(targetClassName);
        } catch (Exception e) {
            String offsetPath = RunResultDTO.genOffsetPathRandom(e);
            DebugPowerResultUtils.putCache(offsetPath, e);
            writeAndFlushNotException(outputStream, RunTargetMethodResponsePacket.of(runDTO, e, offsetPath, DebugPowerBootstrap.serverConfig.getApplicationName()));
            return;
        }
        Method targetMethod;
        try {
            targetMethod = targetClass.getDeclaredMethod(runDTO.getTargetMethodName(), DebugPowerClassUtils.getTypes(runDTO.getTargetMethodParameterTypes()));
        } catch (NoSuchMethodException | SecurityException e) {
            ArgsParseException exception = new ArgsParseException("未找到目标方法");
            String offsetPath = RunResultDTO.genOffsetPathRandom(exception);
            DebugPowerResultUtils.putCache(offsetPath, exception);
            writeAndFlushNotException(outputStream, RunTargetMethodResponsePacket.of(runDTO, exception, offsetPath, DebugPowerBootstrap.serverConfig.getApplicationName()));
            return;
        }
        DebugPowerEnvUtils.setRequest(runDTO);
        DebugPowerEnvUtils.setXxlJobParam(runDTO.getXxlJobParam());
        Object instance = VmToolsUtils.getInstance(targetClass, targetMethod);
        Method bridgedMethod = DebugPowerEnvUtils.findBridgedMethod(targetMethod);
        ReflectUtil.setAccessible(bridgedMethod);
        Object[] targetMethodArgs = DebugPowerEnvUtils.getArgs(bridgedMethod, runDTO.getTargetMethodContent());
        run(targetClass, bridgedMethod, instance, targetMethodArgs, runDTO, outputStream);
    }

    private void run(Class<?> targetClass, Method bridgedMethod, Object instance, Object[] targetMethodArgs, RunDTO runDTO, OutputStream outputStream) throws Exception {
        boolean voidType = void.class.isAssignableFrom(bridgedMethod.getReturnType()) || Void.class.isAssignableFrom(bridgedMethod.getReturnType());
        if (instance instanceof Proxy) {
            InvocationHandler invocationHandler = Proxy.getInvocationHandler(instance);
            if (DebugPowerEnvUtils.isAopProxy(invocationHandler)) {
                try {
                    printResult(invocationHandler.invoke(instance, bridgedMethod, targetMethodArgs), runDTO, outputStream, voidType);
                    return;
                } catch (Throwable ignored) {
                }
            }
        }
        try {
            printResult(bridgedMethod.invoke(instance, targetMethodArgs), runDTO, outputStream, voidType);
        } catch (Throwable throwable) {
            logger.error("invoke target method error", throwable);
            Throwable cause = throwable.getCause();
            String offsetPath = RunResultDTO.genOffsetPathRandom(cause);
            DebugPowerResultUtils.putCache(offsetPath, cause);
            writeAndFlushNotException(outputStream, RunTargetMethodResponsePacket.of(runDTO, cause, offsetPath, DebugPowerBootstrap.serverConfig.getApplicationName()));
        }
    }

    private void printResult(Object result, RunDTO runDTO, OutputStream outputStream, boolean voidType) {
        RunTargetMethodResponsePacket packet = new RunTargetMethodResponsePacket();
        packet.setRunInfo(runDTO, DebugPowerBootstrap.serverConfig.getApplicationName());
        if (voidType) {
            packet.setResultClassType(ResultClassType.VOID);
            packet.setPrintResult("Void");
        } else {
            if (result == null) {
                packet.setResultClassType(ResultClassType.NULL);
                packet.setPrintResult("NULL");
            } else if (ClassUtil.isSimpleValueType(result.getClass())) {
                packet.setResultClassType(ResultClassType.SIMPLE);
                packet.setPrintResult(Convert.toStr(result));
            } else {
                packet.setResultClassType(ResultClassType.OBJECT);
                packet.setPrintResult(result.toString());
                String offsetPath = RunResultDTO.genOffsetPathRandom(result);
                packet.setOffsetPath(offsetPath);
                DebugPowerResultUtils.putCache(offsetPath, result);
            }
        }
        writeAndFlushNotException(outputStream, packet);
    }

}
