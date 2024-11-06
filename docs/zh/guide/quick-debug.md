# 调试面板 {#quick-debug}

在要调用的方法上唤醒右键菜单，点击 `Quick Debug` 唤醒 **调试面板**。

![idea_menu.png](/images/idea_menu.png){v-zoom}

![quick_debug](/images/quick_debug.png){v-zoom}

## ClassLoader {#classloader}

要想调用方法，就需要获取其 **Class信息** 或 **实例信息**，所以需要选择调用方法的 [ClassLoader](./classloader) ，默认选中默认类加载器。

::: tip 注意
类加载器除了附着第一次获取其他都是使用的缓存，如果通过热重载等加载了其他的类，那么需要通过 `Refresh` 刷新类加载器。
:::

## CurrentClass {#currentclass}

设置要调用方法所在的类，默认为鼠标所在方法的类。

## CurrentMethod

设置要调用方法，默认为鼠标所在的方法。

## XXL-Job Param

如果调用的方法时 [xxl-job](./xxl-job) 的方法，那么可以在此处传递参数。

## Header

如果需要对当前方法传递 [Header](./header) 参数，可以在此处设置。

## 传递方法参数 {#debugtools-json}

DebugTools 使用 JSON 格式传递方法参数，我们称为 `DebugToolsJson` 。格式为:

```json
{
  "方法参数名字": {
    "type": "参数类型",
    "content": "参数内容"
  }
}
```

### type支持类型

#### simple

::: details 当方法参数是简单类型时，type 为 simple，`content 为具体的值`。唤醒控制面板时符合的参数 type 会自动识别为 simple。 

- 原始类型
- String、other CharSequence
- Number
- Date
- URI
- URL
- Locale
- Class

:::

::: details 转换方式

- `spring环境下`：
  - **Date**：格式化 `yyyy-MM-dd HH:mm:ss` 的字符串
  - **LocalDateTime**：格式化 `yyyy-MM-dd HH:mm:ss` 的字符串
  - **LocalDate**：格式化 `yyyy-MM-dd` 的字符串
  - **LocalTime**：格式化 `HH:mm:ss` 的字符串
  - **其他情况**：通过`org.springframework.beans.SimpleTypeConverter` 的 `convertIfNecessary` 方法转换
- `非spring环境下`：
  - **Date**：格式化 `yyyy-MM-dd HH:mm:ss` 的字符串
  - **LocalDateTime**：格式化 `yyyy-MM-dd HH:mm:ss` 的字符串
  - **LocalDate**：格式化 `yyyy-MM-dd` 的字符串
  - **LocalTime**：格式化 `HH:mm:ss` 的字符串
  - **其他情况**：通过`cn.hutool.core.convert.Convert` 的 `convert` 方法转换

:::

#### enum

当方法参数是枚举类型时，type 为 enum，`content 为枚举的 name 信息`。唤醒控制面板时参数如果是 `enum` 时 type 会自动识别为 enum。

::: details 转换方式

**content** 中需要填入对应枚举的 `name` 进行转换，使用 `Enum.valueOf()` 方法

:::

::: details 示例

枚举值如下

```java
public enum TestEnum {
    
    YES(1, "是"),
  
    NO(0, "否");
    
    private final int code;
    
    private final String desc;
    
    public TestEnum(int code, String desc) {
        
    }
}

// 方法为
public void test(TestEnum testEnum) {
    
}
```

如要传递给 test 方法的 testEnum 参数传入 YES

```json
{
  "testEnum": {
    "type": "enum",
    "content": "YES"
  }
}
```

:::

#### json_entity

DebugTools 通过 content 中的信息以 `JSON` 格式给 Java 实体对象赋值。

唤醒控制面板时参数如果符合时 type 会自动识别为 json_entity。

::: details 识别方式

1. 数组类型
2. 非其他特殊类型的实体

:::

::: details 示例

要传递的java实体如下：

```java
public class TestDTO {
    
    private String name;
    
    private Integer age;
    
}
```

要调用的方法如下：

```java
public class TestService {
    
    public void test(TestDTO testDTO) {
        
    }
}
```

调用方法时传入参数：

```json
{
  "testDTO" : {
    "type": "json_entity",
    "content": {
      "name": "DebugTools",
      "age": 3
    }
  }
}
```
:::

#### lambda

DebugTools 通过 [lambdaFromString](https://github.com/greenjoe/lambdaFromString) 将 content 中的 String 转化为 lambda 表达式。

唤醒控制面板时参数如果符合时 type 会自动识别为 lambda。

::: details 识别方式

类是 `接口` 并且有 `java.lang.FunctionalInterface` 注解

:::

::: details 示例

要传递的 lambda 表达式如下：

```java
package java.util.function;

import java.util.Objects;

@FunctionalInterface
public interface BiFunction<T, U, R> {
    
  R apply(T t, U u);

}
```

要调用的方法如下：

```java
public class TestService {

  public Integer add(BiFunction<Integer, Integer, Integer> function) {
    return function.apply(1, 2);
  }
}
```

调用方法时传入参数：

```json
{
  "function" : {
    "type": "lambda",
    "content": "(x, y) -> x + y"
  }
}
```
:::

#### bean

方法参数是 Spring Bean 类型时，`type 为 bean`，不需要指定 `content`。

唤醒控制面板时参数如果符合时 type 会自动识别为 bean。

::: details 识别方式

类是上可以获取到下面注解，直接间接都可以：
1. `org.springframework.stereotype.Controller`
2. `org.springframework.stereotype.Service`
3. `org.springframework.stereotype.Component`
4. `org.springframework.stereotype.Repository`

:::

::: details 转换方式

- DebugTools 会通过选择的 [classloader](./classloader) 加载方法的 Class 信息，获取到要注入 Bean 的 Class 信息。
- 优先通过 spring 上下文获取，如果有多个取第一个
- 获取不到从jvm中获取，如果有多个取第一个
- 获取不到调用构造方法创建

:::

::: details 示例

```java
@Service
public class TestBean1 {
    
}
```

要调用的方法如下：

```java
@Service
public class TestBean2 {

    public void test(TestBean1 testBean1) {
        
    }
}
```

调用方法时传入参数：

```json
{
  "function" : {
    "type": "bean"
  }
}
```

:::

#### request

方法参数是 request 类型时，type 为 request，不需要指定 `content`。

唤醒控制面板时参数如果符合时 type 会自动识别为 request。

::: details 识别方式

类是 `接口` 并且固定为 `javax.servlet.http.HttpServletRequest`。 

:::

::: details 转换方式

DebugTools 通过自定义的 `MockHttpServletRequest` 类实现了 `tomcat` 的 `javax.servlet.http.HttpServletRequest` 接口。  
如果传递了 [header](./header) 信息，则使用携带 header 信息的 MockHttpServletRequest，否则创建新的 MockHttpServletRequest。

:::

::: details 示例

调用方法接收 `HttpServletRequest` 参数

```java
import javax.servlet.http.HttpServletRequest;

public class TestService {

  public void test(HttpServletRequest request) {

  }
}

```

调用方法时传入参数：

```json
{
  "request" : {
    "type": "request"
  }
}
```

:::

#### response

方法参数是 response 类型时，type 为 response，不需要指定 `content`。

唤醒控制面板时参数如果符合时 type 会自动识别为 response。

::: details 识别方式

类是 `接口` 并且固定为 `javax.servlet.http.HttpServletResponse`。

:::

::: details 转换方式

DebugTools 通过自定义的 `MockHttpServletRequest` 类实现了 `tomcat` 的 `javax.servlet.http.HttpServletRequest` 接口。  
如果传递了 [header](./header) 信息，则使用携带 header 信息的 MockHttpServletRequest，否则创建新的 MockHttpServletRequest。

:::

::: details 示例

调用方法接收 `HttpServletResponse` 参数

```java
import javax.servlet.http.HttpServletResponse;

public class TestService {

  public void test(HttpServletResponse response) {

  }
}

```

调用方法时传入参数：

```json
{
  "response" : {
    "type": "response"
  }
}
```

:::

#### file

方法参数是 `文件` 类型时，type 为 file，`content 为文件绝对路径`。

唤醒控制面板时参数如果符合时 type 会自动识别为 file。

::: details 识别方式

1. methodArgClass.isAssignableFrom(Class.class) 返回 true。
2. 类是 `接口` 并且固定为 `org.springframework.web.multipart.MultipartFile`。

:::

::: details 转换方式

DebugTools 通过 content 获取文件的绝对路径。
- 通过创建 `File` 实例传入
- 如果是 `MultipartFile` 类型则创建 `MockMultipartFile` 实例传入

:::

::: details 示例

调用方法接收 `HttpServletResponse` 参数

```java
import javax.servlet.http.HttpServletResponse;

public class TestService {

  public void test(HttpServletResponse response) {

  }
}

```

调用方法时传入参数：

```json
{
  "response" : {
    "type": "response"
  }
}
```

:::


#### class

方法参数是 `java.lang.Class` 类型时，type 为 class，`content 为类的全路径`。

唤醒控制面板时参数如果符合时 type 会自动识别为 class。

::: details 识别方式

methodArgClass.isAssignableFrom(Class.class) 返回 true。

:::

::: details 转换方式

DebugTools 通过 content 获取类信息，采用选择的 [类加载器](./classloader) 获取到 class 实例传入

:::

::: details 示例

调用方法接收 `Class` 参数

```java
public class TestService {

  public String test(Class<?> clz) {
    return clz.getName();
  }
}

```

调用方法时传入参数：

```json
{
  "response" : {
    "type": "class",
    "content": "java.lang.String"
  }
}
```

:::


### 快捷操作

#### 格式转化

当前端给我们参数时，我们希望他能直接用 DebugTools 调用，但是 DebugTools 只接受 DebugToolsJson 格式，因此需要我们先将参数转化为 DebugToolsJson。

目前支持的 `json`、`query` 和 `path` 格式。

::: tip

**path** 格式是通过属性顺序转换的。  
实体类型仅支持 **json** 格式转换。

:::

##### 其他格式转DebugToolsJson格式

将其它格式转为DebugToolsJson时，在调试面板点击 <img src="/icon/import.svg" style="display: inline-block; width: 20px; height: 20px; vertical-align: middle;" /> 唤醒。点击 `Convert` 按钮后会自动添加到调试面板。

![import.png](/images/import.png){v-zoom}

::: details 示例

::: code-group

```json [json]
{
    "name": "DebugTools",
    "age": 18
}
```

```text [query]
name=DebugTools&age=18
```

```text [path]
/DebugTools/18
```

```json [转换为 DebugToolsJson 格式]
{
  "name": {
    "type": "simple",
    "content": "DebugTools"
  },
  "age": {
    "type": "simple",
    "content": 18
  }
}
```

:::

#####  DebugToolsJson格式转其他格式

将DebugToolsJson转为其它格式时，在调试面板点击 <img src="/icon/export.svg" style="display: inline-block; width: 20px; height: 20px; vertical-align: middle;" /> 唤醒。点击 `Copy` 按钮后会复制到剪切板。

![import.png](/images/export.png){v-zoom}

::: details 示例

::: code-group

```json [要转换的 DebugToolsJson 格式]
{
  "name": {
    "type": "simple",
    "content": "DebugTools"
  },
  "age": {
    "type": "simple",
    "content": 18
  }
}
```

```json [json]
{
    "name": "DebugTools",
    "age": 18
}
```

```text [query]
name=DebugTools&age=18
```

```text [path]
/DebugTools/18
```

:::

##### 美化

在调试面板点击 <img src="/icon/pretty.svg" style="display: inline-block; width: 20px; height: 20px; vertical-align: middle;" /> 会美化调试面板的DebugToolsJson。

#### 填入DebugToolsJson {#fill-debug-tools-json}

DebugTools 可以根据方法参数自动生成 DebugToolsJson，目前支持 `simple`、`current` 和 `all` 三种模式。

在没有运行过的时，第一次 DebugTools 会自动生成 DebugToolsJson，通过配置可以修改默认生成哪种模式。

![example_config.png](/images/example_config.png){v-zoom}

下面例子中的 `Child` 类如下:

```java
public class Parent {
    
    private String name;
    
    private Integer age;
}

public class Child extends Parent {
    
    private Integer sex;
}
```

##### Simple

在调试面板点击 <img src="/icon/example_simple.svg" style="display: inline-block; width: 20px; height: 20px; vertical-align: middle;" /> 可以生成简单的DebugToolsJson，如下：

```json
{
  "child": {
    "type": "json_entity",
    "content": {
    }
  }
}
```

##### Current

在调试面板点击 <img src="/icon/example_current.svg" style="display: inline-block; width: 20px; height: 20px; vertical-align: middle;" /> 可以生成当前类的DebugToolsJson，如下：

```json
{
  "child": {
    "type": "json_entity",
    "content": {
      "sex": {
        "type": "simple",
        "content": 0
      }
    }
  }
}
```

##### All

在调试面板点击 <img src="/icon/example_all.svg" style="display: inline-block; width: 20px; height: 20px; vertical-align: middle;" /> 可以生成当前类及所有父类属性的DebugToolsJson，如下：

```json
{
  "child": {
    "type": "json_entity",
    "content": {
      "sex": {
        "type": "simple",
        "content": 0
      },
      "name": {
        "type": "simple",
        "content": ""
      },
      "age": {
        "type": "simple",
        "content": 0
      }
    }
  }
}
```

## 存储 {#store}

如果参数很多，每次都输入很麻烦，调用完成之后 DebugTools 会储存这个 **唯一方法** 的上次请求，下次唤醒这个方法调试面板时会自动填充。

::: tip 方法唯一标识生成方式

通过方法所在 `类 + 方法名 + 方法参数` 生成唯一标识，目前每个方法只保留最后一次请求。

:::

DebugTools 还可以 [快捷调用上一次](execute-last.md) 方法。 