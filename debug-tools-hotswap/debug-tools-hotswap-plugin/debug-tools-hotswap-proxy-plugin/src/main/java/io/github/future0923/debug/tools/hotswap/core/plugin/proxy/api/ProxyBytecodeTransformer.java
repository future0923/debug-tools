package io.github.future0923.debug.tools.hotswap.core.plugin.proxy.api;

/**
 * 转换新的代理定义的字节码，以便在重新定义后使用。
 *
 * @author future0923
 */
public interface ProxyBytecodeTransformer {

    String INIT_FIELD_PREFIX = "initCalled";

    byte[] transform(byte[] classfileBuffer) throws Exception;
}
