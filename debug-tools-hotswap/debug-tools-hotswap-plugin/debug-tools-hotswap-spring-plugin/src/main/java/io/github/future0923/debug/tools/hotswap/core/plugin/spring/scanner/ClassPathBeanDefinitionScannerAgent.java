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
package io.github.future0923.debug.tools.hotswap.core.plugin.spring.scanner;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.javassist.ClassPool;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.SpringPlugin;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.cache.ResetBeanPostProcessorCaches;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.cache.ResetRequestMappingCaches;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.cache.ResetSpringStaticCaches;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.getbean.ProxyReplacer;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.patch.ClassPathBeanDefinitionScannerPatcher;
import io.github.future0923.debug.tools.hotswap.core.util.PluginManagerInvoker;
import io.github.future0923.debug.tools.hotswap.core.util.ReflectionHelper;
import io.github.future0923.debug.tools.hotswap.core.util.spring.util.CollectionUtils;
import io.github.future0923.debug.tools.hotswap.core.util.spring.util.ReflectionUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * 处理Spring{@link ClassPathBeanDefinitionScanner}的Agent类
 * <p>ClassPathBeanDefinitionScanner 是 Spring 用于扫描类路径并注册 Bean 定义的核心类
 */
public class ClassPathBeanDefinitionScannerAgent {

    private static final Logger logger = Logger.getLogger(ClassPathBeanDefinitionScannerAgent.class);

    /**
     * Spring的ClassPathBeanDefinitionScanner与热重载处理它的ClassPathBeanDefinitionScannerAgent类的映射
     */
    private static final Map<ClassPathBeanDefinitionScanner, ClassPathBeanDefinitionScannerAgent> instances = new ConcurrentHashMap<>(16);

    /**
     * path 与 bean name 的映射
     */
    private static final Map<String, Set<String>> pathBeanNameMapping = new ConcurrentHashMap<>(256);

    /**
     * 已经删除的BeanName集合
     */
    private static final Set<String> deleteBeanNameSet = new ConcurrentSkipListSet<>();

    public static final Map<String, Map<String, String>> autoWiredDependencyMap = new ConcurrentHashMap<>();

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
     * {@link ClassPathBeanDefinitionScannerPatcher#transform(CtClass, ClassPool)}
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
            if (scannerAgent.basePackages.contains(basePackage)) {
                scannerAgents.add(scannerAgent);
            }
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
     * 初始化文件path与beanName的映射，在{@link SpringPlugin#patchAbstractApplicationContext(CtClass, ClassPool)}处调用
     */
    @SuppressWarnings("unchecked")
    public static void initPathBeanNameMapping() {
        for (ClassPathBeanDefinitionScannerAgent value : instances.values()) {
            DefaultListableBeanFactory defaultListableBeanFactory = value.maybeRegistryToBeanFactory();
            Map<String, BeanDefinition> beanDefinitionMap = (Map<String, BeanDefinition>) ReflectionHelper.get(defaultListableBeanFactory, "beanDefinitionMap");
            for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()) {
                String beanName = entry.getKey();
                BeanDefinition beanDefinition = entry.getValue();
                resolvePath(beanDefinition, beanName);
            }
        }
    }

    private static void resolvePath(String path, String beanName) {
        if (path != null) {
            Set<String> beanNameList = pathBeanNameMapping.computeIfAbsent(path, k -> new HashSet<>());
            beanNameList.add(beanName);
        }
    }

    private static void resolvePath(BeanDefinition beanDefinition, String beanName) {
        Resource resource = (Resource) ReflectionHelper.get(beanDefinition, "resource");
        if (resource == null) {
            return;
        }
        String path = null;
        try {
            if ("jar".equals(resource.getURL().getProtocol())) {
                return;
            }
            path = resource.getURL().getPath();
        } catch (Exception e) {
            try {
                if ("jar".equals(resource.getURI().getScheme())) {
                    return;
                }
                path = resource.getURI().getPath();
            } catch (Exception ex) {
                try {
                    path = resource.getFile().getAbsolutePath();
                } catch (Exception ignore) {
                }
            }
        }
        resolvePath(path, beanName);
    }

    /**
     * 通过dir path移除beanDefinition
     */
    public static void removeBeanDefinitionByDirPath(String path) {
        Set<String> beanNameSet = new HashSet<>();
        pathBeanNameMapping.forEach((k, v) -> {
            if (k.startsWith(path + File.separator)) {
                beanNameSet.addAll(v);
            }
        });
        addDeleteBeanNameSet(beanNameSet);
        //removeBeanDefinition(beanNameSet, path);
    }

    /**
     * 通过file path移除beanDefinition
     */
    public static void removeBeanDefinitionByFilePath(String path) {
        Set<String> beanNameSet = pathBeanNameMapping.get(path);
        if (CollectionUtils.isEmpty(beanNameSet)) {
            return;
        }
        addDeleteBeanNameSet(beanNameSet);
        //removeBeanDefinition(beanNameSet, path);
    }

    /**
     * 通过beanName移除beanDefinition
     */
    public static void removeBeanDefinition(Collection<String> beanNameSet, String path) {
        for (ClassPathBeanDefinitionScannerAgent value : instances.values()) {
            DefaultListableBeanFactory defaultListableBeanFactory = value.maybeRegistryToBeanFactory();
            if (defaultListableBeanFactory != null) {
                for (String beanName : beanNameSet) {
                    try {
                        defaultListableBeanFactory.removeBeanDefinition(beanName);
                        logger.info("remove bean name {} by delete path {}", beanName, path);
                    } catch (NoSuchBeanDefinitionException ignored) {

                    }
                }
            }
        }
    }

    /**
     * 设置删除的
     */
    public static void addDeleteBeanNameSet(Set<String> beanNameSet) {
        deleteBeanNameSet.addAll(beanNameSet);
    }

    /**
     * 新增Bean的时候移除已经删除的beanName
     */
    public static void removeDeleteBeanNameSet(String beanName) {
        deleteBeanNameSet.remove(beanName);
    }

    /**
     * 过滤掉已经删除的beanName
     * {@link SpringPlugin#patchAbstractHandlerMethodMapping(CtClass, ClassPool)}
     */
    public static String[] filterDeleteBeanName(String[] original) {
        return Arrays.stream(original).filter(beanName -> !deleteBeanNameSet.contains(beanName)).toArray(String[]::new);
    }

    /**
     * {@link ClassPathBeanRefreshCommand}执行时会调用这里刷新class文件
     *
     * @param basePackage     base package on witch the transformer was registered, used to obtain associated scanner.
     * @param classDefinition new class definition
     * @param path            class path
     * @throws IOException error working with classDefinition
     */
    public static void refreshClass(String basePackage, byte[] classDefinition, String path) throws IOException, ClassNotFoundException {
        ResetSpringStaticCaches.reset();
        List<ClassPathBeanDefinitionScannerAgent> scannerAgents = getInstances(basePackage);
        if (scannerAgents.isEmpty()) {
            logger.error("basePackage '{}' not associated with any scannerAgent", basePackage);
            return;
        }
        for (ClassPathBeanDefinitionScannerAgent scannerAgent : scannerAgents) {
            BeanDefinition beanDefinition = scannerAgent.resolveBeanDefinition(classDefinition);
            if (null == beanDefinition) {
                continue;
            }
            String beanName = scannerAgent.getBeanName(beanDefinition);
            Class<?> beanClass = ((AbstractBeanDefinition) beanDefinition).resolveBeanClass(ClassPathBeanDefinitionScannerAgent.class.getClassLoader());
            assemblingSpringBean(beanName, beanClass);
            scannerAgent.defineBean(beanDefinition, path);
            break;
        }
        reloadFlag = false;
    }

    private static void assemblingSpringBean(String beanName, Class<?> beanClass) {
        ReflectionUtils.doWithFields(beanClass,
                field -> {
                    Type genericType = field.getGenericType();
                    if (genericType instanceof ParameterizedType) {
                        ParameterizedType parameterizedType = (ParameterizedType) genericType;
                        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                        if (actualTypeArguments.length > 0) {
                            Class<?> clazz;
                            Type lastType = actualTypeArguments[actualTypeArguments.length - 1];
                            if (lastType instanceof Class) {
                                clazz = (Class<?>) lastType;
                            } else if (lastType instanceof ParameterizedType) {
                                clazz = ((Class<?>) ((ParameterizedType) lastType).getRawType());
                            } else {
                                return;
                            }
                            autoWiredDependencyMap.computeIfAbsent(clazz.getName(), k -> new ConcurrentHashMap<>()).put(beanName, beanClass.getName());
                        }
                    }
                },
                field -> {
                    Autowired autowired = AnnotationUtils.getAnnotation(field, Autowired.class);
                    if (autowired == null) {
                        return false;
                    }
                    return List.class.isAssignableFrom(field.getType()) || Map.class.isAssignableFrom(field.getType());
                }
        );
    }

    public String getBeanName(BeanDefinition candidate) {
        return this.beanNameGenerator.generateBeanName(candidate, this.registry);
    }

    /**
     * 验证BeanDefinition后，处理SpringBean的Scope生成最终要注册到SpringBean中的BeanDefinitionHolder。
     * <p>
     * 根据{@link ClassPathBeanDefinitionScanner}的doScan方法}
     *
     * @param candidate 要重载的BeanDefinition
     */
    public void defineBean(BeanDefinition candidate, String path) {
        synchronized (ClassPathBeanDefinitionScannerAgent.class) {
            ScopeMetadata scopeMetadata = this.scopeMetadataResolver.resolveScopeMetadata(candidate);
            candidate.setScope(scopeMetadata.getScopeName());
            String beanName = getBeanName(candidate);
            if (candidate instanceof AbstractBeanDefinition) {
                postProcessBeanDefinition((AbstractBeanDefinition) candidate, beanName);
            }
            if (candidate instanceof AnnotatedBeanDefinition) {
                processCommonDefinitionAnnotations((AnnotatedBeanDefinition) candidate);
            }
            removeIfExists(beanName);
            if (checkCandidate(beanName, candidate)) {
                if (path != null) {
                    resolvePath(path, beanName);
                }
                removeDeleteBeanNameSet(beanName);
                BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(candidate, beanName);
                definitionHolder = applyScopedProxyMode(scopeMetadata, definitionHolder, registry);
                registerBeanDefinition(definitionHolder, registry);
                DefaultListableBeanFactory bf = maybeRegistryToBeanFactory();
                if (bf != null) {
                    ResetRequestMappingCaches.reset(bf);
                }
                ProxyReplacer.clearAllProxies();
                freezeConfiguration();
                logger.reload("Registered Spring bean '{}'", beanName);
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
            logger.debug("Removing bean definition '{}'", beanName);
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
                logger.debug("Identified candidate component class '{}'", metadataReader.getClassMetadata().getClassName());
                return sbd;
            } else {
                logger.debug("Ignored because not a concrete top-level class '{}'", metadataReader.getClassMetadata().getClassName());
                return null;
            }
        } else {
            logger.trace("Ignored because not matching any filter '{}' ", metadataReader.getClassMetadata().getClassName());
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
                logger.trace("Cache cleared: CachingMetadataReaderFactory.clearCache()");
            } else {
                logger.warning("Cache NOT cleared: neither CachingMetadataReaderFactory.metadataReaderCache nor clearCache does not exist.");
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
     * 注册Bean到容器
     */
    private void registerBeanDefinition(BeanDefinitionHolder definitionHolder, BeanDefinitionRegistry registry) {
        ReflectionHelper.invoke(scanner, ClassPathBeanDefinitionScanner.class, "registerBeanDefinition", new Class[]{BeanDefinitionHolder.class, BeanDefinitionRegistry.class}, definitionHolder, registry);
    }

    /**
     * 对BeanName和BeanDefinition进行检查
     *
     * @return 返回true表示可以注册
     */
    private boolean checkCandidate(String beanName, BeanDefinition candidate) {
        return (Boolean) ReflectionHelper.invoke(scanner, ClassPathBeanDefinitionScanner.class, "checkCandidate", new Class[]{String.class, BeanDefinition.class}, beanName, candidate);
    }

    /**
     * 解析公共的注解信息(@Lazy、@DependsOn、@Role、@Description)并将其应用到 BeanDefinition 上。
     */
    private void processCommonDefinitionAnnotations(AnnotatedBeanDefinition candidate) {
        ReflectionHelper.invoke(null, AnnotationConfigUtils.class, "processCommonDefinitionAnnotations", new Class[]{AnnotatedBeanDefinition.class}, candidate);
    }

    /**
     * 设置BeanDefinition的属性
     */
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