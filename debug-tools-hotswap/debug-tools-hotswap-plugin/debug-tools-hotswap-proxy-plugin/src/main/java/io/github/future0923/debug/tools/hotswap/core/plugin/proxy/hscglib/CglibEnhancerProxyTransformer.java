package io.github.future0923.debug.tools.hotswap.core.plugin.proxy.hscglib;

import io.github.future0923.debug.tools.hotswap.core.javassist.ClassPool;
import io.github.future0923.debug.tools.hotswap.core.plugin.proxy.api.ProxyBytecodeGenerator;
import io.github.future0923.debug.tools.hotswap.core.plugin.proxy.api.ProxyBytecodeTransformer;
import io.github.future0923.debug.tools.hotswap.core.plugin.proxy.api.SingleStepProxyTransformer;

/**
 * 重新定义了 Cglib 增强器代理类。使用 CglibEnhancerProxyBytecodeGenerator 进行字节码生成。
 *
 * @author future0923
 */
public class CglibEnhancerProxyTransformer extends SingleStepProxyTransformer {

    private final GeneratorParams params;
    private final ClassLoader loader;

    public CglibEnhancerProxyTransformer(Class<?> classBeingRedefined,
                                         ClassPool classPool,
                                         byte[] classfileBuffer,
                                         ClassLoader loader,
                                         GeneratorParams params) {
        super(classBeingRedefined, classPool, classfileBuffer);
        this.loader = loader;
        this.params = params;
    }

    public static byte[] transform(Class<?> classBeingRedefined,
                                   ClassPool classPool,
                                   byte[] classfileBuffer,
                                   ClassLoader loader,
                                   GeneratorParams params) throws Exception {
        return new CglibEnhancerProxyTransformer(classBeingRedefined, classPool, classfileBuffer, loader, params).transformRedefine();
    }

    @Override
    protected ProxyBytecodeGenerator createGenerator() {
        return new CglibEnhancerProxyBytecodeGenerator(params, loader);
    }

    @Override
    protected ProxyBytecodeTransformer createTransformer() {
        return new CglibProxyBytecodeTransformer(classPool);
    }
}
