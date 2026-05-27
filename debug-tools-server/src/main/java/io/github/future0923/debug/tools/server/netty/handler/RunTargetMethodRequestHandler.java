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

import io.github.future0923.debug.tools.base.context.RunMethodContext;
import io.github.future0923.debug.tools.base.exception.DefaultClassLoaderException;
import io.github.future0923.debug.tools.base.hutool.core.util.ReflectUtil;
import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.base.utils.DebugToolsClassUtils;
import io.github.future0923.debug.tools.base.utils.DebugToolsStringUtils;
import io.github.future0923.debug.tools.common.dto.RunDTO;
import io.github.future0923.debug.tools.common.dto.RunResultDTO;
import io.github.future0923.debug.tools.common.dto.TraceMethodDTO;
import io.github.future0923.debug.tools.common.exception.ArgsParseException;
import io.github.future0923.debug.tools.common.handler.PacketHandler;
import io.github.future0923.debug.tools.common.protocal.packet.request.RunTargetMethodRequestPacket;
import io.github.future0923.debug.tools.common.protocal.packet.response.RunTargetMethodResponsePacket;
import io.github.future0923.debug.tools.server.DebugToolsBootstrap;
import io.github.future0923.debug.tools.server.http.handler.AllClassLoaderHttpHandler;
import io.github.future0923.debug.tools.server.trace.TraceMethodClassFileTransformer;
import io.github.future0923.debug.tools.server.utils.BeanInstanceUtils;
import io.github.future0923.debug.tools.server.utils.DebugToolsEnvUtils;
import io.github.future0923.debug.tools.server.utils.DebugToolsResultUtils;
import io.netty.channel.ChannelHandlerContext;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 运行方法请求
 *
 * @author future0923
 */
public class RunTargetMethodRequestHandler implements PacketHandler<RunTargetMethodRequestPacket> {

    private static final Logger logger = Logger.getLogger(RunTargetMethodRequestHandler.class);

    public static final RunTargetMethodRequestHandler INSTANCE = new RunTargetMethodRequestHandler();

    private final RunTargetMethodResultWriter resultWriter = new RunTargetMethodResultWriter();

    private final ReactiveStreamResultHandler reactiveStreamResultHandler = new ReactiveStreamResultHandler(resultWriter);

    private final RunMethodAroundInvoker aroundInvoker = new RunMethodAroundInvoker();

    public boolean cancelStream(String identity) {
        return reactiveStreamResultHandler.cancelStream(identity);
    }

    public void cancelStreamsByChannel(ChannelHandlerContext ctx) {
        reactiveStreamResultHandler.cancelStreamsByChannel(ctx);
    }

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
        Object[] targetMethodArgs = DebugToolsEnvUtils.getArgs(bridgedMethod, runDTO);

        RunMethodAroundInvoker.Invocation aroundInvocation = aroundInvoker.prepare(runDTO, classLoader, targetMethodArgs);
        Object result = null;
        Throwable throwable = null;
        try {
            RunMethodContext.setRunMethod(targetClassName, runDTO.getTargetMethodName());
            result = run(bridgedMethod, instance, targetMethodArgs, runDTO, ctx, traceMethod);
            aroundInvocation.onAfter(result);
        } catch (Exception e) {
            logger.error("invoke target method error", e);
            throwable = e.getCause();
            if (throwable == null) {
                throwable = e;
            }
            String offsetPath = RunResultDTO.genOffsetPathRandom(throwable);
            DebugToolsResultUtils.putCache(offsetPath, throwable);
            ctx.writeAndFlush(RunTargetMethodResponsePacket.of(runDTO, throwable, offsetPath, DebugToolsBootstrap.serverConfig.getApplicationName()));
            aroundInvocation.onException(throwable);
        } finally {
            aroundInvocation.onFinally(result, throwable);
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
        if (!voidType && reactiveStreamResultHandler.writeIfReactiveStreamResult(result, duration, runDTO, ctx)) {
            return;
        }
        resultWriter.writeNormalResult(result, duration, runDTO, ctx, voidType, traceMethod);
    }

}
