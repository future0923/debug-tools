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
