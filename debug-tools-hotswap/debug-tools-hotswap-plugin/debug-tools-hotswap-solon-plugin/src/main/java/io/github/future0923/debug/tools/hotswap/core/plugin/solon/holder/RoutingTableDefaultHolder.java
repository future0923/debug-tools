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

import org.noear.solon.core.route.RoutingTableDefault;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 所有的路由表实例
 *
 * @author future0923
 */
public class RoutingTableDefaultHolder {

    /**
     * Solon路由表RoutingTableDefault实例集合
     */
    private static final Set<RoutingTableDefault<?>> ROUTING_INSTANCE = ConcurrentHashMap.newKeySet();

    /**
     * 添加路由表实例
     *
     * @param routing 路由表实例
     */
    public static void add(RoutingTableDefault<?> routing) {
        ROUTING_INSTANCE.add(routing);
    }

    /**
     * 获取所有路由表实例
     *
     * @return 路由表实例集合
     */
    public static Set<RoutingTableDefault<?>> getRoutingInstance() {
        return ROUTING_INSTANCE;
    }
}
