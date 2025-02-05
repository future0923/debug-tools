package io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.command;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.patch.MyBatisSpringPatcher;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.reload.MyBatisSpringMapperReload;

/**
 * 重载 MyBatis Spring mapper 类命令
 *
 * @author future0923
 */
public class MyBatisSpringMapperReloadCommand {

    private static final Logger logger = Logger.getLogger(MyBatisSpringMapperReloadCommand.class);

    /**
     * 当class重新定义时，通过{@link MyBatisSpringPatcher#redefineMyBatisSpringMapper}创建命令后调用这
     */
    public static void reloadConfiguration(String className) {
        try {
            MyBatisSpringMapperReload.INSTANCE.reload(className);
        } catch (Exception e) {
            logger.error("reloadConfiguration error", e);
        }
    }
}
