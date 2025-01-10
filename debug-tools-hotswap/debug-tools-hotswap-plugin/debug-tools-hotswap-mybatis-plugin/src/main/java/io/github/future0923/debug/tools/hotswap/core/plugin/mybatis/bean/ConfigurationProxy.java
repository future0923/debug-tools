package io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.bean;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.javassist.util.proxy.Proxy;
import io.github.future0923.debug.tools.hotswap.core.javassist.util.proxy.ProxyFactory;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.register.MybatisXmlRegister;
import io.github.future0923.debug.tools.hotswap.core.util.ReflectionHelper;
import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.session.Configuration;
import sun.reflect.ReflectionFactory;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class ConfigurationProxy {

    private static final Logger logger = Logger.getLogger(ConfigurationProxy.class);

    private static final Map<XMLConfigBuilder, ConfigurationProxy> proxiedConfigurations = new HashMap<>();

    /**
     * XMLConfigBuilder 是 MyBatis 的核心类，作用是解析 XML 配置文件，并生成全局配置对象 Configuration
     */
    private final XMLConfigBuilder configBuilder;

    private Configuration configuration;

    private Configuration proxyInstance;

    private ConfigurationProxy(XMLConfigBuilder configBuilder) {
        this.configBuilder = configBuilder;
    }

    /**
     * 当 {@link MybatisXmlRegister#patchXMLConfigBuilder} 方法执行时会调用本方法设置 XMLConfigBuilder
     */
    public static ConfigurationProxy getWrapper(XMLConfigBuilder configBuilder) {
        if (!proxiedConfigurations.containsKey(configBuilder)) {
            proxiedConfigurations.put(configBuilder, new ConfigurationProxy(configBuilder));
        }
        return proxiedConfigurations.get(configBuilder);
    }

    /**
     * 当 {@link MybatisXmlRegister#patchXMLConfigBuilder} 方法执行时会调用本方法给 XMLConfigBuilder 返回代理对象
     */
    public Configuration proxy(Configuration originalConfiguration) {
        this.configuration = originalConfiguration;
        if (proxyInstance == null) {
            ProxyFactory factory = new ProxyFactory();
            factory.setSuperclass(Configuration.class);
            try {
                Constructor<?> constructor = ReflectionFactory.getReflectionFactory().newConstructorForSerialization(factory.createClass(), Object.class.getDeclaredConstructor(new Class[0]));
                proxyInstance = (Configuration) constructor.newInstance();
                ((Proxy) proxyInstance).setHandler(new ConfigurationProxyMethodHandler(configuration));
            } catch (Exception e) {
                throw new Error("Unable instantiate Configuration proxy", e);
            }
        }
        return proxyInstance;
    }

    public static void refreshProxiedConfigurations() {
        for (ConfigurationProxy wrapper : proxiedConfigurations.values())
            try {
                wrapper.refreshProxiedConfiguration();
            } catch (Exception e) {
                logger.error("refresh proxied configuration error", e);
            }
    }

    public void refreshProxiedConfiguration() {
        this.configuration = new Configuration();
        ReflectionHelper.invoke(configBuilder, MybatisXmlRegister.REFRESH_METHOD);
    }
}