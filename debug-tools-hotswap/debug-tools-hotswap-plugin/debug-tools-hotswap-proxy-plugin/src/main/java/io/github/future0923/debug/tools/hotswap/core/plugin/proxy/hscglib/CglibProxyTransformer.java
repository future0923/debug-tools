package io.github.future0923.debug.tools.hotswap.core.plugin.proxy.hscglib;

import io.github.future0923.debug.tools.hotswap.core.javassist.ClassPool;
import io.github.future0923.debug.tools.hotswap.core.plugin.proxy.api.MultistepProxyTransformer;
import io.github.future0923.debug.tools.hotswap.core.plugin.proxy.api.ProxyBytecodeGenerator;
import io.github.future0923.debug.tools.hotswap.core.plugin.proxy.api.ProxyBytecodeTransformer;
import io.github.future0923.debug.tools.hotswap.core.plugin.proxy.api.TransformationState;

import java.lang.instrument.IllegalClassFormatException;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author future0923
 */
public class CglibProxyTransformer extends MultistepProxyTransformer {

    /**
     * 所有类加载器的类转换状态。用于代理类加载器。
     */
    private static final Map<Class<?>, TransformationState> TRANSFORMATION_STATES = Collections.synchronizedMap(new WeakHashMap<>());
    private final GeneratorParams params;

    public CglibProxyTransformer(Class<?> classBeingRedefined,
                                 ClassPool classPool, byte[] classfileBuffer,
                                 GeneratorParams params) {
        super(classBeingRedefined, classPool, classfileBuffer,
                TRANSFORMATION_STATES);
        this.params = params;
    }

    public static byte[] transform(Class<?> classBeingRedefined,
                                   ClassPool classPool,
                                   byte[] classfileBuffer,
                                   GeneratorParams params) throws Exception {
        return new CglibProxyTransformer(classBeingRedefined, classPool, classfileBuffer, params).transformRedefine();
    }

    public static boolean isReloadingInProgress() {
        return !TRANSFORMATION_STATES.isEmpty();
    }

    @Override
    protected ProxyBytecodeGenerator createGenerator() {
        return new CglibProxyBytecodeGenerator(params);
    }

    @Override
    protected ProxyBytecodeTransformer createTransformer() {
        return new CglibProxyBytecodeTransformer(classPool);
    }
}
