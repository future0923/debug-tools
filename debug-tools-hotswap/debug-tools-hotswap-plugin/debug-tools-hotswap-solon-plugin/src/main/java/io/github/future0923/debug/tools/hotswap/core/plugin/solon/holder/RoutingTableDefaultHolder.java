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
