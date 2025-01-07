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
package io.github.future0923.debug.tools.hotswap.core.util;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.annotation.LoadEvent;
import io.github.future0923.debug.tools.hotswap.core.annotation.OnClassLoadEvent;
import io.github.future0923.debug.tools.hotswap.core.annotation.Plugin;
import io.github.future0923.debug.tools.hotswap.core.annotation.handler.PluginClassFileTransformer;
import io.github.future0923.debug.tools.hotswap.core.config.PluginManager;
import lombok.Getter;
import lombok.Setter;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.regex.Pattern;

/**
 * 热重载转换器，
 * <p>
 * 热重载只有这一个转换器，它会委托其他插件转换器进行转换
 */
public class HotswapTransformer implements ClassFileTransformer {

    private static final Logger LOGGER = Logger.getLogger(HotswapTransformer.class);

    /**
     * 跳过转换的classloader
     */
    private static final Set<String> skippedClassLoaders = new HashSet<>(Arrays.asList(
            "jdk.internal.reflect.DelegatingClassLoader",
            "sun.reflect.DelegatingClassLoader"
    ));

    /**
     * 应该被跳过初始化的类加载器
     */
    private static final Set<String> excludedClassLoaders = new HashSet<>(Arrays.asList(
            "org.apache.felix.framework.BundleWiringImpl$BundleClassLoader", // delegating ClassLoader in GlassFish
            "org.apache.felix.framework.BundleWiringImpl$BundleClassLoaderJava5" // delegating ClassLoader in_GlassFish
    ));

    /**
     * 注册Transformer记录，可以使用{@link OnClassLoadEvent}注入，也可以调用{@link #registerTransformer}注入
     */
    public static class RegisteredTransformersRecord {

        /**
         * 匹配Class的正则表达式
         */
        Pattern pattern;

        /**
         * 所有关注这个正则的transformer集合
         */
        List<HaClassFileTransformer> transformerList = new LinkedList<>();
    }

    /**
     * 只关注{@link LoadEvent#REDEFINE}事件的transformer信息
     */
    protected Map<String, RegisteredTransformersRecord> redefinitionTransformers = new LinkedHashMap<>();

    /**
     * 关注非{@link LoadEvent#REDEFINE}事件的transformer信息
     */
    protected Map<String, RegisteredTransformersRecord> otherTransformers = new LinkedHashMap<>();

    /**
     * Transformer与ClassLoader之间的映射
     */
    protected Map<ClassFileTransformer, ClassLoader> classLoaderTransformers = new LinkedHashMap<>();

    /**
     * 类加载是否已经初始化完成
     */
    protected Map<ClassLoader, Boolean> seenClassLoaders = new WeakHashMap<>();

    /**
     * 应该初始化的类加载器正则集合
     */
    @Getter
    @Setter
    private List<Pattern> includedClassLoaderPatterns;

    /**
     * 排除初始化的类加载器正则集合
     */
    @Getter
    @Setter
    private List<Pattern> excludedClassLoaderPatterns;

    /**
     * 转换时本身不检测，调用注册的插件，如果匹配，则调用插件的transform方法（无顺序）
     */
    @Override
    public byte[] transform(final ClassLoader classLoader, String className, Class<?> redefiningClass,
                            final ProtectionDomain protectionDomain, byte[] bytes) throws IllegalClassFormatException {
        String classLoaderClassName = classLoader != null ? classLoader.getClass().getName() : null;
        if (skippedClassLoaders.contains(classLoaderClassName)) {
            return bytes;
        }
        if (className != null) {
            LOGGER.debug("className, {}", className);
        }
        // 非插件类文件Transformer的集合
        List<ClassFileTransformer> toApply = new ArrayList<>();
        // 插件类文件Transformer的集合
        List<PluginClassFileTransformer> pluginTransformers = new ArrayList<>();
        try {
            // 调用关注非define类型的transformer
            for (RegisteredTransformersRecord transformerRecord : new ArrayList<>(otherTransformers.values())) {
                if ((className != null && transformerRecord.pattern.matcher(className).matches()) ||
                        (redefiningClass != null && transformerRecord.pattern.matcher(redefiningClass.getName()).matches())) {
                    for (ClassFileTransformer transformer : new ArrayList<ClassFileTransformer>(transformerRecord.transformerList)) {
                        if (transformer instanceof PluginClassFileTransformer) {
                            PluginClassFileTransformer pluginClassFileTransformer = (PluginClassFileTransformer) transformer;
                            if (!pluginClassFileTransformer.isPluginDisabled(classLoader)) {
                                pluginTransformers.add(pluginClassFileTransformer);
                            }
                        } else {
                            toApply.add(transformer);
                        }
                    }
                }
            }
            // 调用关注redefine类型的transformer
            if (redefiningClass != null && className != null) {
                for (RegisteredTransformersRecord transformerRecord : new ArrayList<RegisteredTransformersRecord>(redefinitionTransformers.values())) {
                    if (transformerRecord.pattern.matcher(className).matches()) {
                        for (ClassFileTransformer transformer : new ArrayList<ClassFileTransformer>(transformerRecord.transformerList)) {
                            if (transformer instanceof PluginClassFileTransformer) {
                                PluginClassFileTransformer pluginClassFileTransformer = (PluginClassFileTransformer) transformer;
                                if (!pluginClassFileTransformer.isPluginDisabled(classLoader)) {
                                    pluginTransformers.add(pluginClassFileTransformer);
                                }
                            } else {
                                toApply.add(transformer);
                            }
                        }
                    }
                }
            }
        } catch (Throwable t) {
            LOGGER.error("Error transforming class '" + className + "'.", t);
        }

        if (!pluginTransformers.isEmpty()) {
            pluginTransformers = reduce(classLoader, pluginTransformers, className);
        }

        // 确保classLoader已经初始化
        if (!ensureClassLoaderInitialized(classLoader, protectionDomain)) {
            LOGGER.trace("Skipping className '{}' classloader '{}' transform", className, classLoader);
            return bytes;
        }

        if (toApply.isEmpty() && pluginTransformers.isEmpty()) {
            return bytes;
        }

        try {
            byte[] result = bytes;

            // 调用插件的Transformer
            for (ClassFileTransformer transformer : pluginTransformers) {
                LOGGER.trace("Transforming class '" + className + "' with transformer '" + transformer + "' " + "@ClassLoader" + classLoader + ".");
                result = transformer.transform(classLoader, className, redefiningClass, protectionDomain, result);
            }

            // 调用非插件的Transformer
            for (ClassFileTransformer transformer : toApply) {
                LOGGER.trace("Transforming class '" + className + "' with transformer '" + transformer + "' " + "@ClassLoader" + classLoader + ".");
                result = transformer.transform(classLoader, className, redefiningClass, protectionDomain, result);
            }
            return result;
        } catch (Throwable t) {
            LOGGER.error("Error transforming class '" + className + "'.", t);
        }
        return bytes;
    }

    /**
     * 注册 transformer
     */
    public void registerTransformer(ClassLoader classLoader, String classNameRegexp, HaClassFileTransformer transformer) {
        LOGGER.debug("Registering transformer for class regexp '{}'.", classNameRegexp);

        String normalizeRegexp = normalizeTypeRegexp(classNameRegexp);

        Map<String, RegisteredTransformersRecord> transformersMap = getTransformerMap(transformer);

        RegisteredTransformersRecord transformerRecord = transformersMap.get(normalizeRegexp);
        if (transformerRecord == null) {
            transformerRecord = new RegisteredTransformersRecord();
            transformerRecord.pattern = Pattern.compile(normalizeRegexp);
            transformersMap.put(normalizeRegexp, transformerRecord);
        }

        if (!transformerRecord.transformerList.contains(transformer)) {
            transformerRecord.transformerList.add(transformer);
        }

        if (classLoader != null) {
            classLoaderTransformers.put(transformer, classLoader);
        }
    }

    private Map<String, RegisteredTransformersRecord> getTransformerMap(HaClassFileTransformer transformer) {
        if (transformer.isForRedefinitionOnly()) {
            return redefinitionTransformers;
        }
        return otherTransformers;
    }

    /**
     * Remove registered transformer.
     *
     * @param classNameRegexp regexp to match fully qualified class name.
     * @param transformer     currently registered transformer
     */
    public void removeTransformer(String classNameRegexp, HaClassFileTransformer transformer) {
        String normalizeRegexp = normalizeTypeRegexp(classNameRegexp);
        Map<String, RegisteredTransformersRecord> transformersMap = getTransformerMap(transformer);
        RegisteredTransformersRecord transformerRecord = transformersMap.get(normalizeRegexp);
        if (transformerRecord != null) {
            transformerRecord.transformerList.remove(transformer);
        }
    }

    /**
     * 移除类加载器中所有的transformer
     */
    public void closeClassLoader(ClassLoader classLoader) {
        for (Iterator<Map.Entry<ClassFileTransformer, ClassLoader>> entryIterator = classLoaderTransformers.entrySet().iterator();
             entryIterator.hasNext(); ) {
            Map.Entry<ClassFileTransformer, ClassLoader> entry = entryIterator.next();
            if (entry.getValue().equals(classLoader)) {
                entryIterator.remove();
                for (RegisteredTransformersRecord transformerRecord : redefinitionTransformers.values()) {
                    transformerRecord.transformerList.remove(entry.getKey());
                }
                for (RegisteredTransformersRecord transformerRecord : otherTransformers.values()) {
                    transformerRecord.transformerList.remove(entry.getKey());
                }
            }
        }

        LOGGER.debug("All transformers removed for classLoader {}", classLoader);
    }

    /**
     * 匹配插件如果有不满足的则过滤不满足的，并处理{@link Plugin#fallback()}
     */
    private LinkedList<PluginClassFileTransformer> reduce(final ClassLoader classLoader, List<PluginClassFileTransformer> pluginCalls, String className) {
        LinkedList<PluginClassFileTransformer> reduced = new LinkedList<>();

        Map<String, PluginClassFileTransformer> fallbackMap = new HashMap<>();

        for (PluginClassFileTransformer transformer : pluginCalls) {
            try {
                String pluginGroup = transformer.getPluginGroup();
                if (transformer.versionMatches(classLoader)) {
                    if (pluginGroup != null) {
                        fallbackMap.put(pluginGroup, null);
                    }
                    reduced.add(transformer);
                } else if (transformer.isFallbackPlugin()) {
                    if (pluginGroup != null && !fallbackMap.containsKey(pluginGroup)) {
                        fallbackMap.put(pluginGroup, transformer);
                    }
                }
            } catch (Exception e) {
                LOGGER.warning("Error evaluating aplicability of plugin", e);
            }
        }

        for (PluginClassFileTransformer transformer : fallbackMap.values()) {
            if (transformer != null) {
                reduced.add(transformer);
            }
        }

        return reduced;
    }

    /**
     * 每个类加载器都应该确定被初始化，热重载需要将插件加载到每一个类加载器中才能重载。
     */
    protected boolean ensureClassLoaderInitialized(final ClassLoader classLoader, final ProtectionDomain protectionDomain) {
        if (!seenClassLoaders.containsKey(classLoader)) {

            if (classLoader == null) {
                PluginManager.getInstance().initClassLoader(null, protectionDomain);
            } else {
                if (shouldScheduleClassLoader(classLoader)) {
                    PluginManager.getInstance().initClassLoader(classLoader, protectionDomain);
                } else {
                    seenClassLoaders.put(classLoader, false);
                    return false;
                }
            }
            seenClassLoaders.put(classLoader, true);
        }
        return seenClassLoaders.get(classLoader) != null && seenClassLoaders.get(classLoader);
    }

    /**
     * 返回类加载器是否应该被初始化
     */
    private boolean shouldScheduleClassLoader(final ClassLoader classLoader) {
        String name = classLoader.getClass().getName();
        if (excludedClassLoaders.contains(name)) {
            return false;
        }

        if (includedClassLoaderPatterns != null) {
            for (Pattern pattern : includedClassLoaderPatterns) {
                if (pattern.matcher(name).matches()) {
                    return true;
                }
            }
            return false;
        }

        if (excludedClassLoaderPatterns != null) {
            for (Pattern pattern : excludedClassLoaderPatterns) {
                if (pattern.matcher(name).matches()) {
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * 转换为正规正则
     *
     * @return ^regexp$
     */
    protected String normalizeTypeRegexp(String registeredType) {
        String regexp = registeredType;
        if (!registeredType.startsWith("^")) {
            regexp = "^" + regexp;
        }
        if (!registeredType.endsWith("$")) {
            regexp = regexp + "$";
        }

        return regexp;
    }

}
