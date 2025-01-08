package io.github.future0923.debug.tools.test.simple.proxy.jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author future0923
 */
public class UserServiceInvocationHandler implements InvocationHandler {

    private final Object target; // 被代理对象

    // 构造方法
    public UserServiceInvocationHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("addUser")) {
            System.out.println("Before method 11: " + method.getName());
            Object result = method.invoke(target, args);
            System.out.println("After method 22: " + method.getName());
            return result;
        } else {
            return method.invoke(target, args);
        }
    }
}
