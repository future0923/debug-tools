package io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.reload;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.dto.MyBatisPlusMapperReloadDTO;
import io.github.future0923.debug.tools.hotswap.core.util.ReflectionHelper;
import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.mapper.ClassPathMapperScanner;

import java.util.Set;

/**
 * 重载 MyBatisPlus Mapper 资源
 *
 * @author future0923
 */
public class MyBatisPlusMapperReload extends AbstractMyBatisResourceReload<MyBatisPlusMapperReloadDTO> {

    private static final Logger logger = Logger.getLogger(MyBatisPlusMapperReload.class);

    public static final MyBatisPlusMapperReload INSTANCE = new MyBatisPlusMapperReload();

    private final Object reloadLock = new Object();

    private MyBatisPlusMapperReload() {
    }

    @Override
    protected void doReload(MyBatisPlusMapperReloadDTO dto) throws Exception {
        Class<?> clazz = dto.getClazz();
        try {
            logger.debug("transform class: {}", clazz.getName());
            ClassPathMapperScanner mapperScanner = MyBatisSpringResourceManager.getMapperScanner();
            if (mapperScanner == null) {
                logger.debug("mapperScanner is null");
                return;
            }
            Set<Configuration> configurationList = MyBatisSpringResourceManager.getConfigurationList();
            if (configurationList.isEmpty()) {
                logger.debug("mybatis configuration is empty");
                return;
            }
            for (Configuration configuration : configurationList) {
                Class<? extends Configuration> configurationClass = configuration.getClass();
                if (configurationClass.getName().equals("com.baomidou.mybatisplus.core.MybatisConfiguration")) {
                    synchronized (reloadLock) {
                        ReflectionHelper.invoke(configuration, configurationClass, "removeMapper", new Class[]{Class.class}, clazz);
                        ReflectionHelper.invoke(configuration, configurationClass, "addMapper", new Class[]{Class.class}, clazz);
                        defineBean(clazz.getName(), dto.getBytes());
                        logger.reload("reload {} in {}", clazz.getName(), configuration);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("refresh mybatis error", e);
        }
    }
}
