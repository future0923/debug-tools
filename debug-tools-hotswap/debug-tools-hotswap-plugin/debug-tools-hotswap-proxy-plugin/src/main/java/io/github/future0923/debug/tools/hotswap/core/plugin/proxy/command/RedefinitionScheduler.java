package io.github.future0923.debug.tools.hotswap.core.plugin.proxy.command;

import io.github.future0923.debug.tools.hotswap.core.annotation.Init;
import io.github.future0923.debug.tools.hotswap.core.plugin.proxy.api.MultistepProxyTransformer;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;

/**
 * 为 MultistepProxyTransformer 安排一个新的重定义事件。
 *
 * @author future0923
 */
public class RedefinitionScheduler implements Runnable{

    private final MultistepProxyTransformer transformer;

    @Init
    private static Instrumentation instrumentation;

    public RedefinitionScheduler(MultistepProxyTransformer transformer) {
        this.transformer = transformer;
    }

    @Override
    public void run() {
        try {
            instrumentation.redefineClasses(new ClassDefinition(transformer.getClassBeingRedefined(), transformer.getClassfileBuffer()));
        } catch (Throwable t) {
            transformer.removeClassState();
            throw new RuntimeException(t);
        }
    }

    public static void schedule(MultistepProxyTransformer multistepProxyTransformer) {
        new Thread(new RedefinitionScheduler(multistepProxyTransformer)).start();
    }
}
