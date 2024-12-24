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
package io.github.future0923.debug.tools.hotswap.core.plugin.spring.boot.listener;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.javassist.ClassPool;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.boot.SpringBootPlugin;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.files.PropertiesChangeEvent;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.listener.SpringEvent;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.listener.SpringEventSource;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.listener.SpringListener;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.reload.BeanChangeEvent;
import io.github.future0923.debug.tools.hotswap.core.util.AnnotationHelper;
import io.github.future0923.debug.tools.hotswap.core.util.spring.util.ClassUtils;
import io.github.future0923.debug.tools.hotswap.core.util.spring.util.ObjectUtils;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Objects;

/**
 * 当property source改变时刷新配置。
 * 不会直接刷新，通过发送{@link BeanChangeEvent}事件来刷新
 */
public class PropertySourceChangeListener implements SpringListener<SpringEvent<?>> {

    private final static Logger LOGGER = Logger.getLogger(PropertySourceChangeListener.class);

    private final DefaultListableBeanFactory beanFactory;

    /**
     * {@link SpringBootPlugin#register(ClassLoader, CtClass, ClassPool)}注册这个listener
     */
    public static void register(ConfigurableApplicationContext context) {
        ConfigurableListableBeanFactory configurableListableBeanFactory = context.getBeanFactory();
        if (!(configurableListableBeanFactory instanceof DefaultListableBeanFactory)) {
            LOGGER.debug("beanFactory is not DefaultListableBeanFactory, skip register PropertySourceChangeBootListener, {}", ObjectUtils.identityToString(configurableListableBeanFactory));
            return;
        }
        LOGGER.debug("register PropertySourceChangeBootListener, {}", ObjectUtils.identityToString(configurableListableBeanFactory));
        PropertySourceChangeListener propertySourceChangeListener = new PropertySourceChangeListener((DefaultListableBeanFactory)configurableListableBeanFactory);
        // 将实例添加到映射和实例中
        SpringEventSource.INSTANCE.addListener(propertySourceChangeListener);
    }

    public PropertySourceChangeListener(DefaultListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public DefaultListableBeanFactory beanFactory() {
        return beanFactory;
    }

    @Override
    public void onEvent(SpringEvent<?> event) {
        if (event instanceof PropertiesChangeEvent) {
            refreshConfigurationProperties(event.getBeanFactory());
        }
    }

    private void refreshConfigurationProperties(ConfigurableListableBeanFactory eventBeanFactory) {
        for (String singleton : beanFactory.getSingletonNames()) {
            Object bean = beanFactory.getSingleton(singleton);
            Class<?> beanClass = ClassUtils.getUserClass(bean.getClass());

            if (AnnotationHelper.hasAnnotation(beanClass, ConfigurationProperties.class.getName())) {
                LOGGER.debug("refresh configuration properties: {}", beanClass);
                String[] beanNames = beanFactory.getBeanNamesForType(beanClass);
                if (beanNames.length > 0) {
                    SpringEventSource.INSTANCE.fireEvent(new BeanChangeEvent(beanNames, eventBeanFactory));
                }
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (!(o instanceof PropertySourceChangeListener)) {return false;}
        PropertySourceChangeListener that = (PropertySourceChangeListener)o;
        return Objects.equals(beanFactory, that.beanFactory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(beanFactory);
    }
}
