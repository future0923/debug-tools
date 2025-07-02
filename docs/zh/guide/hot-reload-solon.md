# Solon热重载 <Badge type="warning" text="beta" />

- 支持Solon容器的bean新增、修改
- [快捷调用java方法](attach-local.md)支持调用Solon的bean

### Controller

Controller层类的新增或修改可以进行热重载并会重新解析 Mapping 注解（如 @Mapping、@Get、@Post等）信息。

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

    @Get  // [!code --]
    @Mapping("/demo2")  // [!code --]
    public String demo2() {  // [!code --]
        return "demo2";  // [!code --]
    @Post  // [!code ++]
    @Mapping("/demo3")  // [!code ++]
    public String demo3() {  // [!code ++]
        return "demo3";  // [!code ++]
    }

}
```

- 之前的 `/demo2` 访问不了了，可以访问新增的 `/demo3`。
- 可以访问新增的 `/demo1`，使用到的 `DemoService` 也会注入到 `DemoController` 中。

### Component

简单的AOP实现，使用 `@Around` 注解打印方法执行前后的日志。

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

具体实现类

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

这时我们重载`DemoService`类如下

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
    }  // [!code ++]

    public String hello3(String name) { // [!code --]
        return userService.ab(); // [!code --]
    } // [!code --]
}
```

- 重载之后调用 `hello1()` 时候 `@TestAop` 注解即可生效打印 `before` 和 `after` 日志，并且会调用 `userService.ab()` 方法。
- 可以调用新增的 `hello2()`方法。
- 删除的 `hello3()` 方法找不到了。

### Configuration

在 `@Configuration` 新增了 `@Bean` 创建 `TestConfigBean`

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

注入到 `DemoService` 中并使用

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

- 可以容器中可以获取到新增的 `TestConfigBean`，并且 `DemoService` 可以使用 `testConfigBean`。