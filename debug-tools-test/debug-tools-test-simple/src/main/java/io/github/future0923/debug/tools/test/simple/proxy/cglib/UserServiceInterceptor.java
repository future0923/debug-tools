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
