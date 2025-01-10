package io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.command;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.bean.ConfigurationProxy;

/**
 * @author future0923
 */
public class MyBatisConfigurationCommand {

    private static final Logger logger = Logger.getLogger(MyBatisConfigurationCommand.class);

    public static void reloadConfiguration() {
        ConfigurationProxy.refreshProxiedConfigurations();
        logger.reload("MyBatis configuration refreshed.");
    }
}
