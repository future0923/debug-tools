package io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.reload;

import io.github.future0923.debug.tools.base.constants.ProjectConstants;
import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.dto.MyBatisPlusMapperReloadDTO;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.utils.MyBatisUtils;
import io.github.future0923.debug.tools.hotswap.core.util.ReflectionHelper;
import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.mapper.ClassPathMapperScanner;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 重载 MyBatisPlus Mapper 资源
 *
 * @author future0923
 */
public class MyBatisPlusMapperReload extends AbstractMyBatisResourceReload<MyBatisPlusMapperReloadDTO> {

    private static final Logger logger = Logger.getLogger(MyBatisPlusMapperReload.class);

    public static final MyBatisPlusMapperReload INSTANCE = new MyBatisPlusMapperReload();

    private static final Set<String> RELOADING_CLASS = ConcurrentHashMap.newKeySet();

    private MyBatisPlusMapperReload() {
    }

    @Override
    protected void doReload(MyBatisPlusMapperReloadDTO dto) throws Exception {
        Class<?> clazz = dto.getClazz();
        String className = clazz.getName();
        if (RELOADING_CLASS.contains(className)) {
            if (ProjectConstants.DEBUG) {
                logger.info("{} is currently processing reload task.", className);
            }
            return;
        }
        try {
            logger.debug("reload class: {}", className);
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
                    synchronized (MyBatisUtils.getReloadLockObject()) {
                        if (!RELOADING_CLASS.add(className)) {
                            if (ProjectConstants.DEBUG) {
                                logger.info("{} is currently processing reload task.", className);
                            }
                            return;
                        }
                        ReflectionHelper.invoke(configuration, configurationClass, "removeMapper", new Class[]{Class.class}, clazz);
                        ReflectionHelper.invoke(configuration, configurationClass, "addMapper", new Class[]{Class.class}, clazz);
                        defineBean(className, dto.getBytes(), dto.getPath());
                        RELOADING_CLASS.remove(className);
                        logger.reload("reload {} in {}", className, configuration);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("refresh mybatis error", e);
        }
    }
}
