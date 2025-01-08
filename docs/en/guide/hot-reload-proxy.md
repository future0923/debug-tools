# Hot reload of proxy classes

Hot reload of proxy classes is very important because [Spring](hot-reload-springboot.md) and [MyBatisPlus](hot-reload-mybatis-plus.md) etc. enhance the code through proxy classes.

## JDK

Define interface

```java
public interface UserService {

    void addUser(String name);

}
```

Define the implementation class

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

Define the InvocationHandler class

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

Using Proxy to create a proxy object

```java
import java.lang.reflect.Proxy;

public class ProxyTest {

    public static void main(String[] args) throws InterruptedException {
        UserService target = new UserServiceImpl();
        UserServiceInvocationHandler handler = new UserServiceInvocationHandler(target);
        UserService proxy = (UserService) Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                target.getClass().getInterfaces(),
                handler
        );
        while (true) {
            Thread.sleep(10000);
            proxy.addUser("DebugTools");
        }
    }
}
```

Print Result

```text
# Before hot reload
Before method: addUser
Add user: DebugTools
After method: addUser
# After hot reload
Before printName: addUser
printName: DebugTools
After printName: addUser
```

## Cglib

Creating a proxy object

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

Writing method interceptors

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

Use Enhancer to dynamically generate proxy classes

```java
import net.sf.cglib.proxy.Enhancer;

public class ProxyTest {

    public static void main(String[] args) throws InterruptedException {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(UserService.class);
        enhancer.setCallback(new UserServiceInterceptor());
        UserService proxy = (UserService) enhancer.create();
        while (true) {
            Thread.sleep(1000);
            proxy.addUser("DebugTools");
        }
    }
}
```

Print Result

```text
# Before hot reload
Before method: addUser
Add user: DebugTools
After method: addUser
# After hot reload
Before printName: addUser
printName: DebugTools
After printName: addUser
```