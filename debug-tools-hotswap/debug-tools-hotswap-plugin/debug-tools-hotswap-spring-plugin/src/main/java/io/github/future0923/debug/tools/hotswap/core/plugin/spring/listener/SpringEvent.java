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
package io.github.future0923.debug.tools.hotswap.core.plugin.spring.listener;

import lombok.Getter;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import java.util.EventObject;

/**
 * Spring相关的事件。用于通知插件，bean definition发生了变化。Spring boot 和 Spring 之间需要互相发送一些事件，事件和监听器就是用来满足这个需求的。
 *
 * @param <T> 事件类型
 */
@Getter
public abstract class SpringEvent<T> extends EventObject {

    private final ConfigurableListableBeanFactory beanFactory;

    /**
     * 创建事件
     *
     * @param source      事件对象
     * @param beanFactory ioc容器
     * @throws IllegalArgumentException source为null
     */
    public SpringEvent(T source, ConfigurableListableBeanFactory beanFactory) {
        super(source);
        this.beanFactory = beanFactory;
    }

    @SuppressWarnings("unchecked")
    public T getSource() {
        return (T) super.getSource();
    }
}
