package io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.command;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.MyBatisPlugin;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.reload.MyBatisSpringXmlReload;

import java.net.URL;

/**
 * 重载 MyBatis Spring xml 文件命令
 *
 * @author future0923
 */
public class MyBatisSpringXmlReloadCommand {

    private static final Logger logger = Logger.getLogger(MyBatisSpringXmlReloadCommand.class);

    /**
     * 当xml文件变化时，通过{@link MyBatisPlugin#registerResourceListeners}创建MyBatisXmlResourceRefreshCommands后调用这里
     */
    public static void reloadConfiguration(URL url) {
        try {
            MyBatisSpringXmlReload.INSTANCE.reload(url);
        } catch (Exception e) {
            logger.error("reload MyBatis spring xml error", e);
        }

    }
}
