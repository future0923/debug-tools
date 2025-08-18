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
package io.github.future0923.debug.tools.base.around;

import java.util.List;
import java.util.Map;

/**
 * @author future0923
 */
public class RunMethodAround {

    /**
     * Execute before a method call
     *
     * @param headers              headers info
     * @param xxlJobParam          xxl job param
     * @param className            call class name
     * @param methodName           call method name
     * @param methodParameterTypes call parameter types
     * @param methodParameters     call parameters
     */
    public void onBefore(Map<String, String> headers, String xxlJobParam, String className, String methodName, List<String> methodParameterTypes, Object[] methodParameters) {

    }

    /**
     * Execute after a method call
     *
     * @param headers              headers info
     * @param xxlJobParam          xxl job param
     * @param className            call class name
     * @param methodName           call method name
     * @param methodParameterTypes call parameter types
     * @param methodParameters     call parameters
     * @param result               call method return value
     */
    public void onAfter(Map<String, String> headers, String xxlJobParam, String className, String methodName, List<String> methodParameterTypes, Object[] methodParameters, Object result) {

    }

    /**
     * Execute exception a method call
     *
     * @param headers              headers info
     * @param xxlJobParam          xxl job param
     * @param className            call class name
     * @param methodName           call method name
     * @param methodParameterTypes call parameter types
     * @param methodParameters     call parameters
     * @param throwable            call method exception info
     */
    public void onException(Map<String, String> headers, String xxlJobParam, String className, String methodName, List<String> methodParameterTypes, Object[] methodParameters, Throwable throwable) {

    }

    /**
     * Execute finally a method call
     *
     * @param headers              headers info
     * @param xxlJobParam          xxl job param
     * @param className            call class name
     * @param methodName           call method name
     * @param methodParameterTypes call parameter types
     * @param methodParameters     call parameters
     * @param result               call method return value
     * @param throwable            call method exception info
     */
    public void onFinally(Map<String, String> headers, String xxlJobParam, String className, String methodName, List<String> methodParameterTypes, Object[] methodParameters, Object result, Throwable throwable) {

    }
}
