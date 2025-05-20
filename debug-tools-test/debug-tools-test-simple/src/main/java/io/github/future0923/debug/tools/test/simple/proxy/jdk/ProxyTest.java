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
package io.github.future0923.debug.tools.test.simple.proxy.jdk;

import java.lang.reflect.Proxy;

/**
 * @author future0923
 */
public class ProxyTest {

    public static void main(String[] args) throws InterruptedException {
        // 创建目标对象
        UserService target = new UserServiceImpl();

        // 创建 InvocationHandler
        UserServiceInvocationHandler handler = new UserServiceInvocationHandler(target);

        // 创建代理对象
        UserService proxy = (UserService) Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                target.getClass().getInterfaces(),
                handler
        );

        while (true) {
            Thread.sleep(1000);
            // 调用代理对象的方法
            proxy.addUser("DebugTools");
        }
    }
}
