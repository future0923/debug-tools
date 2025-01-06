package io.github.future0923.debug.tools.hotswap.core.plugin.proxy.api;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.javassist.ClassPool;
import io.github.future0923.debug.tools.hotswap.core.plugin.proxy.command.RedefinitionScheduler;

import java.util.Map;

/**
 * 多步骤代理重新定义策略。使用Instrumentation来调度和运行下一步操作。
 *
 * @author future0923
 */
public abstract class MultistepProxyTransformer extends AbstractProxyTransformer {

    private static final Logger LOGGER = Logger.getLogger(MultistepProxyTransformer.class);
    public static boolean addThirdStep = false;

    protected byte[] classfileBuffer;
    protected Map<Class<?>, TransformationState> transformationStates;

    public MultistepProxyTransformer(Class<?> classBeingRedefined, ClassPool classPool, byte[] classfileBuffer,
                                     Map<Class<?>, TransformationState> transformationStates) {
        super(classBeingRedefined, classPool);
        this.classPool = classPool;
        this.transformationStates = transformationStates;
        this.classfileBuffer = classfileBuffer;
    }

    /**
     * 处理当前的转换状态
     */
    public byte[] transformRedefine() throws Exception {
        switch (getTransformationstate()) {
            case NEW:
                if (!isTransformingNeeded()) {
                    return classfileBuffer;
                }
                setClassAsWaiting();
                // 无法在此事件中进行转换，因为看不到类定义中的更改。安排一个新的重新定义事件。
                scheduleRedefinition();
                return classfileBuffer;
            case WAITING:
                classfileBuffer = getTransformer().transform(getGenerator().generate());
                LOGGER.reload("Class '{}' has been reloaded.", classBeingRedefined.getName());
                if (addThirdStep) {
                    setClassAsFinished();
                    scheduleRedefinition();
                } else
                    removeClassState();
                return classfileBuffer;
            case FINISHED:
                removeClassState();
                return classfileBuffer;
            default:
                throw new RuntimeException("Unhandled TransformationState!");
        }
    }

    /**
     * 当前classBeingRedefined的状态
     */
    protected TransformationState getTransformationstate() {
        TransformationState transformationState = transformationStates.get(classBeingRedefined);
        if (transformationState == null) {
            transformationState = TransformationState.NEW;
        }
        return transformationState;
    }

    /**
     * 为classBeingRedefined生成一个新的redefine事件
     */
    protected void scheduleRedefinition() {
        RedefinitionScheduler.schedule(this);
    }

    /**
     * 设置 classBeingRedefined 为 WAITING
     */
    protected TransformationState setClassAsWaiting() {
        return transformationStates.put(classBeingRedefined, TransformationState.WAITING);
    }

    /**
     * 设置 classBeingRedefined 为 FINISHED
     */
    protected TransformationState setClassAsFinished() {
        return transformationStates.put(classBeingRedefined, TransformationState.FINISHED);
    }

    /**
     * 删除与 classBeingRedefined 相关的任何状态。
     */
    public TransformationState removeClassState() {
        return transformationStates.remove(classBeingRedefined);
    }

    /**
     * 此实例正在重新定义的类
     */
    public Class<?> getClassBeingRedefined() {
        return classBeingRedefined;
    }

    /**
     * 此实例正在重新定义的类的字节码。
     */
    public byte[] getClassfileBuffer() {
        return classfileBuffer;
    }
}
