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
package io.github.future0923.debug.tools.hotswap.core.plugin.spring.reload;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.SpringPlugin;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.listener.SpringEvent;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.listener.SpringEventSource;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.listener.SpringListener;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.scanner.BeanDefinitionChangeEvent;
import io.github.future0923.debug.tools.hotswap.core.util.spring.util.ObjectUtils;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 重新加载Spring环境
 */
public class SpringChangedAgent implements SpringListener<SpringEvent<?>>, Comparable<SpringChangedAgent> {
    private static final Logger LOGGER = Logger.getLogger(SpringChangedAgent.class);

    private static final AtomicInteger waitingReloadCount = new AtomicInteger(0);

    private final DefaultListableBeanFactory defaultListableBeanFactory;

    /**
     * 代理类的ClassLoader，{@link SpringPlugin#init}会设置
     */
    private static ClassLoader appClassLoader;
    private static final Map<DefaultListableBeanFactory, SpringChangedAgent> springChangeAgents = new ConcurrentHashMap<>(2);

    /**
     * 重新加载SpringBean环境
     */
    private final SpringBeanReload springReload;

    /**
     * 重载锁
     */
    private final ReentrantLock reloadLock = new ReentrantLock();

    public SpringChangedAgent(DefaultListableBeanFactory defaultListableBeanFactory) {
        springReload = new SpringBeanReload(defaultListableBeanFactory);
        this.defaultListableBeanFactory = defaultListableBeanFactory;
        SpringEventSource.INSTANCE.addListener(this);
    }

    public static SpringChangedAgent getInstance(DefaultListableBeanFactory beanFactory) {
        if (springChangeAgents.get(beanFactory) == null) {
            synchronized (SpringChangedAgent.class) {
                if (springChangeAgents.get(beanFactory) == null) {
                    SpringChangedAgent springChangedAgent = new SpringChangedAgent(beanFactory);
                    springChangeAgents.put(beanFactory, springChangedAgent);
                }
            }
        }
        return springChangeAgents.get(beanFactory);
    }

    public static void setClassLoader(ClassLoader classLoader) {
        SpringChangedAgent.appClassLoader = classLoader;
    }

    public static boolean addChangedClass(Class<?> clazz) {
        boolean result = false;
        for (SpringChangedAgent springChangedAgent : springChangeAgents.values()) {
            result |= springChangedAgent.addClass(clazz);
        }
        return result;
    }

    public static boolean addChangedClass(Class<?> clazz, DefaultListableBeanFactory beanFactory) {
        boolean result = false;
        for (SpringChangedAgent springChangedAgent : springChangeAgents.values()) {
            if (springChangedAgent.beanFactory().equals(beanFactory)) {
                result |= springChangedAgent.addClass(clazz);
            }
        }
        return result;
    }

    public static boolean addChangedXml(URL xmlUrl) {
        for (SpringChangedAgent springChangedAgent : springChangeAgents.values()) {
            springChangedAgent.addXml(xmlUrl);
        }
        return true;
    }

    public static boolean addChangedProperty(URL property) {
        for (SpringChangedAgent springChangedAgent : springChangeAgents.values()) {
            springChangedAgent.addProperty(property);
        }
        return true;
    }

    public static boolean addChangedYaml(URL yamlProperty) {
        for (SpringChangedAgent springChangedAgent : springChangeAgents.values()) {
            springChangedAgent.addYaml(yamlProperty);
        }
        return true;
    }

    public static boolean addNewBean(BeanDefinitionHolder beanDefinitionHolder,
        ConfigurableListableBeanFactory beanFactory) {
        for (SpringChangedAgent springChangedAgent : springChangeAgents.values()) {
            if (springChangedAgent.beanFactory() == beanFactory) {
                springChangedAgent.addNewBean(springChangedAgent.beanFactory(), beanDefinitionHolder);
            }
        }
        return true;
    }

    public static void reload(long changeTimeStamps) {
        int reloadCount = waitingReloadCount.incrementAndGet();
        // 避免多个重载同时生效
        if (reloadCount > 2) {
            LOGGER.trace("Spring reload is already scheduled, skip this time:{}", changeTimeStamps);
            waitingReloadCount.decrementAndGet();
            return;
        }
        try {
            // 排序保证父类的beanFactory先重新加载
            List<SpringChangedAgent> changedAgentList = new ArrayList<>(springChangeAgents.values());
            Collections.sort(changedAgentList);
            for (SpringChangedAgent springChangedAgent : changedAgentList) {
                springChangedAgent.reloadAll(changeTimeStamps);
            }
        } finally {
            waitingReloadCount.decrementAndGet();
        }
    }

    /**
     * unit test
     *
     * @param beanFactory
     */
    public static void destroyBeanFactory(AbstractAutowireCapableBeanFactory beanFactory) {
        if (!(beanFactory instanceof DefaultListableBeanFactory)) {
            return;
        }
        springChangeAgents.remove(beanFactory);
    }

    /**
     * 添加要reload的class
     */
    boolean addClass(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        springReload.addClass(clazz);
        return true;
    }

    void addProperty(URL property) {
        springReload.addProperty(property);
    }

    void addYaml(URL yaml) {
        springReload.addYaml(yaml);
    }

    void addXml(URL xml) {
        springReload.addXml(xml);
    }

    void addChangedBeanNames(String[] beanNames) {
        springReload.addChangedBeanNames(beanNames);
    }

    void addNewBean(BeanDefinitionRegistry registry, BeanDefinitionHolder beanDefinitionHolder) {
        springReload.addScanNewBean(registry, beanDefinitionHolder);
    }

    private void reloadAll(long changeTimeStamps) {
        try {
            doReload(changeTimeStamps);
        } catch (InterruptedException e) {
            LOGGER.warning("reload spring failed: {}", e, ObjectUtils.identityToString(defaultListableBeanFactory));
        }
    }

    private void doReload(long changeTimeStamps) throws InterruptedException {
        boolean isLockAcquired = reloadLock.tryLock(1, TimeUnit.SECONDS);
        if (isLockAcquired) {
            try {
                LOGGER.trace("Spring reload: {} at timestamps '{}'", ObjectUtils.identityToString(defaultListableBeanFactory), changeTimeStamps);
                springReload.reload(changeTimeStamps);
            } finally {
                reloadLock.unlock();
            }
        } else {
            Thread.sleep(100);
            doReload(changeTimeStamps);
        }
    }

    public static void collectPlaceholderProperties(ConfigurableListableBeanFactory configurableListableBeanFactory) {
        if (!(configurableListableBeanFactory instanceof DefaultListableBeanFactory)) {
            return;
        }
        getInstance(
            (DefaultListableBeanFactory)configurableListableBeanFactory).springReload.collectPlaceHolderProperties();
    }

    @Override
    public DefaultListableBeanFactory beanFactory() {
        return defaultListableBeanFactory;
    }

    @Override
    public void onEvent(SpringEvent<?> event) {
        if (event instanceof BeanDefinitionChangeEvent) {
            BeanDefinitionChangeEvent beanDefinitionChangeEvent = (BeanDefinitionChangeEvent)event;
            addNewBean(beanFactory(), beanDefinitionChangeEvent.getSource());
        } else if (event instanceof BeanChangeEvent) {
            BeanChangeEvent beanChangeEvent = (BeanChangeEvent)event;
            addChangedBeanNames(beanChangeEvent.getSource());
        }
    }

    /**
     * calculate the order by beanFactory.
     * If the beanFactory is root, return 1, else return 1 + parentBeanFactory's order
     *
     * @param beanFactory
     * @return
     */
    private int orderByParentBeanFactory(AbstractBeanFactory beanFactory) {
        if (beanFactory == null) {
            return 0;
        }
        if (beanFactory.getParentBeanFactory() == null) {
            return 1;
        }
        if (beanFactory.getParentBeanFactory() instanceof AbstractBeanFactory) {
            return 1 + orderByParentBeanFactory((AbstractBeanFactory)beanFactory.getParentBeanFactory());
        }
        return 1;
    }

    @Override
    public int compareTo(SpringChangedAgent o) {
        return Integer.compare(orderByParentBeanFactory(defaultListableBeanFactory),
            orderByParentBeanFactory(o.defaultListableBeanFactory));
    }
}
