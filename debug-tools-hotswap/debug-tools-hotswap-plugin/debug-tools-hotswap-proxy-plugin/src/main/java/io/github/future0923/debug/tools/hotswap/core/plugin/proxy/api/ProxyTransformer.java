package io.github.future0923.debug.tools.hotswap.core.plugin.proxy.api;

/**
 * redefine proxy
 *
 * @author future0923
 */
public interface ProxyTransformer {

    byte[] transformRedefine() throws Exception;
}
