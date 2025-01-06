package io.github.future0923.debug.tools.hotswap.core.plugin.proxy.api;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.javassist.ClassPool;

/**
 * 可以一步完成的代理转换
 *
 * @author future0923
 */
public abstract class SingleStepProxyTransformer extends AbstractProxyTransformer {

    private static final Logger LOGGER = Logger.getLogger(SingleStepProxyTransformer.class);

    protected byte[] classfileBuffer;

    public SingleStepProxyTransformer(Class<?> classBeingRedefined, ClassPool classPool, byte[] classfileBuffer) {
        super(classBeingRedefined, classPool);
        this.classfileBuffer = classfileBuffer;
    }

    /**
     * 处理当前的转换状态
     */
    public byte[] transformRedefine() throws Exception {
        if (!isTransformingNeeded()) {
            return classfileBuffer;
        }
        classfileBuffer = getTransformer().transform(getGenerator().generate());
        LOGGER.reload("Class '{}' has been reloaded.", classBeingRedefined.getName());
        return classfileBuffer;
    }
}
