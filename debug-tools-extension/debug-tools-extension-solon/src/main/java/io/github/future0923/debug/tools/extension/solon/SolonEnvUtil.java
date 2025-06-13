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
package io.github.future0923.debug.tools.extension.solon;

import io.github.future0923.debug.tools.base.hutool.core.collection.CollUtil;
import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.vm.JvmToolsUtils;
import org.noear.solon.core.AppContext;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author future0923
 */
public class SolonEnvUtil {

    private static final Logger logger = Logger.getLogger(SolonEnvUtil.class);

    private static volatile boolean init = false;

    private static List<AppContext> appContexts;

    private static void initSolonContext() {
        if (!init) {
            synchronized (SolonEnvUtil.class) {
                if (!init) {
                    appContexts = Arrays.asList(JvmToolsUtils.getInstances(AppContext.class));
                    init = true;
                }
            }
        }
    }

    public static <T> T getLastBean(String beanName) {
        return CollUtil.getLast(getBeans(beanName));
    }

    public static <T> List<T> getBeans(String beanName) {
        initSolonContext();
        return getBeansByAppContext(beanName);
    }

    private static <T> List<T> getBeansByAppContext(String beanName) {
        List<T> beanList = new LinkedList<>();
        for (AppContext appContext : appContexts) {
            beanList.addAll(appContext.getBean(beanName));
        }
        return beanList;
    }

    public static <T> T getLastBean(Class<T> requiredType) {
        return CollUtil.getLast(getBeans(requiredType));
    }

    public static <T> List<T> getBeans(Class<T> requiredType) {
        initSolonContext();
        return getBeansByAppContext(requiredType);
    }

    private static <T> List<T> getBeansByAppContext(Class<T> requiredType) {
        List<T> beanList = new LinkedList<>();
        for (AppContext appContext : appContexts) {
            beanList.addAll(appContext.getBeansOfType(requiredType));
        }
        return beanList;
    }
}
