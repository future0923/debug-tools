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
