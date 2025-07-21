/*
 * Copyright (C) 2024-2025 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package io.github.future0923.debug.tools.server.scoket.handler;

import io.github.future0923.debug.tools.base.exception.DefaultClassLoaderException;
import io.github.future0923.debug.tools.base.hutool.core.convert.Convert;
import io.github.future0923.debug.tools.base.hutool.core.util.ClassUtil;
import io.github.future0923.debug.tools.base.hutool.core.util.ReflectUtil;
import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.base.trace.MethodTrace;
import io.github.future0923.debug.tools.base.trace.MethodTreeNode;
import io.github.future0923.debug.tools.base.utils.DebugToolsStringUtils;
import io.github.future0923.debug.tools.common.dto.RunDTO;
import io.github.future0923.debug.tools.common.dto.RunResultDTO;
import io.github.future0923.debug.tools.common.dto.TraceMethodDTO;
import io.github.future0923.debug.tools.common.enums.ResultClassType;
import io.github.future0923.debug.tools.common.exception.ArgsParseException;
import io.github.future0923.debug.tools.common.handler.BasePacketHandler;
import io.github.future0923.debug.tools.common.protocal.packet.request.RunTargetMethodRequestPacket;
import io.github.future0923.debug.tools.common.protocal.packet.response.RunTargetMethodResponsePacket;
import io.github.future0923.debug.tools.common.utils.DebugToolsClassUtils;
import io.github.future0923.debug.tools.server.DebugToolsBootstrap;
import io.github.future0923.debug.tools.server.http.handler.AllClassLoaderHttpHandler;
import io.github.future0923.debug.tools.server.trace.TraceMethodClassFileTransformer;
import io.github.future0923.debug.tools.server.utils.BeanInstanceUtils;
import io.github.future0923.debug.tools.server.utils.DebugToolsEnvUtils;
import io.github.future0923.debug.tools.server.utils.DebugToolsResultUtils;

import java.io.OutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

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
        ClassLoader classLoader = null;
        if (runDTO.getClassLoader() != null && DebugToolsStringUtils.isNotBlank(runDTO.getClassLoader().getIdentity())) {
            try {
                classLoader = AllClassLoaderHttpHandler.getClassLoader(runDTO.getClassLoader().getIdentity());
            } catch (DefaultClassLoaderException e) {
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
            targetClass = DebugToolsClassUtils.loadClass(targetClassName, classLoader);
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
        Object instance = null;
        if (!targetMethod.isSynthetic()) {
            try {
                instance = BeanInstanceUtils.getInstance(targetClass, targetMethod);
            } catch (Exception e) {
                ArgsParseException exception = new ArgsParseException("获取目标实例失败", e);
                String offsetPath = RunResultDTO.genOffsetPathRandom(exception);
                DebugToolsResultUtils.putCache(offsetPath, exception);
                writeAndFlushNotException(outputStream, RunTargetMethodResponsePacket.of(runDTO, exception, offsetPath, DebugToolsBootstrap.serverConfig.getApplicationName()));
                return;
            }
        }
        Method bridgedMethod = DebugToolsEnvUtils.findBridgedMethod(targetMethod);
        TraceMethodDTO traceMethodDTO = runDTO.getTraceMethodDTO();
        boolean traceMethod = traceMethodDTO != null && traceMethodDTO.getTraceMethod();
        if (traceMethod) {
            TraceMethodClassFileTransformer.traceMethod(classLoader, targetClass, bridgedMethod, traceMethodDTO);
        }
        ReflectUtil.setAccessible(bridgedMethod);
        Object[] targetMethodArgs = DebugToolsEnvUtils.getArgs(bridgedMethod, runDTO.getTargetMethodContent());
        run(bridgedMethod, instance, targetMethodArgs, runDTO, outputStream, traceMethod);
        Thread.currentThread().setContextClassLoader(orgClassLoader);
    }

    private void run(Method bridgedMethod, Object instance, Object[] targetMethodArgs, RunDTO runDTO, OutputStream outputStream, Boolean traceMethod) throws Exception {
        boolean voidType = void.class.isAssignableFrom(bridgedMethod.getReturnType()) || Void.class.isAssignableFrom(bridgedMethod.getReturnType());
        if (instance instanceof Proxy) {
            InvocationHandler invocationHandler = Proxy.getInvocationHandler(instance);
            if (DebugToolsEnvUtils.isAopProxy(invocationHandler)) {
                try {
                    printResult(invocationHandler.invoke(instance, bridgedMethod, targetMethodArgs), runDTO, outputStream, voidType, traceMethod);
                    return;
                } catch (Throwable ignored) {
                }
            }
        }
        try {
            printResult(bridgedMethod.invoke(instance, targetMethodArgs), runDTO, outputStream, voidType, traceMethod);
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

    private void printResult(Object result, RunDTO runDTO, OutputStream outputStream, boolean voidType, boolean traceMethod) {
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
        if (traceMethod) {
            List<MethodTreeNode> traceResult = MethodTrace.getResult();
            String offsetPath = RunResultDTO.genOffsetPathRandom(traceResult);
            DebugToolsResultUtils.putCache(offsetPath, traceResult);
            packet.setTraceOffsetPath(offsetPath);
        }
        writeAndFlushNotException(outputStream, packet);
    }

}
