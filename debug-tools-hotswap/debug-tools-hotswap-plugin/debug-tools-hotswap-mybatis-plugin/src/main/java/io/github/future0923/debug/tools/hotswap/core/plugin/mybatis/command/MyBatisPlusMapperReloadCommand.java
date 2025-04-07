package io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.command;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.command.MergeableCommand;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.dto.MyBatisPlusMapperReloadDTO;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.reload.MyBatisPlusMapperReload;

/**
 * 重载 MyBatis Plus Mapper 命令，支持新增
 *
 * @author future0923
 */
public class MyBatisPlusMapperReloadCommand extends MergeableCommand {

    private static final Logger logger = Logger.getLogger(MyBatisPlusMapperReloadCommand.class);

    private final ClassLoader loader;

    private final Class<?> clazz;

    private final byte[] bytes;

    /**
     * 路径(没有认为是修改)
     */
    private final String path;

    public MyBatisPlusMapperReloadCommand(ClassLoader loader, Class<?> clazz, byte[] bytes, String path) {
        this.loader = loader;
        this.clazz = clazz;
        this.bytes = bytes;
        this.path = path;
    }

    @Override
    public void executeCommand() {
        try {
            MyBatisPlusMapperReload.INSTANCE.reload(new MyBatisPlusMapperReloadDTO(loader, clazz, bytes, path));
        } catch (Exception e) {
            logger.error("refresh mybatis error", e);
        }
    }
}
