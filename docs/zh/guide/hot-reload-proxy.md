# 代理类热重载

代理类热重载非常重要，因为 [Spring](hot-reload-springboot.md) 和 [MyBatisPlus](hot-reload-mybatis-plus.md) 等都是通过代理类对代码进行增强。

## JDK

定义接口

```java
public interface UserService {

    void addUser(String name);

}
```

定义实现类

```java
public class UserServiceImpl implements UserService {

    @Override
    public void addUser(String name) {
        System.out.println("Add user: " + name); // [!code --]
        printName(name); // [!code ++]
    }
    
    public void printName(String name) { // [!code ++]
        System.out.println("printName: " + name); // [!code ++]
    } // [!code ++]
}
```

定义 InvocationHandler

```java
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class UserServiceInvocationHandler implements InvocationHandler {

    private final Object target;

    public UserServiceInvocationHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("addUser")) {
            System.out.println("Before method: " + method.getName()); // [!code --]
            printName("Before", method.getName()); // [!code ++]
            Object result = method.invoke(target, args);
            System.out.println("After method: " + method.getName()); // [!code --]
            printName("After", method.getName()); // [!code ++]
            return result;
        } else {
            return method.invoke(target, args);
        }
    }
    
    private void printName(String hook, String name) { // [!code ++]
        System.out.println(hook + " printName :" + name); // [!code ++]
    } // [!code ++]
}
```

使用 Proxy 创建代理对象

```java
import java.lang.reflect.Proxy;

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
            Thread.sleep(10000);
            // 调用代理对象的方法
            proxy.addUser("DebugTools");
        }
    }
}
```

打印执行

```text
# 热重载之前
Before method: addUser
Add user: DebugTools
After method: addUser
# 热重载之后
Before printName: addUser
printName: DebugTools
After printName: addUser
```

## Cglib

创建代理对象

```java
public class UserService {

    public void addUser(String name) {
        System.out.println("Add user: " + name); // [!code --]
        printName(name); // [!code ++]
    }

    public void printName(String name) { // [!code ++]
        System.out.println("printName: " + name); // [!code ++]
    } // [!code ++]
}
```

编写方法拦截器

```java
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import java.lang.reflect.Method;

public class UserServiceInterceptor implements MethodInterceptor {

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        if (method.getName().equals("addUser")) {
            System.out.println("Before method: " + method.getName()); // [!code --]
            printName("Before", method.getName()); // [!code ++]
            Object result = proxy.invokeSuper(obj, args);
            System.out.println("After method: " + method.getName()); // [!code --]
            printName("After", method.getName()); // [!code ++]
            return result;
        } else {
            return proxy.invokeSuper(obj, args);
        }
    }

    private void printName(String hook, String name) { // [!code ++]
        System.out.println(hook + " printName :" + name); // [!code ++]
    } // [!code ++]
}
```

使用 Enhancer 动态生成代理类：

```java
import net.sf.cglib.proxy.Enhancer;

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
```

打印执行

```text
# 热重载之前
Before method: addUser
Add user: DebugTools
After method: addUser
# 热重载之后
Before printName: addUser
printName: DebugTools
After printName: addUser
```