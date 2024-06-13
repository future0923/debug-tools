package io.github.future0923.debug.power.core;

import io.github.future0923.debug.power.server.SocketServer;

import java.lang.instrument.Instrumentation;

/**
 * @author future0923
 */
public class DebugPowerBootstrap {

    private static DebugPowerBootstrap debugBootstrap;

    private final SocketServer socketServer;

    private DebugPowerBootstrap(Instrumentation instrumentation) {
        this.socketServer = new SocketServer(instrumentation);
    }

    public static synchronized DebugPowerBootstrap getInstance(Instrumentation instrumentation) {
        if (debugBootstrap == null) {
            debugBootstrap = new DebugPowerBootstrap(instrumentation);
        }
        return debugBootstrap;
    }

    public void start() {
        socketServer.start();
    }

    //public void call(String agentArgs, Instrumentation inst) throws Exception {
    //    RunDTO runDTO = parseArgs(agentArgs);
    //    Class<?> targetClass = DebugPowerClassUtils.loadClass(runDTO.getTargetClassName());
    //    Method targetMethod;
    //    try {
    //        targetMethod = targetClass.getDeclaredMethod(runDTO.getTargetMethodName(), DebugPowerClassUtils.getTypes(runDTO.getTargetMethodParameterTypes()));
    //    } catch (NoSuchMethodException | SecurityException e) {
    //        throw new ArgsParseException("未找到目标方法");
    //    }
    //
    //    setRequest(runDTO);
    //
    //    VmToolsUtils.init();
    //    Object instance = VmToolsUtils.getInstance(targetClass, targetMethod);
    //    // 获取正确的目标方法（非桥接方法）
    //    Method bridgedMethod = BridgeMethodResolver.findBridgedMethod(targetMethod);
    //    ReflectUtil.setAccessible(bridgedMethod);
    //    Object[] targetMethodArgs = DebugPowerParamConvertUtils.getArgs(bridgedMethod, runDTO.getTargetMethodContent());
    //    run(targetClass, bridgedMethod, instance, targetMethodArgs, runDTO.getRunConfigDTO());
    //}
    //
    //public static void setRequest(RunDTO runDTO) {
    //    if (runDTO.getHeaders() != null && !runDTO.getHeaders().isEmpty()) {
    //        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
    //        runDTO.getHeaders().forEach(mockHttpServletRequest::addHeader);
    //        ServletRequestAttributes requestAttributes = new ServletRequestAttributes(mockHttpServletRequest);
    //        RequestContextHolder.setRequestAttributes(requestAttributes);
    //    }
    //}
    //
    //public void run(Class<?> targetClass, Method bridgedMethod, Object instance, Object[] targetMethodArgs, RunConfigDTO configDTO) throws Exception {
    //    if (instance instanceof Proxy) {
    //        InvocationHandler invocationHandler = Proxy.getInvocationHandler(instance);
    //        if (invocationHandler instanceof AopProxy) {
    //            try {
    //                printResult(invocationHandler.invoke(instance, bridgedMethod, targetMethodArgs), configDTO);
    //                return;
    //            } catch (Throwable e) {
    //                e.printStackTrace();
    //            }
    //        }
    //    }
    //    printResult(bridgedMethod.invoke(instance, targetMethodArgs), configDTO);
    //}
    //
    //private void printResult(Object result, RunConfigDTO configDTO) {
    //    if (configDTO == null || configDTO.getPrintResultType() == null || PrintResultType.TOSTRING.equals(configDTO.getPrintResultType())) {
    //        System.out.println("DebugPower执行结果：" + result);
    //    } else if (PrintResultType.JSON.equals(configDTO.getPrintResultType())) {
    //        System.out.println("DebugPower执行结果：");
    //        System.out.println(DebugPowerJsonUtils.isTypeJSON(String.valueOf(result)) ? DebugPowerJsonUtils.toJsonPrettyStr(result) : result);
    //    } else if (PrintResultType.NO_PRINT.equals(configDTO.getPrintResultType())) {
    //
    //    }
    //}
    //
    //private static RunDTO parseArgs(String agentArgs) {
    //    try {
    //        if (StringUtils.isEmpty(agentArgs)) {
    //            ArgsParseException.throwEx("未读取到参数");
    //        }
    //        if (agentArgs.startsWith("file://")) {
    //            String agentJson = URLDecoder.decode(agentArgs.substring(7), StandardCharsets.UTF_8.name());
    //            File file = new File(agentJson);
    //            if (!file.exists()) {
    //                ArgsParseException.throwEx("文件不存在：" + agentJson);
    //            }
    //            agentArgs = DebugPowerFileUtils.getFileAsString(file);
    //        }
    //        return DebugPowerJsonUtils.toBean(agentArgs, RunDTO.class);
    //    } catch (ArgsParseException e) {
    //        throw e;
    //    } catch (Exception e) {
    //        throw new ArgsParseException(e);
    //    }
    //}
}
