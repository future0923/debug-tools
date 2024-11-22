/*
 * Copyright 2013-2024 the HotswapAgent authors.
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
import io.github.future0923.debug.tools.hotswap.core.javassist.CannotCompileException;
import io.github.future0923.debug.tools.hotswap.core.javassist.ClassPool;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.SpringPlugin;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.reload.SpringChangedAgent;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.reload.SpringChangedReloadCommand;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.transformers.ClassPathBeanDefinitionScannerTransformer;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.utils.RegistryUtils;
import io.github.future0923.debug.tools.hotswap.core.util.PluginManagerInvoker;
import io.github.future0923.debug.tools.hotswap.core.util.ReflectionHelper;
import io.github.future0923.debug.tools.hotswap.core.util.spring.util.ObjectUtils;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.context.annotation.ScopeMetadata;
import org.springframework.context.annotation.ScopeMetadataResolver;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
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

    private ClassPathBeanDefinitionScannerAgent(ClassPathBeanDefinitionScanner scanner) {
        this.scanner = scanner;
        this.registry = scanner.getRegistry();
        this.scopeMetadataResolver = (ScopeMetadataResolver) ReflectionHelper.get(scanner, "scopeMetadataResolver");
        this.beanNameGenerator = (BeanNameGenerator) ReflectionHelper.get(scanner, "beanNameGenerator");
    }

    /**
     * 初始化注册Spring扫描的base package，目前{@link ClassPathBeanDefinitionScannerTransformer#transform}方法会调用这里注册
     */
    public void registerBasePackage(String basePackage) {
        this.basePackages.add(basePackage);
        PluginManagerInvoker.callPluginMethod(SpringPlugin.class, getClass().getClassLoader(), "registerComponentScanBasePackage", new Class[]{String.class}, new Object[]{basePackage});
    }

    /**
     * {@link ClassPathBeanRefreshCommand#executeCommand()}调用这个方法
     * <p>通过classDefinition的byte[]为新Bean创建beanDefinition，并添加到新注册中。
     * <p>当{@link SpringChangedReloadCommand}命令执行刷新Spring环境的时候{@code SpringBeanReload#refreshNewBean()}方法会处理新增的Bean
     *
     * @return 是否添加成功
     */
    public static boolean refreshClassAndCheckReload(ClassLoader appClassLoader, String basePackage, String clazzName, byte[] classDefinition) throws IOException {
        ClassPathBeanDefinitionScannerAgent scannerAgent = getInstance(basePackage);
        if (scannerAgent == null) {
            LOGGER.error("basePackage '{}' not associated with any scannerAgent", basePackage);
            return false;
        }
        return scannerAgent.createBeanDefinitionAndCheckReload(appClassLoader, clazzName, classDefinition);
    }

    /**
     * 通过classDefinition为新Bean创建beanDefinition，并添加到新注册中。
     * <p>当{@link SpringChangedReloadCommand}命令执行刷新Spring环境的时候{@code SpringBeanReload#refreshNewBean()}方法会处理新增的Bean
     *
     * @return 是否添加成功
     */
    private boolean createBeanDefinitionAndCheckReload(ClassLoader appClassLoader, String clazzName, byte[] classDefinition) throws IOException {
        DefaultListableBeanFactory defaultListableBeanFactory = RegistryUtils.maybeRegistryToBeanFactory(registry);
        // 如果bean已经存在，添加到Spring要重载的bean中
        if (doProcessWhenBeanExist(defaultListableBeanFactory, appClassLoader, clazzName, classDefinition)) {
            LOGGER.debug("the class '{}' is exist at '{}', it will not create new BeanDefinition", clazzName, ObjectUtils.identityToString(defaultListableBeanFactory));
            return true;
        }
        BeanDefinition beanDefinition = resolveBeanDefinition(appClassLoader, classDefinition);
        if (beanDefinition == null) {
            return false;
        }
        String beanName = this.beanNameGenerator.generateBeanName(beanDefinition, registry);
        // BeanName如果存在则不处理
        if (registry.containsBeanDefinition(beanName)) {
            LOGGER.debug("Bean definition '{}' already exists", beanName);
            return false;
        }
        // 定义Bean
        BeanDefinitionHolder beanDefinitionHolder = defineBean(beanDefinition);
        if (beanDefinitionHolder != null) {
            LOGGER.reload("Registering Spring bean '{}'", beanName);
            if (defaultListableBeanFactory != null) {
                SpringChangedAgent.addNewBean(beanDefinitionHolder, defaultListableBeanFactory);
                return true;
            }
        }
        return false;
    }

    /**
     * 如果bean已经存在，添加到Spring要重载的bean中
     */
    private boolean doProcessWhenBeanExist(DefaultListableBeanFactory defaultListableBeanFactory,
                                           ClassLoader appClassLoader,
                                           String clazzName, byte[] classDefinition) {
        try {
            Class<?> clazz = loadClass(appClassLoader, clazzName, classDefinition);
            if (defaultListableBeanFactory != null && clazz != null) {
                String[] beanNames = defaultListableBeanFactory.getBeanNamesForType(clazz);
                if (beanNames.length != 0) {
                    SpringChangedAgent.addChangedClass(clazz, defaultListableBeanFactory);
                    return true;
                }
            }
        } catch (Exception t) {
            LOGGER.debug("make class failed", t);
        }
        return false;
    }

    /**
     * 载入class
     * <p>先从ClassLoader中加载，
     * <p>找不到通过javassist解析，从ClassLoader中记载javassist解析的class，
     * <p>找不到通过javassist生成class加载到jvm中
     */
    private Class<?> loadClass(ClassLoader appClassLoader,
                               String clazzName,
                               byte[] classDefinition) {
        Class<?> clazz = doLoadClass(appClassLoader, clazzName);
        if (clazz != null) {
            return clazz;
        }
        ClassPool pool = ClassPool.getDefault();
        try {
            CtClass ctClass = pool.makeClass(new ByteArrayInputStream(classDefinition));
            clazz = doLoadClass(appClassLoader, ctClass.getName());
            if (clazz != null) {
                return clazz;
            }
            return ctClass.toClass(appClassLoader, registry.getClass().getProtectionDomain());
        } catch (IOException | CannotCompileException e) {
            LOGGER.trace("make new class failed, {}", e.getMessage());
            return null;
        }

    }

    /**
     * 从ClassLoader中加载class
     */
    private Class<?> doLoadClass(ClassLoader appClassLoader,
                                 String clazzName) {
        try {
            if (clazzName == null || clazzName.isEmpty()) {
                return null;
            }
            String realClassName = clazzName.replaceAll("/", ".");
            return appClassLoader.loadClass(realClassName);
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            // ignore
        }
        return null;
    }

    /**
     * 验证BeanDefinition后，处理SpringBean的Scope生成最终要注册到SpringBean中的BeanDefinitionHolder
     *
     * @param candidate 要重载的BeanDefinition
     * @return BeanDefinitionHolder是Bean最终要注册到SpringBean中的BeanDefinition
     */
    public BeanDefinitionHolder defineBean(BeanDefinition candidate) {
        synchronized (ClassPathBeanDefinitionScannerAgent.class) {
            ScopeMetadata scopeMetadata = this.scopeMetadataResolver.resolveScopeMetadata(candidate);
            candidate.setScope(scopeMetadata.getScopeName());
            String beanName = this.beanNameGenerator.generateBeanName(candidate, registry);
            if (checkCandidate(beanName, candidate)) {
                BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(candidate, beanName);
                definitionHolder = applyScopedProxyMode(scopeMetadata, definitionHolder, registry);
                LOGGER.debug("Bean definition '{}'", beanName, candidate);
                return definitionHolder;
            }
            return null;
        }
    }

    /**
     * 如果需要被SpringBean管理，那么通过解析byte[]生成BeanDefinition
     *
     * @return null表示不需要被SpringBean管理
     */
    public BeanDefinition resolveBeanDefinition(ClassLoader appClassLoader, byte[] bytes) throws IOException {
        Resource resource = new ByteArrayResource(bytes);
        resetCachingMetadataReaderFactoryCache();
        MetadataReader metadataReader = getMetadataReader(appClassLoader, resource);
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
            LOGGER.debug("Ignored because not matching any filter '{}' ", metadataReader.getClassMetadata().getClassName());
            return null;
        }
    }

    /**
     * 获取MetadataReader
     */
    private MetadataReader getMetadataReader(ClassLoader appClassLoader, Resource resource) throws IOException {
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(appClassLoader);
            return getMetadataReaderFactory().getMetadataReader(resource);
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }
    }

    /**
     * 调用Spring {@code ClassPathBeanDefinitionScanner#metadataReaderFactory}获取MetadataReader
     */
    private MetadataReaderFactory getMetadataReaderFactory() {
        return (MetadataReaderFactory) ReflectionHelper.get(scanner, "metadataReaderFactory");
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
