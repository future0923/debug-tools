package io.github.future0923.debug.tools.test.simple.proxy.cglib;

import net.sf.cglib.proxy.Enhancer;

/**
 * @author future0923
 */
public class ProxyTest {

    public static void main(String[] args) throws InterruptedException {
        // 创建 Enhancer 对象
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(UserService.class);
        enhancer.setCallback(new UserServiceInterceptor());
        UserService proxy = (UserService) enhancer.create();
        while (true) {
            Thread.sleep(1000);
            // 调用代理对象的方法
            proxy.addUser("DebugTools");
        }
    }
}
