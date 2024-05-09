package io.github.future0923.debug.power.core.jvm;

import arthas.VmTool;
import com.taobao.arthas.common.OSUtils;
import io.github.future0923.debug.power.common.utils.DebugPowerAopUtils;
import io.github.future0923.debug.power.common.utils.DebugPowerFileUtils;
import io.github.future0923.debug.power.common.utils.DebugPowerSpringUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.CodeSource;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * @author future0923
 */
public class VmToolsUtils {

    private static VmTool instance;

    static {
        init();
    }

    private static void init() {
        String libName;
        if (OSUtils.isMac()) {
            libName = "libJniLibrary.dylib";
        } else if (OSUtils.isLinux()) {
            libName = "libJniLibrary-x64.so";
        } else if (OSUtils.isWindows()) {
            libName = "libJniLibrary-x64.dll";
        } else {
            throw new IllegalStateException("unsupported os");
        }

        CodeSource codeSource = VmToolsUtils.class.getProtectionDomain().getCodeSource();
        String libPath = null;
        if (codeSource != null) {
            try {
                File bootJarPath = new File(codeSource.getLocation().toURI().getSchemeSpecificPart());
                libPath = DebugPowerFileUtils.copyChildFile(bootJarPath, "lib/" + libName);
                instance = VmTool.getInstance(libPath);
            } catch (Throwable e) {
                throw new IllegalStateException(e);
            }
        }

        if (instance == null) {
            throw new IllegalStateException("VmToolUtils init fail. codeSource: " + codeSource + " libPath: " + libPath);
        }
    }

    public static Object getInstance(Class<?> targetClass, Method targetMethod) {
        Object instance = VmToolsUtils.getSpringInstance(targetClass);
        if (!Modifier.isPublic(targetMethod.getModifiers())) {
            return DebugPowerAopUtils.getTargetObject(instance);
        } else {
            return instance;
        }
    }

    /**
     * 优先通过spring 上下文获取
     */
    public static Object getSpringInstance(Class<?> clazz) {
        try {
            // 这里用的是被调用项目的ApplicationContext
            DebugPowerSpringUtils.initApplicationContexts(() -> instance.getInstances(ApplicationContext.class), () -> instance.getInstances(BeanFactory.class));
            if (DebugPowerSpringUtils.containsBean(clazz)) {
                return DebugPowerSpringUtils.getBean(clazz);
            }
        } catch (Throwable ignored) {
        }
        Object[] instances = instance.getInstances(clazz);
        if (instances.length == 0) {
            return instantiate(clazz);
        } else {
            return instances[0];
        }
    }

    public static <T> T instantiate(Class<T> clazz) {
        if (clazz == null || clazz.isInterface()) {
            throw new IllegalArgumentException("Specified class is null or interface. " + clazz);
        }
        Optional<Constructor<?>> noArgConstructorOpt = Arrays.stream(clazz.getDeclaredConstructors())
                .filter(constructor -> constructor.getParameterCount() == 0).findFirst();
        Object obj;
        try {
            if (noArgConstructorOpt.isPresent()) {
                Constructor<?> constructor = noArgConstructorOpt.get();
                if (!constructor.isAccessible()) {
                    constructor.setAccessible(true);
                }
                obj = constructor.newInstance();
            } else {
                Constructor<?> constructor = clazz.getDeclaredConstructors()[0];
                if (!constructor.isAccessible()) {
                    constructor.setAccessible(true);
                }
                Object[] objects = IntStream.range(0, constructor.getParameterCount()).mapToObj(i -> null).toArray();
                obj = constructor.newInstance(objects);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("instantiate Exception" + clazz, e);
        }
        return (T) obj;
    }
}
