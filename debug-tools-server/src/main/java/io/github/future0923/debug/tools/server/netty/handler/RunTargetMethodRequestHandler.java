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
package io.github.future0923.debug.tools.server.netty.handler;

import io.github.future0923.debug.tools.base.around.RunMethodAround;
import io.github.future0923.debug.tools.base.context.RunMethodContext;
import io.github.future0923.debug.tools.base.exception.DefaultClassLoaderException;
import io.github.future0923.debug.tools.base.hutool.core.convert.Convert;
import io.github.future0923.debug.tools.base.hutool.core.util.ClassUtil;
import io.github.future0923.debug.tools.base.hutool.core.util.ReflectUtil;
import io.github.future0923.debug.tools.base.hutool.core.util.StrUtil;
import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.base.trace.MethodTrace;
import io.github.future0923.debug.tools.base.trace.MethodTreeNode;
import io.github.future0923.debug.tools.base.utils.DebugToolsClassUtils;
import io.github.future0923.debug.tools.base.utils.DebugToolsStringUtils;
import io.github.future0923.debug.tools.common.dto.RunDTO;
import io.github.future0923.debug.tools.common.dto.RunResultDTO;
import io.github.future0923.debug.tools.common.dto.TraceMethodDTO;
import io.github.future0923.debug.tools.common.enums.ResultClassType;
import io.github.future0923.debug.tools.common.exception.ArgsParseException;
import io.github.future0923.debug.tools.common.protocal.packet.request.RunTargetMethodRequestPacket;
import io.github.future0923.debug.tools.common.protocal.packet.response.RunTargetMethodResponsePacket;
import io.github.future0923.debug.tools.server.DebugToolsBootstrap;
import io.github.future0923.debug.tools.server.compiler.DynamicCompiler;
import io.github.future0923.debug.tools.server.http.handler.AllClassLoaderHttpHandler;
import io.github.future0923.debug.tools.common.handler.NettyPacketHandler;
import io.github.future0923.debug.tools.server.trace.TraceMethodClassFileTransformer;
import io.github.future0923.debug.tools.server.utils.BeanInstanceUtils;
import io.github.future0923.debug.tools.server.utils.DebugToolsEnvUtils;
import io.github.future0923.debug.tools.server.utils.DebugToolsResultUtils;
import io.netty.channel.ChannelHandlerContext;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;

/**
 * @author future0923
 */
public final class RunTargetMethodRequestHandler implements NettyPacketHandler<RunTargetMethodRequestPacket> {

    private static final Logger logger = Logger.getLogger(RunTargetMethodRequestHandler.class);

    public static final RunTargetMethodRequestHandler INSTANCE = new RunTargetMethodRequestHandler();

    public String methodAroundContentIdentity;

    @Override
    public void handle(ChannelHandlerContext ctx, RunTargetMethodRequestPacket packet) throws Exception {
        RunDTO runDTO = packet.getRunDTO();
        ClassLoader orgClassLoader = Thread.currentThread().getContextClassLoader();
        String targetClassName = runDTO.getTargetClassName();
        if (DebugToolsStringUtils.isBlank(targetClassName)) {
            ArgsParseException exception = new ArgsParseException("目标类为空");
            String offsetPath = RunResultDTO.genOffsetPathRandom(exception);
            DebugToolsResultUtils.putCache(offsetPath, exception);
            ctx.writeAndFlush(RunTargetMethodResponsePacket.of(runDTO, exception, offsetPath, DebugToolsBootstrap.serverConfig.getApplicationName()));
            return;
        }
        ClassLoader classLoader = null;
        if (runDTO.getClassLoader() != null && DebugToolsStringUtils.isNotBlank(runDTO.getClassLoader().getIdentity())) {
            try {
                classLoader = AllClassLoaderHttpHandler.getClassLoader(runDTO.getClassLoader().getIdentity());
            } catch (DefaultClassLoaderException e) {
                ArgsParseException exception = new ArgsParseException("未找到[" + runDTO.getClassLoader().getName() + "]类加载器");
                String offsetPath = RunResultDTO.genOffsetPathRandom(exception);
                DebugToolsResultUtils.putCache(offsetPath, exception);
                ctx.writeAndFlush(RunTargetMethodResponsePacket.of(runDTO, exception, offsetPath, DebugToolsBootstrap.serverConfig.getApplicationName()));
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
            ctx.writeAndFlush(RunTargetMethodResponsePacket.of(runDTO, e, offsetPath, DebugToolsBootstrap.serverConfig.getApplicationName()));
            return;
        }
        Method targetMethod;
        try {
            targetMethod = targetClass.getDeclaredMethod(runDTO.getTargetMethodName(), DebugToolsClassUtils.getTypes(runDTO.getTargetMethodParameterTypes()));
        } catch (NoSuchMethodException | SecurityException e) {
            ArgsParseException exception = new ArgsParseException("未找到目标方法");
            String offsetPath = RunResultDTO.genOffsetPathRandom(exception);
            DebugToolsResultUtils.putCache(offsetPath, exception);
            ctx.writeAndFlush(RunTargetMethodResponsePacket.of(runDTO, exception, offsetPath, DebugToolsBootstrap.serverConfig.getApplicationName()));
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
                ctx.writeAndFlush(RunTargetMethodResponsePacket.of(runDTO, exception, offsetPath, DebugToolsBootstrap.serverConfig.getApplicationName()));
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
        Class<?> aroundClass = DebugToolsClassUtils.loadClass(RunMethodAround.class.getName(), classLoader);
        if (StrUtil.isNotBlank(runDTO.getMethodAroundContent()) && !StrUtil.equals(methodAroundContentIdentity, runDTO.getMethodAroundContentIdentity())) {
            methodAroundContentIdentity = runDTO.getMethodAroundContentIdentity();
            Instrumentation instrumentation = DebugToolsBootstrap.INSTANCE.getInstrumentation();
            DynamicCompiler dynamicCompiler = new DynamicCompiler(classLoader);
            dynamicCompiler.addSource(RunMethodAround.class.getName(), runDTO.getMethodAroundContent());
            instrumentation.redefineClasses(new ClassDefinition(aroundClass, dynamicCompiler.buildByteCodes().get(RunMethodAround.class.getName())));
        }
        Object aroundInstance = aroundClass.getConstructor().newInstance();
        ReflectUtil.invoke(
                aroundInstance,
                ReflectUtil.getMethod(aroundClass, "onBefore", Map.class, String.class, String.class, String.class, List.class, Object[].class),
                runDTO.getHeaders(),
                runDTO.getXxlJobParam(),
                runDTO.getTargetClassName(),
                runDTO.getTargetMethodName(),
                runDTO.getTargetMethodParameterTypes(),
                targetMethodArgs
        );
        Object result = null;
        Throwable throwable = null;
        try {
            RunMethodContext.setRunMethod(targetClassName, runDTO.getTargetMethodName());
            result = run(bridgedMethod, instance, targetMethodArgs, runDTO, ctx, traceMethod);
            ReflectUtil.invoke(
                    aroundInstance,
                    ReflectUtil.getMethod(aroundClass, "onAfter", Map.class, String.class, String.class, String.class, List.class, Object[].class, Object.class),
                    runDTO.getHeaders(),
                    runDTO.getXxlJobParam(),
                    runDTO.getTargetClassName(),
                    runDTO.getTargetMethodName(),
                    runDTO.getTargetMethodParameterTypes(),
                    targetMethodArgs,
                    result
            );
        } catch (Exception e) {
            logger.error("invoke target method error", e);
            throwable = e.getCause();
            if (throwable == null) {
                throwable = e;
            }
            String offsetPath = RunResultDTO.genOffsetPathRandom(throwable);
            DebugToolsResultUtils.putCache(offsetPath, throwable);
            ctx.writeAndFlush(RunTargetMethodResponsePacket.of(runDTO, throwable, offsetPath, DebugToolsBootstrap.serverConfig.getApplicationName()));
            ReflectUtil.invoke(
                    aroundInstance,
                    ReflectUtil.getMethod(aroundClass, "onException", Map.class, String.class, String.class, String.class, List.class, Object[].class, Exception.class),
                    runDTO.getHeaders(),
                    runDTO.getXxlJobParam(),
                    runDTO.getTargetClassName(),
                    runDTO.getTargetMethodName(),
                    runDTO.getTargetMethodParameterTypes(),
                    targetMethodArgs,
                    throwable
            );
        } finally {
            ReflectUtil.invoke(
                    aroundInstance,
                    ReflectUtil.getMethod(aroundClass, "onFinally", Map.class, String.class, String.class, String.class, List.class, Object[].class, Object.class, Exception.class),
                    runDTO.getHeaders(),
                    runDTO.getXxlJobParam(),
                    runDTO.getTargetClassName(),
                    runDTO.getTargetMethodName(),
                    runDTO.getTargetMethodParameterTypes(),
                    targetMethodArgs,
                    result,
                    throwable
            );
            RunMethodContext.clear();
            Thread.currentThread().setContextClassLoader(orgClassLoader);
        }
    }

    private Object run(Method bridgedMethod, Object instance, Object[] targetMethodArgs, RunDTO runDTO, ChannelHandlerContext ctx, Boolean traceMethod) throws Exception {
        boolean voidType = void.class.isAssignableFrom(bridgedMethod.getReturnType()) || Void.class.isAssignableFrom(bridgedMethod.getReturnType());
        if (instance instanceof Proxy) {
            InvocationHandler invocationHandler = Proxy.getInvocationHandler(instance);
            if (DebugToolsEnvUtils.isAopProxy(invocationHandler)) {
                try {
                    long start = System.currentTimeMillis();
                    Object result = invocationHandler.invoke(instance, bridgedMethod, targetMethodArgs);
                    long end = System.currentTimeMillis();
                    printResult(result, end - start, runDTO, ctx, voidType, traceMethod);
                    return result;
                } catch (Throwable ignored) {
                }
            }
        }
        long start = System.currentTimeMillis();
        Object result = bridgedMethod.invoke(instance, targetMethodArgs);
        long end = System.currentTimeMillis();
        printResult(result, end - start, runDTO, ctx, voidType, traceMethod);
        return result;
    }

    private void printResult(Object result, Long duration, RunDTO runDTO, ChannelHandlerContext ctx, boolean voidType, boolean traceMethod) {
        RunTargetMethodResponsePacket packet = new RunTargetMethodResponsePacket();
        packet.setRunInfo(runDTO, DebugToolsBootstrap.serverConfig.getApplicationName());
        packet.setDuration(duration);
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
        ctx.writeAndFlush(packet);
    }

}
