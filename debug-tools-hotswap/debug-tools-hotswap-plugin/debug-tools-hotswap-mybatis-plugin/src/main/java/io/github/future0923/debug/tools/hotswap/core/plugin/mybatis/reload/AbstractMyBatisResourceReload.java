/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.reload;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.scanner.ClassPathBeanDefinitionScannerAgent;
import io.github.future0923.debug.tools.hotswap.core.util.ReflectionHelper;
import org.mybatis.spring.mapper.ClassPathMapperScanner;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * @author future0923
 */
public abstract class AbstractMyBatisResourceReload<T> implements MyBatisResourceReload {

    private static final Logger logger = Logger.getLogger(AbstractMyBatisResourceReload.class);

    @Override
    @SuppressWarnings("unchecked")
    public void reload(Object object) throws Exception {
        doReload((T) object);
    }

    protected abstract void doReload(T object) throws Exception;

    /**
     * 这块是mybatis接口的生成代理类的原理
     */
    protected void mybatisBeanDefinition(ClassPathMapperScanner mapperScanner, BeanDefinitionHolder holder){
        try{
            Set<BeanDefinitionHolder> holders = new HashSet<>();
            holders.add(holder);
            Class<?> classPathMapperScanner = Class.forName("org.mybatis.spring.mapper.ClassPathMapperScanner");
            Method method = classPathMapperScanner.getDeclaredMethod("processBeanDefinitions", Set.class);
            boolean isAccess = method.isAccessible();
            method.setAccessible(true);
            method.invoke(mapperScanner, holders);
            method.setAccessible(isAccess);
        }catch (Exception e) {
            logger.error("freshMyBatis err",e);
        }
    }

    protected void defineBean(String className, byte[] bytes, String path) throws IOException {
        ClassPathMapperScanner mapperScanner = MyBatisSpringResourceManager.getMapperScanner();
        if (mapperScanner == null) {
            logger.debug("mapperScanner is null");
            return;
        }
        ClassPathBeanDefinitionScannerAgent scannerAgent = ClassPathBeanDefinitionScannerAgent.getInstance(mapperScanner);
        BeanDefinition beanDefinition = scannerAgent.resolveBeanDefinition(bytes);
        if (beanDefinition == null) {
            logger.error("not found beanDefinition:{}", className);
            return;
        }
        scannerAgent.defineBean(beanDefinition, path);
        BeanNameGenerator beanNameGenerator = (BeanNameGenerator) ReflectionHelper.get(mapperScanner, "beanNameGenerator");
        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) ReflectionHelper.get(scannerAgent, "registry");
        String beanName = beanNameGenerator.generateBeanName(beanDefinition, registry);
        BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(beanDefinition, beanName);
        mybatisBeanDefinition(mapperScanner, definitionHolder);
        logger.reload("register mapper {} in spring bean", className);
    }
}
