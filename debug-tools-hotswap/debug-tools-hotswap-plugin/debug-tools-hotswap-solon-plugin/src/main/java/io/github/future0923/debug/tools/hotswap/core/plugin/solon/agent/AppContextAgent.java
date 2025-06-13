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
package io.github.future0923.debug.tools.hotswap.core.plugin.solon.agent;

import io.github.future0923.debug.tools.base.hutool.core.collection.CollUtil;
import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.plugin.solon.SolonPlugin;
import io.github.future0923.debug.tools.hotswap.core.plugin.solon.holder.InjectMappingHolder;
import io.github.future0923.debug.tools.hotswap.core.plugin.solon.holder.RoutingTableDefaultHolder;
import io.github.future0923.debug.tools.hotswap.core.util.PluginManagerInvoker;
import io.github.future0923.debug.tools.hotswap.core.util.ReflectionHelper;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.wrap.ClassWrap;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * Solon IOC 处理类
 *
 * @author future0923
 */
@SuppressWarnings({"unchecked"})
public class AppContextAgent {

    private static final Logger logger = Logger.getLogger(AppContextAgent.class);

    /**
     * Solon IOC
     */
    private final AppContext appContext;

    /**
     * 单例
     */
    private static AppContextAgent instance;

    /**
     * 构造函数
     */
    private AppContextAgent(AppContext appContext) {
        this.appContext = appContext;
    }

    /**
     * 获取单例
     *
     * @param appContext Solon IOC
     * @return 单例
     */
    public static AppContextAgent getInstance(AppContext appContext) {
        if (instance == null) {
            instance = new AppContextAgent(appContext);
        }
        return instance;
    }

    /**
     * 注册扫描包
     *
     * @param basePackage 扫描包
     */
    public void registerBasePackage(String basePackage) {
        PluginManagerInvoker.callPluginMethod(SolonPlugin.class, getClass().getClassLoader(),
                "registerComponentScanBasePackage", new Class[]{String.class}, new Object[]{basePackage});
    }

    /**
     * 刷新类
     *
     * @param basePackage  扫描包
     * @param refreshClass 刷新类
     * @param path         刷新类路径
     * @throws IOException            异常
     * @throws ClassNotFoundException 异常
     */
    public static void refreshClass(String basePackage, Class<?> refreshClass, String path) throws IOException, ClassNotFoundException {
        AppContextAgent appContextAgent = instance;
        if (appContextAgent == null) {
            logger.error("basePackage '{}' not associated with any scannerAgent", basePackage);
            return;
        }
        appContextAgent.resetCache(refreshClass);
        appContextAgent.tryBuildBeanOfClass(refreshClass);
        appContextAgent.reInject(refreshClass);
        logger.reload("Registered Solon bean '{}'", refreshClass.getName());
    }

    /**
     * 获取注入refreshClass类的所有实例对象并重新注入
     */
    private void reInject(Class<?> refreshClass) {
        Set<Object> objects = InjectMappingHolder.get(refreshClass);
        if (CollUtil.isNotEmpty(objects)) {
            objects.iterator().forEachRemaining(appContext::beanInject);
        }
    }

    /**
     * 尝试构建refreshClass类并放入IOC
     */
    private void tryBuildBeanOfClass(Class<?> refreshClass) {
        ReflectionHelper.invoke(appContext, AppContext.class, "tryBuildBeanOfClass", new Class[]{Class.class}, refreshClass);
    }

    /**
     * 重置缓存
     */
    private void resetCache(Class<?> refreshClass) {
        // 清除路由表
        RoutingTableDefaultHolder.getRoutingInstance().forEach(routing -> routing.remove(refreshClass));
        // 清除Bean构建缓存，如有不会重新构建Bean
        Set<Class<?>> beanBuildedCached = (Set<Class<?>>) ReflectionHelper.getNoException(appContext, AppContext.class, "beanBuildedCached");
        if (beanBuildedCached != null) {
            beanBuildedCached.remove(refreshClass);
        }
        // 清除IOC之前存在的BeanWrap
        ReflectionHelper.invoke(appContext, AppContext.class, "removeWrap", new Class[]{Class.class}, refreshClass);
        // 清除掉ClassWrap中的类信息缓存
        Map<Class<?>, ClassWrap> cached = (Map<Class<?>, ClassWrap>) ReflectionHelper.getNoException(null, ClassWrap.class, "cached");
        if (cached != null) {
            cached.remove(refreshClass);
        }
    }
}
