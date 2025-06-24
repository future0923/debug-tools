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
package io.github.future0923.debug.tools.test.solon.aop;

import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.aspect.Invocation;


/**
 * @author future0923
 */
public class TestAopInterceptor implements Interceptor {

    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        TestAop anno = inv.getMethodAnnotation(TestAop.class);
        if (anno == null) {
            anno = inv.getTargetAnnotation(TestAop.class);
        }
        if (anno == null) {
            return inv.invoke();
        }
        System.out.println("before");
        Object invoke = inv.invoke();
        System.out.println("after");
        return invoke;
    }
}
