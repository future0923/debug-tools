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
package io.github.future0923.debug.tools.hotswap.core.plugin.spring.getbean;

/**
 * Spring热重载代理类，标记作用类似于Spring的{@code InfrastructureProxy}，在{@link EnhancerProxyCreater#buildProxyCreaterClass}中使用
 * <p>
 * 在{@link EnhancerProxyCreater#buildProxyCallbackClass}中代理了下面两个方法
 */
public interface SpringHotswapAgentProxy {
    /**
     * 实际代理实现为{@link DetachableBeanHolder#getTarget()}
     */
    Object $$ha$getTarget();

    /**
     * 实际代理实现为{@link DetachableBeanHolder#setTarget(Object)}
     */
    void $$ha$setTarget(Object object);
}
