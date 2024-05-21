package io.github.future0923.debug.power.attach;

import cn.hutool.core.util.ReflectUtil;
import io.github.future0923.debug.power.common.dto.RunDTO;
import io.github.future0923.debug.power.common.utils.DebugPowerClassUtils;
import io.github.future0923.debug.power.common.utils.DebugPowerFileUtils;
import io.github.future0923.debug.power.common.utils.DebugPowerJsonUtils;
import io.github.future0923.debug.power.common.utils.DebugPowerParamConvertUtils;
import io.github.future0923.debug.power.core.DebugBootstrap;
import io.github.future0923.debug.power.core.jvm.VmToolsUtils;
import io.github.future0923.debug.power.core.mock.springmvc.MockHttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * @author future0923
 */
public class DebugPowerAttach {

    public static void agentmain(String agentArgs, Instrumentation inst) throws Exception {
        if (StringUtils.isEmpty(agentArgs)) {
            System.err.println("未读取到参数");
            return;
        }
        if (agentArgs.startsWith("file://")) {
            String agentJson = URLDecoder.decode(agentArgs.substring(7), StandardCharsets.UTF_8.name());
            File file = new File(agentJson);
            if (!file.exists()) {
                System.err.println("文件不存在：" + agentJson);
                return;
            }
            agentArgs = DebugPowerFileUtils.getFileAsString(file);
        }
        RunDTO runDTO = DebugPowerJsonUtils.toBean(agentArgs, RunDTO.class);
        Class<?> targetClass = DebugPowerClassUtils.loadClass(runDTO.getTargetClassName());
        Method targetMethod;
        try {
            targetMethod = targetClass.getDeclaredMethod(runDTO.getTargetMethodName(), DebugPowerClassUtils.getTypes(runDTO.getTargetMethodParameterTypes()));
        } catch (NoSuchMethodException | SecurityException e) {
            System.err.println("未找到目标方法");
            return;
        }

        setRequest(runDTO);

        Object instance = VmToolsUtils.getInstance(targetClass, targetMethod);
        // 获取正确的目标方法（非桥接方法）
        Method bridgedMethod = BridgeMethodResolver.findBridgedMethod(targetMethod);
        ReflectUtil.setAccessible(bridgedMethod);
        Object[] targetMethodArgs = DebugPowerParamConvertUtils.getArgs(bridgedMethod, runDTO.getTargetMethodContent());
        DebugBootstrap debugBootstrap = DebugBootstrap.getInstance(inst, new HashMap<>());
        debugBootstrap.run(targetClass, bridgedMethod, instance, targetMethodArgs);
        //File debugCoreJar = new File("/Users/weilai/Downloads/debug-power/debug-power-core/target/debug-core.jar");
        //if (!debugCoreJar.exists()) {
        //    throw new IllegalStateException("debugCoreJar not exists");
        //}

        //try (DebugAttachClassloader classloader = new DebugAttachClassloader(new URL[]{debugCoreJar.toURI().toURL()})) {
        //    Class<?> bootstrapClass = classloader.loadClass("io.github.future0923.debug.power.core.DebugBootstrap");
        //    HashMap<String, String> configMap = new HashMap<>();
        //    Object bootstrap = bootstrapClass.getMethod("getInstance", Instrumentation.class, Map.class).invoke(null, inst, configMap);
        //    bootstrapClass.getMethod("run", Class.class, Method.class, Object.class, Object[].class).invoke(bootstrap, targetClass, bridgedMethod, instance, targetMethodArgs);
        //    System.out.println("agentmain end");
        //} catch (Exception e) {
        //    e.printStackTrace();
        //}
    }

    public static void setRequest(RunDTO runDTO) {
        if (runDTO.getHeaders() != null && !runDTO.getHeaders().isEmpty()) {
            MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
            runDTO.getHeaders().forEach(mockHttpServletRequest::addHeader);
            ServletRequestAttributes requestAttributes = new ServletRequestAttributes(mockHttpServletRequest);
            RequestContextHolder.setRequestAttributes(requestAttributes);
        }
    }
}
