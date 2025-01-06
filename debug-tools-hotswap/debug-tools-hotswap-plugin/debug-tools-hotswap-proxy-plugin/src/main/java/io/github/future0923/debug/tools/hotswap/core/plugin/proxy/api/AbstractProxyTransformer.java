package io.github.future0923.debug.tools.hotswap.core.plugin.proxy.api;

import io.github.future0923.debug.tools.hotswap.core.javassist.ClassPool;
import io.github.future0923.debug.tools.hotswap.core.plugin.proxy.utils.ProxyClassSignatureHelper;

/**
 * @author future0923
 */
public abstract class AbstractProxyTransformer {

    public AbstractProxyTransformer(Class<?> classBeingRedefined, ClassPool classPool) {
        super();
        this.classBeingRedefined = classBeingRedefined;
        this.classPool = classPool;
    }

    protected ProxyBytecodeGenerator generator;
    protected ProxyBytecodeTransformer transformer;
    protected Class<?> classBeingRedefined;
    protected ClassPool classPool;

    protected ProxyBytecodeGenerator getGenerator() {
        if (generator == null) {
            generator = createGenerator();
        }
        return generator;
    }

    protected ProxyBytecodeTransformer getTransformer() {
        if (transformer == null) {
            transformer = createTransformer();
        }
        return transformer;
    }

    /**
     * 在此转换器中创建一个新的 ProxyBytecodeGenerator 实例以供使用。
     */
    protected abstract ProxyBytecodeGenerator createGenerator();

    /**
     * 在此转换器中创建一个新的 ProxyBytecodeTransformer 实例以供使用。
     */
    protected abstract ProxyBytecodeTransformer createTransformer();

    /**
     * 检查是否有需要重新定义代理的变化。
     */
    protected boolean isTransformingNeeded() {
        return ProxyClassSignatureHelper.isNonSyntheticPoolClassOrParentDifferent(classBeingRedefined, classPool);
    }

}
