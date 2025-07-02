# Solon hot reload <Badge type="warning" text="beta" />

- Support adding and modifying beans in Solon container

- [Quickly call java method](attach-local.md) Support calling Solon beans

### Controller

Adding or modifying Controller layer classes can be hot reloaded and Mapping annotations (such as @Mapping, @Get, @Post, etc.) information will be reparsed.

```java
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Get;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;

@Controller
public class DemoController { 

    @Inject // [!code ++] 
    private DemoService demoService; // [!code ++] 

    @Get // [!code ++] 
    @Mapping("/demo1") // [!code ++] 
    public String demo1() { // [!code ++] 
        return demoService.hello3211("solon1"); // [!code ++] 
    } // [!code ++] 

    @Get // [!code --] 
    @Mapping("/demo2") // [!code --] 
    public String demo2() { // [!code --] 
        return "demo2"; // [!code --]
    @Post // [!code ++]
    @Mapping("/demo3") // [!code ++]
    public String demo3() { // [!code ++]
        return "demo3"; // [!code ++]
    }

}
```

- The previous `/demo2` is no longer accessible, but the newly added `/demo3` can be accessed.
- The newly added `/demo1` can be accessed, and the used `DemoService` will also be injected into `DemoController`.

### Component

Simple AOP implementation, using the `@Around` annotation to print logs before and after method execution.

```java
import org.noear.solon.annotation.Around;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Around(TestAopInterceptor.class)
public @interface TestAop {
}
```

Concrete implementation class

```java
import org.noear.solon.core.aspect.Interceptor;
import org.noear.solon.core.aspect.Invocation;

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
```

At this time we overload the `DemoService` class as follows

```java
import io.github.future0923.debug.tools.test.solon.aop.TestAop;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

@Component
public class DemoService { 

    @Inject 
    private UserService userService; 

    @TestAop // [!code ++]
    public String hello1(String name) {
        return userService.ab();
    }

    public String hello2(String name) { // [!code ++]
        return userService.ab(); // [!code ++]
    } // [!code ++]
    
    public String hello3(String name) { // [!code --]
        return userService.ab(); // [!code --]
    } // [!code --]
}
```

- After overloading, when `hello1()` is called, the `@TestAop` annotation will take effect to print `before` and `after` logs, and the `userService.ab()` method will be called.
- The newly added `hello2()` method can be called.
- The deleted `hello3()` method cannot be found.

### Configuration

Added `@Bean` in `@Configuration` to create `TestConfigBean`

```java
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;

@Configuration
public class TestConfig { 

    public static class TestConfigBean { 

        public String getName() { // [!code ++] 
            return "test"; // [!code ++] 
        } // [!code ++] 
    } 

    @Bean // [!code ++] 
    public TestConfigBean testConfigBean() { 
        return new TestConfigBean(); 
    }
}
```

Inject into `DemoService` and use

```java
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

@Component
public class DemoService { 

    @Inject // [!code ++]
    private TestConfig.TestConfigBean testConfigBean; // [!code ++]

    public String hello(String name) {
        return null; // [!code --]
        return testConfigBean.getName(); // [!code ++]
    }

}
```

- The newly added `TestConfigBean` can be obtained in the container, and `DemoService` can use `testConfigBean`.