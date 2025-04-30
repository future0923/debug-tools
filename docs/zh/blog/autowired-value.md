# @Autowired与@Value源码解析

- `@Autowired` 注解是Spring框架提供的一种依赖注入的方式，通过@Autowired注解，Spring容器会自动注入依赖的Bean。 
- `@Value` 注解是Spring框架提供的一种依赖注入的方式，通过@Value注解，Spring容器会自动注入依赖的属性值。

原理是使用反射机制，通过反射获取当前Bean的属性，然后通过属性名获取对应类型的值后注入到当前Bean的属性中。

核心方法通过 `AutowiredAnnotationBeanPostProcessor` 类来实现，它实现了 `BeanPostProcessor` 接口，在容器初始化过程中，会调用 `postProcessBeforeInitialization` 方法对Bean进行初始化，如果当前Bean的属性中包含@Autowired注解，则会调用对应方法进行依赖注入。

如我们的 `TestController` 代码如下:

```java
@RestController
public class TestController {
    
    @Autowired
    private UserService userService;

}
```

当我们创建 Bean 时候会调用 `AbstractAutowireCapableBeanFactory#doCreateBean()` 方法来创建 Bean。

![do_create_bean.png](/images/do_create_bean.png){v-zoom}

在 doCreateBean 的时候会调用 Bean 生命周期中的 `populateBean` 方法来给 Bean 的属性赋值。

![post_process_properties.png](/images/post_process_properties.png){v-zoom}

在 populateBean 中会执行注册的 `InstantiationAwareBeanPostProcessor` 类的 `postProcessProperties` 方法，而 `AutowiredAnnotationBeanPostProcessor` 正好是一个 InstantiationAwareBeanPostProcessor ，它的 postProcessProperties() 方法会被调用。

![invoke_inject.png](/images/invoke_inject.png){v-zoom}

这里会扫描当前Bean的所有字段、方法，看有没有 @Autowired、@Value、@Inject 等注解后封装为 `InjectionMetadata` 对象.

![element_inject.png](/images/element_inject.png){v-zoom}

这里会调用 InjectionMetadata 对象中的 `InjectedElement#inject` 方法来依次注入每个属性。

![set_element.png](/images/set_element.png){v-zoom}

最终 `AutowiredAnnotationBeanPostProcessor#inject` 方法会调用 `resolveFieldValue` 获取对应类型的 Bean 并反射通过 `field.set` 方法注入到当前 Bean 的属性中。

