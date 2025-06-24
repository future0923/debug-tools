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
