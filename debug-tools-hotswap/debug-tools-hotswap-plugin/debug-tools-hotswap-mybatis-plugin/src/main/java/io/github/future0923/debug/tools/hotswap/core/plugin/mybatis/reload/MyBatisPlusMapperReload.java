/*
 * Copyright (C) 2024-2025 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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

    private static final Set<String> RELOADING_CLASS = ConcurrentHashMap.newKeySet();

    private MyBatisPlusMapperReload() {
    }

    @Override
    protected void doReload(MyBatisPlusMapperReloadDTO dto) throws Exception {
        Class<?> clazz = dto.getClazz();
        String className = clazz.getName();
        // 同类中取重
        if (!RELOADING_CLASS.add(className)) {
            if (ProjectConstants.DEBUG) {
                logger.info("{} plus reload task is already running, skip.", className);
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
            // 不同类中串行
            Object lock = MyBatisUtils.getLock(className);
            synchronized (lock) {
                for (Configuration configuration : configurationList) {
                    Class<? extends Configuration> configurationClass = configuration.getClass();
                    if (configurationClass.getName().equals("com.baomidou.mybatisplus.core.MybatisConfiguration")) {
                        ReflectionHelper.invoke(configuration, configurationClass, "removeMapper", new Class[]{Class.class}, clazz);
                        ReflectionHelper.invoke(configuration, configurationClass, "addMapper", new Class[]{Class.class}, clazz);
                        defineBean(className, dto.getBytes(), dto.getPath());
                        logger.reload("reload {} in {}", className, configuration);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("refresh mybatis plus mapper error", e);
        } finally {
            RELOADING_CLASS.remove(className);
        }
    }
}
