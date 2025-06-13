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
package io.github.future0923.debug.tools.hotswap.core.plugin.solon.holder;

import io.github.future0923.debug.tools.base.hutool.core.collection.CollUtil;
import io.github.future0923.debug.tools.base.hutool.core.util.StrUtil;
import org.noear.solon.Solon;
import org.noear.solon.proxy.asm.AsmProxy;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 注入关系映射
 *
 * @author future0923
 */
public class InjectMappingHolder {

    /**
     * 对象被注入的映射关系
     * key: 被注入的类名
     * value: 注入该类的所有实例集合
     */
    private static final Map<String, Set<Object>> INJECT_INSTANCE_MAPPING = new ConcurrentHashMap<>();

    /**
     * 对象都注入的哪些类的映射关系
     * key: 类
     * value: 注入的类集合
     */
    private static final Map<String, Set<String>> INSTANCE_INJECT_MAPPING = new ConcurrentHashMap<>();

    /**
     * 添加普通对象
     *
     * @param target       实例对象
     * @param injectObject 被注入的对象
     */
    public static void put(Object target, Object injectObject) {
        if (injectObject == null || target == null) {
            return;
        }
        String key = injectObject.getClass().getName();
        INJECT_INSTANCE_MAPPING.computeIfAbsent(key, k -> new HashSet<>()).add(target);
        INSTANCE_INJECT_MAPPING.computeIfAbsent(target.getClass().getName(), k -> new HashSet<>()).add(key);
    }

    /**
     * 添加代理类对象
     */
    public static void putProxy(Object raw) {
        String proxyClassName = raw.getClass().getName();
        String originalClassName = StrUtil.removeSuffix(proxyClassName, AsmProxy.PROXY_CLASSNAME_SUFFIX);
        Set<String> injectClassNameSet = INSTANCE_INJECT_MAPPING.get(originalClassName);
        if (CollUtil.isNotEmpty(injectClassNameSet)) {
            for (String injectClassName : injectClassNameSet) {
                Set<Object> objects = INJECT_INSTANCE_MAPPING.get(injectClassName);
                if (CollUtil.isNotEmpty(objects)) {
                    objects.add(raw);
                }
            }
        }
    }

    /**
     * 获取注入该类的所有实例对象
     */
    public static Set<Object> get(Class<?> key) {
        if (key == null) {
            return null;
        }
        clearBeanInstance(key);
        Set<Object> result = new HashSet<>();
        String className = key.getName();
        Optional.ofNullable(INJECT_INSTANCE_MAPPING.get(className)).ifPresent(result::addAll);
        Optional.ofNullable(INJECT_INSTANCE_MAPPING.get(className + AsmProxy.PROXY_CLASSNAME_SUFFIX)).ifPresent(result::addAll);
        return result;
    }

    /**
     * 清除掉重载之后多余的BeanInstance
     */
    private static void clearBeanInstance(Class<?> key) {
        Set<String> injectClassNameSet = INSTANCE_INJECT_MAPPING.get(key.getName());
        if (CollUtil.isNotEmpty(injectClassNameSet)) {
            for (String injectClassName : injectClassNameSet) {
                Set<Object> objects = INJECT_INSTANCE_MAPPING.get(injectClassName);
                if (CollUtil.isNotEmpty(objects)) {
                    objects.removeIf(object -> {
                        Class<?> beanClass = object.getClass();
                        if (StrUtil.endWith(object.getClass().getName(), AsmProxy.PROXY_CLASSNAME_SUFFIX)) {
                            try {
                                beanClass = Class.forName(StrUtil.removeSuffix(object.getClass().getName(), AsmProxy.PROXY_CLASSNAME_SUFFIX));
                            } catch (ClassNotFoundException ignored) {

                            }
                        }
                        List<?> beansOfType = Solon.context().getBeansOfType(beanClass);
                        return !beansOfType.contains(object);
                    });
                }
            }
        }
    }
}
