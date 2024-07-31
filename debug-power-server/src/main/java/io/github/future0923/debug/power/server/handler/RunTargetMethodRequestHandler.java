package io.github.future0923.debug.power.server.handler;

import cn.hutool.core.util.ReflectUtil;
import io.github.future0923.debug.power.base.logging.Logger;
import io.github.future0923.debug.power.common.dto.RunConfigDTO;
import io.github.future0923.debug.power.common.dto.RunDTO;
import io.github.future0923.debug.power.common.enums.PrintResultType;
import io.github.future0923.debug.power.common.exception.ArgsParseException;
import io.github.future0923.debug.power.common.handler.BasePacketHandler;
import io.github.future0923.debug.power.common.protocal.packet.request.RunTargetMethodRequestPacket;
import io.github.future0923.debug.power.common.protocal.packet.response.RunTargetMethodResponsePacket;
import io.github.future0923.debug.power.common.utils.DebugPowerClassUtils;
import io.github.future0923.debug.power.common.utils.DebugPowerJsonUtils;
import io.github.future0923.debug.power.common.utils.DebugPowerParamConvertUtils;
import io.github.future0923.debug.power.server.jvm.VmToolsUtils;
import io.github.future0923.debug.power.server.mock.springmvc.MockHttpServletRequest;
import org.springframework.aop.framework.AopProxy;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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
        Class<?> targetClass = DebugPowerClassUtils.loadClass(runDTO.getTargetClassName());
        Method targetMethod;
        try {
            targetMethod = targetClass.getDeclaredMethod(runDTO.getTargetMethodName(), DebugPowerClassUtils.getTypes(runDTO.getTargetMethodParameterTypes()));
        } catch (NoSuchMethodException | SecurityException e) {
            throw new ArgsParseException("未找到目标方法");
        }

        setRequest(runDTO);

        Object instance = VmToolsUtils.getInstance(targetClass, targetMethod);
        // 获取正确的目标方法（非桥接方法）
        Method bridgedMethod = BridgeMethodResolver.findBridgedMethod(targetMethod);
        ReflectUtil.setAccessible(bridgedMethod);
        Object[] targetMethodArgs = DebugPowerParamConvertUtils.getArgs(bridgedMethod, runDTO.getTargetMethodContent());
        run(targetClass, bridgedMethod, instance, targetMethodArgs, runDTO, outputStream);
    }

    private static void setRequest(RunDTO runDTO) {
        if (runDTO.getHeaders() != null && !runDTO.getHeaders().isEmpty()) {
            MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
            runDTO.getHeaders().forEach(mockHttpServletRequest::addHeader);
            ServletRequestAttributes requestAttributes = new ServletRequestAttributes(mockHttpServletRequest);
            RequestContextHolder.setRequestAttributes(requestAttributes);
        } else {
            RequestContextHolder.resetRequestAttributes();
        }
    }

    private void run(Class<?> targetClass, Method bridgedMethod, Object instance, Object[] targetMethodArgs, RunDTO runDTO, OutputStream outputStream) throws Exception {
        if (instance instanceof Proxy) {
            InvocationHandler invocationHandler = Proxy.getInvocationHandler(instance);
            if (invocationHandler instanceof AopProxy) {
                try {
                    printResult(invocationHandler.invoke(instance, bridgedMethod, targetMethodArgs), runDTO, outputStream);
                    return;
                } catch (Throwable ignored) {
                }
            }
        }
        try {
            printResult(bridgedMethod.invoke(instance, targetMethodArgs), runDTO, outputStream);
        } catch (Throwable throwable) {
            logger.error("invoke target method error", throwable);
            writeAndFlushNotException(outputStream, RunTargetMethodResponsePacket.of(runDTO, throwable));
        }
    }

    private void printResult(Object result, RunDTO runDTO, OutputStream outputStream) {
        RunTargetMethodResponsePacket packet = new RunTargetMethodResponsePacket();
        packet.setRunInfo(runDTO);
        if (result == null) {
            logger.info("DebugPower执行结果：null");
            packet.setPrintResult(null);
        } else {
            RunConfigDTO configDTO = runDTO.getRunConfigDTO();
            if (configDTO == null || configDTO.getPrintResultType() == null || PrintResultType.TOSTRING.equals(configDTO.getPrintResultType())) {
                String printResult = result.toString();
                logger.info("DebugPower执行结果：{}", printResult);
                packet.setPrintResult(printResult);
            } else if (PrintResultType.JSON.equals(configDTO.getPrintResultType())) {
                String printResult;
                try {
                    printResult = DebugPowerJsonUtils.toJsonPrettyStr(result);
                } catch (Exception e) {
                    printResult = result.toString();
                }
                packet.setPrintResult(printResult);
                logger.info("DebugPower执行结果：{}", printResult);
            } else if (PrintResultType.NO_PRINT.equals(configDTO.getPrintResultType())) {

            }
        }
        writeAndFlushNotException(outputStream, packet);
    }

}
