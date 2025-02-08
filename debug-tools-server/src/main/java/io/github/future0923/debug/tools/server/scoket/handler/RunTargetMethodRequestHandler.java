package io.github.future0923.debug.tools.server.scoket.handler;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.base.utils.DebugToolsStringUtils;
import io.github.future0923.debug.tools.common.dto.RunDTO;
import io.github.future0923.debug.tools.common.dto.RunResultDTO;
import io.github.future0923.debug.tools.common.enums.ResultClassType;
import io.github.future0923.debug.tools.common.exception.ArgsParseException;
import io.github.future0923.debug.tools.common.handler.BasePacketHandler;
import io.github.future0923.debug.tools.common.protocal.packet.request.RunTargetMethodRequestPacket;
import io.github.future0923.debug.tools.common.protocal.packet.response.RunTargetMethodResponsePacket;
import io.github.future0923.debug.tools.common.utils.DebugToolsClassUtils;
import io.github.future0923.debug.tools.server.DebugToolsBootstrap;
import io.github.future0923.debug.tools.server.http.handler.AllClassLoaderHttpHandler;
import io.github.future0923.debug.tools.server.jvm.VmToolsUtils;
import io.github.future0923.debug.tools.server.utils.DebugToolsEnvUtils;
import io.github.future0923.debug.tools.server.utils.DebugToolsResultUtils;

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
        ClassLoader orgClassLoader = Thread.currentThread().getContextClassLoader();
        String targetClassName = runDTO.getTargetClassName();
        if (DebugToolsStringUtils.isBlank(targetClassName)) {
            ArgsParseException exception = new ArgsParseException("目标类为空");
            String offsetPath = RunResultDTO.genOffsetPathRandom(exception);
            DebugToolsResultUtils.putCache(offsetPath, exception);
            writeAndFlushNotException(outputStream, RunTargetMethodResponsePacket.of(runDTO, exception, offsetPath, DebugToolsBootstrap.serverConfig.getApplicationName()));
            return;
        }
        if (runDTO.getClassLoader() != null && DebugToolsStringUtils.isNotBlank(runDTO.getClassLoader().getIdentity())) {
            ClassLoader classLoader = AllClassLoaderHttpHandler.getClassLoaderMap().get(runDTO.getClassLoader().getIdentity());
            if (classLoader == null) {
                ArgsParseException exception = new ArgsParseException("未找到[" + runDTO.getClassLoader().getName() +"]类加载器");
                String offsetPath = RunResultDTO.genOffsetPathRandom(exception);
                DebugToolsResultUtils.putCache(offsetPath, exception);
                writeAndFlushNotException(outputStream, RunTargetMethodResponsePacket.of(runDTO, exception, offsetPath, DebugToolsBootstrap.serverConfig.getApplicationName()));
                return;
            }
            Thread.currentThread().setContextClassLoader(classLoader);
        }
        Class<?> targetClass;
        try {
            targetClass = DebugToolsClassUtils.loadClass(targetClassName);
        } catch (Exception e) {
            String offsetPath = RunResultDTO.genOffsetPathRandom(e);
            DebugToolsResultUtils.putCache(offsetPath, e);
            writeAndFlushNotException(outputStream, RunTargetMethodResponsePacket.of(runDTO, e, offsetPath, DebugToolsBootstrap.serverConfig.getApplicationName()));
            return;
        }
        Method targetMethod;
        try {
            targetMethod = targetClass.getDeclaredMethod(runDTO.getTargetMethodName(), DebugToolsClassUtils.getTypes(runDTO.getTargetMethodParameterTypes()));
        } catch (NoSuchMethodException | SecurityException e) {
            ArgsParseException exception = new ArgsParseException("未找到目标方法");
            String offsetPath = RunResultDTO.genOffsetPathRandom(exception);
            DebugToolsResultUtils.putCache(offsetPath, exception);
            writeAndFlushNotException(outputStream, RunTargetMethodResponsePacket.of(runDTO, exception, offsetPath, DebugToolsBootstrap.serverConfig.getApplicationName()));
            return;
        }
        DebugToolsEnvUtils.setRequest(runDTO);
        if (DebugToolsStringUtils.isNotBlank(runDTO.getXxlJobParam())) {
            DebugToolsEnvUtils.setXxlJobParam(runDTO.getXxlJobParam());
        }
        Object instance = VmToolsUtils.getInstance(targetClass, targetMethod);
        Method bridgedMethod = DebugToolsEnvUtils.findBridgedMethod(targetMethod);
        ReflectUtil.setAccessible(bridgedMethod);
        Object[] targetMethodArgs = DebugToolsEnvUtils.getArgs(bridgedMethod, runDTO.getTargetMethodContent());
        run(targetClass, bridgedMethod, instance, targetMethodArgs, runDTO, outputStream);
        Thread.currentThread().setContextClassLoader(orgClassLoader);
    }

    private void run(Class<?> targetClass, Method bridgedMethod, Object instance, Object[] targetMethodArgs, RunDTO runDTO, OutputStream outputStream) throws Exception {
        boolean voidType = void.class.isAssignableFrom(bridgedMethod.getReturnType()) || Void.class.isAssignableFrom(bridgedMethod.getReturnType());
        if (instance instanceof Proxy) {
            InvocationHandler invocationHandler = Proxy.getInvocationHandler(instance);
            if (DebugToolsEnvUtils.isAopProxy(invocationHandler)) {
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
            if (cause == null) {
                cause = throwable;
            }
            String offsetPath = RunResultDTO.genOffsetPathRandom(cause);
            DebugToolsResultUtils.putCache(offsetPath, cause);
            writeAndFlushNotException(outputStream, RunTargetMethodResponsePacket.of(runDTO, cause, offsetPath, DebugToolsBootstrap.serverConfig.getApplicationName()));
        }
    }

    private void printResult(Object result, RunDTO runDTO, OutputStream outputStream, boolean voidType) {
        RunTargetMethodResponsePacket packet = new RunTargetMethodResponsePacket();
        packet.setRunInfo(runDTO, DebugToolsBootstrap.serverConfig.getApplicationName());
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
                DebugToolsResultUtils.putCache(offsetPath, result);
            }
        }
        writeAndFlushNotException(outputStream, packet);
    }

}
