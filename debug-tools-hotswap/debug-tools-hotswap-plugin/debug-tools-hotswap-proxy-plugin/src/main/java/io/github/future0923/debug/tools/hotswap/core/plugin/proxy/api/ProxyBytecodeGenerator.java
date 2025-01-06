package io.github.future0923.debug.tools.hotswap.core.plugin.proxy.api;

/**
 * 生成新的代理类字节码
 *
 * @author future0923
 */
public interface ProxyBytecodeGenerator {

    /**
     * 生成新的代理类字节码
     */
    byte[] generate() throws Exception;
}
