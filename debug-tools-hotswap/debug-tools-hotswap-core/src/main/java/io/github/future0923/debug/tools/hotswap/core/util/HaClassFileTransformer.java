package io.github.future0923.debug.tools.hotswap.core.util;

import io.github.future0923.debug.tools.hotswap.core.annotation.LoadEvent;

import java.lang.instrument.ClassFileTransformer;

/**
 * 热重载类文件Transformer
 */
public interface HaClassFileTransformer extends ClassFileTransformer {

    /**
     * 是否只关注{@link LoadEvent#REDEFINE}事件
     */
    boolean isForRedefinitionOnly();

}
