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
package io.github.future0923.debug.tools.hotswap.core.plugin.feign.reload;

import io.github.future0923.debug.tools.base.constants.ProjectConstants;
import io.github.future0923.debug.tools.base.hutool.core.util.ReflectUtil;
import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.plugin.feign.FeignPlugin;
import io.github.future0923.debug.tools.hotswap.core.util.ReflectionHelper;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientFactoryBean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * feign client 热加载
 *
 * @author future0923
 */
public class FeignClientReload {

    private static final Logger logger = Logger.getLogger(FeignClientReload.class);

    private static final Set<String> RELOADING_CLASS = ConcurrentHashMap.newKeySet();

    /**
     * 重新注册FeignClient的BeanDefinition
     */
    public void reload(FeignClientReloadDTO dto) throws ClassNotFoundException, IOException {
        String className = dto.getClassName();
        if (!RELOADING_CLASS.add(className)) {
            if (ProjectConstants.DEBUG) {
                logger.info("{} is currently processing reload task.", className);
            }
            return;
        }
        try {
            ClassPathScanningCandidateComponentProvider scanner = FeignPlugin.getScanner();
            ByteArrayResource resource = new ByteArrayResource(dto.getBytes());
            MetadataReader metadataReader = scanner.getMetadataReaderFactory().getMetadataReader(resource);
            ScannedGenericBeanDefinition beanDefinition = new ScannedGenericBeanDefinition(metadataReader);
            beanDefinition.setResource(resource);
            beanDefinition.setSource(resource);
            Boolean isCandidateComponent = (Boolean) ReflectionHelper.invoke(scanner, ClassPathScanningCandidateComponentProvider.class, "isCandidateComponent", new Class[]{AnnotatedBeanDefinition.class}, beanDefinition);
            if (!isCandidateComponent) {
                return;
            }
            AnnotationMetadata annotationMetadata = beanDefinition.getMetadata();
            Map<String, Object> attributes = annotationMetadata.getAnnotationAttributes(FeignClient.class.getCanonicalName());
            String name = ReflectUtil.invoke(FeignPlugin.getFeignClientsRegistrar(), "getClientName", attributes);
            Class<?> feignClientSpecificationClass = dto.getUserClassLoader().loadClass("org.springframework.cloud.openfeign.FeignClientSpecification");
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(feignClientSpecificationClass);
            builder.addConstructorArgValue(name);
            builder.addConstructorArgValue(attributes.get("configuration"));
            FeignPlugin.getRegistry().registerBeanDefinition(name + "." + feignClientSpecificationClass.getSimpleName(),
                    builder.getBeanDefinition());
            //ReflectUtil.invoke(FeignPlugin.getFeignClientsRegistrar(), "registerFeignClient", FeignPlugin.getRegistry(), annotationMetadata, attributes);
            registerFeignClient(FeignPlugin.getRegistry(), annotationMetadata, attributes);
            logger.reload("Reload FeignClient: {}", className);
        } catch (Exception e) {
            logger.error("refresh feign client error", e);
        } finally {
            RELOADING_CLASS.remove(className);
        }
    }

    private void registerFeignClient(BeanDefinitionRegistry registry, AnnotationMetadata annotationMetadata,
                                     Map<String, Object> attributes) {
        String className = annotationMetadata.getClassName();
        Class clazz = ClassUtils.resolveClassName(className, null);
        ConfigurableBeanFactory beanFactory = registry instanceof ConfigurableBeanFactory ? (ConfigurableBeanFactory) registry : null;
        String contextId = ReflectUtil.invoke(FeignPlugin.getFeignClientsRegistrar(), "getContextId", beanFactory, attributes);
        String name = ReflectUtil.invoke(FeignPlugin.getFeignClientsRegistrar(), "getName", attributes);
        FeignClientFactoryBean factoryBean = new FeignClientFactoryBean();
        factoryBean.setBeanFactory(beanFactory);
        factoryBean.setName(name);
        factoryBean.setContextId(contextId);
        factoryBean.setType(clazz);
        BeanDefinitionBuilder definition = BeanDefinitionBuilder.genericBeanDefinition(clazz, () -> {
            factoryBean.setUrl(ReflectUtil.invoke(FeignPlugin.getFeignClientsRegistrar(), "getUrl", beanFactory, attributes));
            factoryBean.setPath(ReflectUtil.invoke(FeignPlugin.getFeignClientsRegistrar(), "getPath", beanFactory, attributes));
            factoryBean.setDecode404(Boolean.parseBoolean(String.valueOf(attributes.get("decode404"))));
            Object fallback = attributes.get("fallback");
            if (fallback != null) {
                factoryBean.setFallback(fallback instanceof Class ? (Class<?>) fallback
                        : ClassUtils.resolveClassName(fallback.toString(), null));
            }
            Object fallbackFactory = attributes.get("fallbackFactory");
            if (fallbackFactory != null) {
                factoryBean.setFallbackFactory(fallbackFactory instanceof Class ? (Class<?>) fallbackFactory
                        : ClassUtils.resolveClassName(fallbackFactory.toString(), null));
            }
            try {
                return factoryBean.getObject();
            } catch (Exception e) {
                logger.error("Failed to instantiation feign client for " + className, e);
                throw e;
            }
        });
        definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
        definition.setLazyInit(true);
        ReflectUtil.invoke(FeignPlugin.getFeignClientsRegistrar(), "validate", attributes);

        String alias = contextId + "FeignClient";
        AbstractBeanDefinition beanDefinition = definition.getBeanDefinition();
        beanDefinition.setAttribute(FactoryBean.OBJECT_TYPE_ATTRIBUTE, className);
        beanDefinition.setAttribute("feignClientsRegistrarFactoryBean", factoryBean);

        // has a default, won't be null
        boolean primary = (Boolean) attributes.get("primary");

        beanDefinition.setPrimary(primary);

        String qualifier = ReflectUtil.invoke(FeignPlugin.getFeignClientsRegistrar(), "getQualifier", attributes);
        if (StringUtils.hasText(qualifier)) {
            alias = qualifier;
        }

        BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDefinition, className, new String[] { alias });
        BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
    }
}
