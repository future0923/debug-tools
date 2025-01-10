package io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.bean;

import io.github.future0923.debug.tools.hotswap.core.javassist.util.proxy.MethodHandler;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Method;

/**
 * 将 org.apache.ibatis.builder.xml.XMLConfigBuilder 中的 configuration 对象替换为代理对象。
 *
 * @author future0923
 */
public class ConfigurationProxyMethodHandler implements MethodHandler {

    /**
     * 原来的 Configuration 对象
     */
    private final Configuration configuration;

    public ConfigurationProxyMethodHandler(Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * 调用代理对象方法时，调用原来的 Configuration 对象的方法。
     */
    @Override
    public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
        return thisMethod.invoke(configuration, args);
    }
}
