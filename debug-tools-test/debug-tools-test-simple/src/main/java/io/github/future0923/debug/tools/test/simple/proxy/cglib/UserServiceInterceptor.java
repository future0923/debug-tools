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
package io.github.future0923.debug.tools.test.simple.proxy.cglib;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @author future0923
 */
public class UserServiceInterceptor implements MethodInterceptor {

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        if (method.getName().equals("addUser")) {
            //System.out.println("Before method: " + method.getName());
            printName("Before", method.getName());
            Object result = proxy.invokeSuper(obj, args);
            //System.out.println("After method: " + method.getName());
            printName("After", method.getName());
            return result;
        } else {
            return proxy.invokeSuper(obj, args);
        }
    }

    private void printName(String hook, String name) {
        System.out.println(hook + " printName :" + name);
    }
}
