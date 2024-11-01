# 内置函数 {#groovy-function}

为了方便获取附着应用信息，`DebugTools` 提供了一些 `Groovy` 内置函数。

## 获取JVM实例 {#jvm-instance}

通过调用 `gi` / `getInstances` 传入 class 对象获取该类当前 JVM 实例数组。

**函数定义**

```java
/**
 * 获取JVM中指定Class所有的实例
 *
 * @param targetClass 要获取的class对象
 * @param <T>         具体的类型
 * @return 实例数组
 */
public <T> T[] gi(Class<T> targetClass) {}

/**
 * 获取JVM中指定Class所有的实例
 *
 * @param targetClass 要获取的class对象
 * @param <T>         具体的类型
 * @return 实例数组
 */
public <T> T[] getInstances(Class<T> targetClass) {}
```

**使用示例**

```groovy
import org.springframework.context.ApplicationContext
// gi或者getInstances 获取jvm实例
gi(ApplicationContext.class)
gi ApplicationContext.class
getInstances(ApplicationContext.class)
getInstances ApplicationContext.class
```

## Spring Bean操作 {#spring-bean-operation}

### 注入Bean实例 {#inject-bean-instance}

通过调用 `rb` / `registerBean` 注入Bean实例。

**函数定义**

```java
/**
 * 向Spring容器中注入实例，通过Spring的BeanName规则生成BeanName
 *
 * @param bean 要获取的bean
 * @param <T>  具体类型
 */
public <T> void rb(T bean) {}

/**
 * 向Spring容器中注入实例，通过Spring的BeanName规则生成BeanName
 *
 * @param bean 要获取的bean
 * @param <T>  具体类型
 */
public <T> void registerBean(T bean) {}

/**
 * 向Spring容器中注入实例并指定BeanName
 *
 * @param beanName 注入的BeanName
 * @param bean     要获取的bean
 * @param <T>      具体类型
 */
public <T> void rb(String beanName, T bean) {}

/**
 * 向Spring容器中注入实例并指定BeanName
 *
 * @param beanName 注入的BeanName
 * @param bean     要获取的bean
 * @param <T>      具体类型
 */
public <T> void registerBean(String beanName, T bean) {}
```

**使用示例**

```groovy
class TestBean {
    String hello(String name) {
        return "hello " + name
    }
}

def testBean = new TestBean()
rb(testBean)
rb("testBean", testBean)
registerBean(testBean)
registerBean("testBean", testBean)
```

### 获取Bean实例 {#get-bean-instance}

通过调用 `gb` / `getBean` 获取Spring Bean实例。

**函数定义**

```java
/**
 * 获取Spring容器中指定Bean的所有实例
 *
 * @param beanClass 要获取的class对象
 * @param <T>       具体类型
 * @return 实例数组
 */
public <T> T[] gb(Class<T> beanClass) {}

/**
 * 获取Spring容器中指定Bean的所有实例
 *
 * @param beanClass 要获取的class对象
 * @param <T>       具体类型
 * @return 实例数组
 */
public <T> T[] getBean(Class<T> beanClass) {}

/**
 * 获取Spring容器中指定Bean的所有实例
 *
 * @param beanName 要获取的bean名称
 * @param <T>      具体类型
 * @return 实例数组
 */
public <T> T[] gb(String beanName) {}

/**
 * 获取Spring容器中指定Bean的所有实例
 *
 * @param beanName 要获取的bean名称
 * @param <T>      具体类型
 * @return 实例数组
 */
public <T> T[] getBean(String beanName) {}
```

**使用示例**

```groovy
gb(TestBean.class)
// 获取注入的testBean，执行hello方法
gb("testBean")[0].hello("debug tools")
gb TestBean.class
getBean(TestBean.class)
getBean("testBean")
getBean TestBean.class
```

### 销毁Bean实例 {#destroy-bean-instance}

通过调用 `urb` / `unregisterBean` 销毁Spring Bean实例。

**函数定义**

```java
/**
 * 销毁指定名称的Bean
 *
 * @param beanName 要销毁的Bean名称
 */
public void urb(String beanName) {}

/**
 * 销毁指定名称的Bean
 *
 * @param beanName 要销毁的Bean名称
 */
public void unregisterBean(String beanName) {}
```

**使用示例**

```groovy
urb("testBean")
unregisterBean("testBean")
```

## 获取Spring运行环境 {#environment-config}

通过调用 `gActive` / `getSpringProfilesActive` 获取 `spring.profiles.active` 配置信息。

**函数定义**

```java
/**
 * 获取spring运行环境
 *
 * @return 运行环境
 */
public String gActive() {}

/**
 * 获取spring运行环境
 *
 * @return 运行环境
 */
public String getSpringProfilesActive() {}
```

**使用示例**

```groovy
gActive()
getSpringProfilesActive()
```

## 获取Spring配置信息 {#spring-config}

通过调用 `gsc` / `getSpringConfig` 获取spring配置信息。

**函数定义**

```java
/**
 * 获取spring配置
 *
 * @param key 配置key
 * @return 具体配置信息
 */
public Object gsc(String key) {}

/**
 * 获取spring配置
 *
 * @param key 配置key
 * @return 具体配置信息
 */
public Object getSpringConfig(String key) {}
```

**使用示例**

```groovy
gsc("spring.application.name")
gsc "spring.application.name"
getSpringConfig("spring.application.name")
getSpringConfig "spring.application.name"
```

## 完整示例 {#complete-example}

```groovy
import org.springframework.context.ApplicationContext

// 返回结果
class ResultDTO {
    // gi的结果
    ApplicationContext[] gi
    // gb的结果
    TestBean[] gb
    // 调用hello方法的执行结果
    String helloMethodResult
    // 当前环境
    String active
    // 应用名
    String applicationName
}

// 要注入TestBean
class TestBean {

    String hello(String name) {
        return "hello " + name
    }
}

def result = new ResultDTO()

// gi或者getInstances 获取jvm实例
def v1 = gi(ApplicationContext.class)
result.gi = v1
gi ApplicationContext.class
getInstances(ApplicationContext.class)
getInstances ApplicationContext.class

def testBean = new TestBean()
// 向spring中注册bean
rb(testBean)
//rb("testBean", testBean)
//registerBean(testBean)
//registerBean("testBean", testBean)


// gb或者getBean 获取spring bean
def v2 = gb(TestBean.class)
result.gb = v2
// 获取注入的testBean，执行hello方法
result.helloMethodResult = gb("testBean")[0].hello("debug tools")
gb TestBean.class
getBean(TestBean.class)
getBean("testBean")
getBean TestBean.class

// 注销bean
urb("testBean")
//unregisterBean("testBean")

// gActive或者getSpringProfilesActive 获取当前spring环境
def v3 = gActive()
result.active = v3
getSpringProfilesActive()

// gsc或getSpringConfig 获取spring配置
def v4 = gsc("spring.application.name")
result.applicationName = v4
gsc "spring.application.name"
getSpringConfig("spring.application.name")
getSpringConfig "spring.application.name"
return result
```

通过 [debug](./run-result#debug) 方式查看到返回结果。

![groovy_example_debug](/images/groovy_example_debug.png){v-zoom}