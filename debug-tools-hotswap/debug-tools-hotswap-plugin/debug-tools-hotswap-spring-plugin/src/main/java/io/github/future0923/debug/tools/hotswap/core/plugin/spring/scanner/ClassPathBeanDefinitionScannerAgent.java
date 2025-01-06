/*
 * Copyright 2013-2019 the HotswapAgent authors.
 *
 * This file is part of HotswapAgent.
 *
 * HotswapAgent is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 2 of the License, or (at your
 * option) any later version.
 *
 * HotswapAgent is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with HotswapAgent. If not, see http://www.gnu.org/licenses/.
 */
package io.github.future0923.debug.tools.hotswap.core.plugin.spring.scanner;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.cache.ResetBeanPostProcessorCaches;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.cache.ResetRequestMappingCaches;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.cache.ResetSpringStaticCaches;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.SpringPlugin;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.getbean.ProxyReplacer;
import io.github.future0923.debug.tools.hotswap.core.util.PluginManagerInvoker;
import io.github.future0923.debug.tools.hotswap.core.util.ReflectionHelper;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.context.annotation.ScopeMetadata;
import org.springframework.context.annotation.ScopeMetadataResolver;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 处理Spring{@link ClassPathBeanDefinitionScanner}的Agent类
 * <p>ClassPathBeanDefinitionScanner 是 Spring 用于扫描类路径并注册 Bean 定义的核心类
 */
public class ClassPathBeanDefinitionScannerAgent {

    private static final Logger LOGGER = Logger.getLogger(ClassPathBeanDefinitionScannerAgent.class);

    /**
     * Spring的ClassPathBeanDefinitionScanner与热重载处理它的ClassPathBeanDefinitionScannerAgent类的映射
     */
    private static final Map<ClassPathBeanDefinitionScanner, ClassPathBeanDefinitionScannerAgent> instances = new HashMap<>();

    /**
     * Flag to check reload status.
     * In unit test we need to wait for reload finish before the test can continue. Set flag to true
     * in the test class and wait until the flag is false again.
     */
    public static boolean reloadFlag = false;

    /**
     * spring 的 ClassPathBeanDefinitionScanner class path 下要被spring管理的bean的扫描器
     */
    private final ClassPathBeanDefinitionScanner scanner;

    /**
     * 扫描的 base package 集合
     */
    private final Set<String> basePackages = new HashSet<>();

    /**
     * spring 的 BeanDefinition 注册表
     */
    private final BeanDefinitionRegistry registry;

    /**
     * spring 的 bean scope 解析器
     */
    private final ScopeMetadataResolver scopeMetadataResolver;

    /**
     * spring 的 bean name 生成器
     */
    private final BeanNameGenerator beanNameGenerator;

    /**
     * 创建处理ClassPathBeanDefinitionScanner的ClassPathBeanDefinitionScannerAgent
     */
    public static ClassPathBeanDefinitionScannerAgent getInstance(ClassPathBeanDefinitionScanner scanner) {
        ClassPathBeanDefinitionScannerAgent classPathBeanDefinitionScannerAgent = instances.get(scanner);
        // 如果有多个应用，注册表可能不同
        if (classPathBeanDefinitionScannerAgent == null || classPathBeanDefinitionScannerAgent.registry != scanner.getRegistry()) {
            instances.put(scanner, new ClassPathBeanDefinitionScannerAgent(scanner));
        }
        return instances.get(scanner);
    }

    /**
     * 通过BasePackage获取ClassPathBeanDefinitionScannerAgent
     */
    public static ClassPathBeanDefinitionScannerAgent getInstance(String basePackage) {
        for (ClassPathBeanDefinitionScannerAgent scannerAgent : instances.values()) {
            if (scannerAgent.basePackages.contains(basePackage)) {
                return scannerAgent;
            }
        }
        return null;
    }

    /**
     * 通过BasePackage获取ClassPathBeanDefinitionScannerAgent
     */
    public static List<ClassPathBeanDefinitionScannerAgent> getInstances(String basePackage) {
        List<ClassPathBeanDefinitionScannerAgent> scannerAgents = new ArrayList<>();
        for (ClassPathBeanDefinitionScannerAgent scannerAgent : instances.values()) {
            if (scannerAgent.basePackages.contains(basePackage))
                scannerAgents.add(scannerAgent);
        }
        return scannerAgents;
    }

    private ClassPathBeanDefinitionScannerAgent(ClassPathBeanDefinitionScanner scanner) {
        this.scanner = scanner;
        this.registry = scanner.getRegistry();
        this.scopeMetadataResolver = (ScopeMetadataResolver) ReflectionHelper.get(scanner, "scopeMetadataResolver");
        this.beanNameGenerator = (BeanNameGenerator) ReflectionHelper.get(scanner, "beanNameGenerator");
    }

    /**
     * 初始化basePackage通过ClassPathBeanDefinitionScanner.scan()
     *
     * @param basePackage package that Spring will scan
     */
    public void registerBasePackage(String basePackage) {
        this.basePackages.add(basePackage);
        PluginManagerInvoker.callPluginMethod(SpringPlugin.class, getClass().getClassLoader(),
                "registerComponentScanBasePackage", new Class[]{String.class}, new Object[]{basePackage});
    }

    /**
     * {@link ClassPathBeanRefreshCommand}执行时会调用这里刷新class文件
     *
     * @param basePackage     base package on witch the transformer was registered, used to obtain associated scanner.
     * @param classDefinition new class definition
     * @throws IOException error working with classDefinition
     */
    public static void refreshClass(String basePackage, byte[] classDefinition) throws IOException {
        ResetSpringStaticCaches.reset();
        List<ClassPathBeanDefinitionScannerAgent> scannerAgents = getInstances(basePackage);
        if (scannerAgents.isEmpty()) {
            LOGGER.error("basePackage '{}' not associated with any scannerAgent", basePackage);
            return;
        }
        for (ClassPathBeanDefinitionScannerAgent scannerAgent : scannerAgents) {
            BeanDefinition beanDefinition = scannerAgent.resolveBeanDefinition(classDefinition);
            if (null == beanDefinition) {
                continue;
            }
            scannerAgent.defineBean(beanDefinition);
            break;
        }
        reloadFlag = false;
    }

    /**
     * 验证BeanDefinition后，处理SpringBean的Scope生成最终要注册到SpringBean中的BeanDefinitionHolder
     *
     * @param candidate 要重载的BeanDefinition
     */
    public void defineBean(BeanDefinition candidate) {
        synchronized (ClassPathBeanDefinitionScannerAgent.class) {
            ScopeMetadata scopeMetadata = this.scopeMetadataResolver.resolveScopeMetadata(candidate);
            candidate.setScope(scopeMetadata.getScopeName());
            String beanName = this.beanNameGenerator.generateBeanName(candidate, registry);
            if (candidate instanceof AbstractBeanDefinition) {
                postProcessBeanDefinition((AbstractBeanDefinition) candidate, beanName);
            }
            if (candidate instanceof AnnotatedBeanDefinition) {
                processCommonDefinitionAnnotations((AnnotatedBeanDefinition) candidate);
            }
            removeIfExists(beanName);
            if (checkCandidate(beanName, candidate)) {
                BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(candidate, beanName);
                definitionHolder = applyScopedProxyMode(scopeMetadata, definitionHolder, registry);
                LOGGER.debug("Registering Spring bean '{}'", beanName);
                LOGGER.debug("Bean definition '{}'", beanName, candidate);
                registerBeanDefinition(definitionHolder, registry);
                DefaultListableBeanFactory bf = maybeRegistryToBeanFactory();
                if (bf != null) {
                    ResetRequestMappingCaches.reset(bf);
                }
                ProxyReplacer.clearAllProxies();
                freezeConfiguration();
                LOGGER.reload("Registered Spring bean '{}'", beanName);
            }
        }


    }

    /**
     * If registry contains the bean, remove it first (destroying existing singletons).
     *
     * @param beanName name of the bean
     */
    private void removeIfExists(String beanName) {
        if (registry.containsBeanDefinition(beanName)) {
            LOGGER.debug("Removing bean definition '{}'", beanName);
            DefaultListableBeanFactory bf = maybeRegistryToBeanFactory();
            if (bf != null) {
                ResetRequestMappingCaches.reset(bf);
            }
            registry.removeBeanDefinition(beanName);

            ResetSpringStaticCaches.reset();
            if (bf != null) {
                ResetBeanPostProcessorCaches.reset(bf);
            }
        }
    }

    private DefaultListableBeanFactory maybeRegistryToBeanFactory() {
        if (registry instanceof DefaultListableBeanFactory) {
            return (DefaultListableBeanFactory) registry;
        } else if (registry instanceof GenericApplicationContext) {
            return ((GenericApplicationContext) registry).getDefaultListableBeanFactory();
        }
        return null;
    }

    // rerun freeze configuration - this method is enhanced with cache reset
    private void freezeConfiguration() {
        if (registry instanceof DefaultListableBeanFactory) {
            ((DefaultListableBeanFactory) registry).freezeConfiguration();
        } else if (registry instanceof GenericApplicationContext) {
            (((GenericApplicationContext) registry).getDefaultListableBeanFactory()).freezeConfiguration();
        }
    }

    /**
     * 如果需要被SpringBean管理，那么通过解析byte[]生成BeanDefinition
     *
     * @return null表示不需要被SpringBean管理
     */
    public BeanDefinition resolveBeanDefinition(byte[] bytes) throws IOException {
        Resource resource = new ByteArrayResource(bytes);
        resetCachingMetadataReaderFactoryCache();
        MetadataReader metadataReader = getMetadataReader(resource);
        if (isCandidateComponent(metadataReader)) {
            ScannedGenericBeanDefinition sbd = new ScannedGenericBeanDefinition(metadataReader);
            sbd.setResource(resource);
            sbd.setSource(resource);
            if (isCandidateComponent(sbd)) {
                LOGGER.debug("Identified candidate component class '{}'", metadataReader.getClassMetadata().getClassName());
                return sbd;
            } else {
                LOGGER.debug("Ignored because not a concrete top-level class '{}'", metadataReader.getClassMetadata().getClassName());
                return null;
            }
        } else {
            LOGGER.trace("Ignored because not matching any filter '{}' ", metadataReader.getClassMetadata().getClassName());
            return null;
        }
    }

    private MetadataReaderFactory getMetadataReaderFactory() {
        return (MetadataReaderFactory) ReflectionHelper.get(scanner, "metadataReaderFactory");
    }

    private MetadataReader getMetadataReader(Resource resource) throws IOException {
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
            return getMetadataReaderFactory().getMetadataReader(resource);
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }
    }


    /**
     * MetadataReader包含了载入类的缓存,在解析BeanDefinition之前需要重置这个缓存
     */
    @SuppressWarnings("rawtypes")
    private void resetCachingMetadataReaderFactoryCache() {
        MetadataReaderFactory metadataReaderFactory = getMetadataReaderFactory();
        if (metadataReaderFactory instanceof CachingMetadataReaderFactory) {
            Map metadataReaderCache = (Map) ReflectionHelper.getNoException(metadataReaderFactory, CachingMetadataReaderFactory.class, "metadataReaderCache");
            if (metadataReaderCache == null) {
                metadataReaderCache = (Map) ReflectionHelper.getNoException(metadataReaderFactory, CachingMetadataReaderFactory.class, "classReaderCache");
            }
            if (metadataReaderCache != null) {
                metadataReaderCache.clear();
                LOGGER.trace("Cache cleared: CachingMetadataReaderFactory.clearCache()");
            } else {
                LOGGER.warning("Cache NOT cleared: neither CachingMetadataReaderFactory.metadataReaderCache nor clearCache does not exist.");
            }
        }
    }

    /**
     * SpringBean的Scope生成最终要注册到SpringBean中的BeanDefinitionHolder
     */
    private BeanDefinitionHolder applyScopedProxyMode(ScopeMetadata metadata,
                                                      BeanDefinitionHolder definition,
                                                      BeanDefinitionRegistry registry) {
        return (BeanDefinitionHolder) ReflectionHelper.invoke(
                null,
                AnnotationConfigUtils.class,
                "applyScopedProxyMode",
                new Class[]{ScopeMetadata.class, BeanDefinitionHolder.class, BeanDefinitionRegistry.class},
                metadata, definition, registry
        );
    }

    /**
     * 调用Spring的{@code ClassPathBeanDefinitionScanner#registerBeanDefinition(BeanDefinitionHolder, BeanDefinitionRegistry)} 注册Bean
     */
    private void registerBeanDefinition(BeanDefinitionHolder definitionHolder, BeanDefinitionRegistry registry) {
        ReflectionHelper.invoke(scanner, ClassPathBeanDefinitionScanner.class, "registerBeanDefinition", new Class[]{BeanDefinitionHolder.class, BeanDefinitionRegistry.class}, definitionHolder, registry);
    }

    /**
     * 调用Spring {@code ClassPathBeanDefinitionScanner#checkCandidate(String, BeanDefinition)} 对BeanName和BeanDefinition进行检查
     *
     * @return 返回true表示可以注册
     */
    private boolean checkCandidate(String beanName, BeanDefinition candidate) {
        return (Boolean) ReflectionHelper.invoke(scanner, ClassPathBeanDefinitionScanner.class, "checkCandidate", new Class[]{String.class, BeanDefinition.class}, beanName, candidate);
    }

    private void processCommonDefinitionAnnotations(AnnotatedBeanDefinition candidate) {
        ReflectionHelper.invoke(null, AnnotationConfigUtils.class, "processCommonDefinitionAnnotations", new Class[]{AnnotatedBeanDefinition.class}, candidate);
    }

    private void postProcessBeanDefinition(AbstractBeanDefinition candidate, String beanName) {
        ReflectionHelper.invoke(scanner, ClassPathBeanDefinitionScanner.class, "postProcessBeanDefinition", new Class[]{AbstractBeanDefinition.class, String.class}, candidate, beanName);
    }

    /**
     * 调用Spring {@code ClassPathScanningCandidateComponentProvider#isCandidateComponent(AnnotatedBeanDefinition)} 确定这个类是否需要被Spring Bean管理
     *
     * @return 返回true表示需要SpringBean管理
     */
    private boolean isCandidateComponent(AnnotatedBeanDefinition sbd) {
        return (Boolean) ReflectionHelper.invoke(scanner, ClassPathScanningCandidateComponentProvider.class, "isCandidateComponent", new Class[]{AnnotatedBeanDefinition.class}, sbd);
    }

    /**
     * 调用Spring {@code ClassPathScanningCandidateComponentProvider#isCandidateComponent(MetadataReader)} 确定这个类是否需要被Spring Bean管理
     *
     * @return 返回true表示需要SpringBean管理
     */
    private boolean isCandidateComponent(MetadataReader metadataReader) {
        return (Boolean) ReflectionHelper.invoke(scanner, ClassPathScanningCandidateComponentProvider.class, "isCandidateComponent", new Class[]{MetadataReader.class}, metadataReader);
    }
}