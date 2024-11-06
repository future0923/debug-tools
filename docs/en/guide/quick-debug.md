# Debug Panel {#quick-debug}

Wake up the right-click menu on the method to be called, and click `Quick Debug` to wake up the **debug panel**.

![idea_menu.png](/images/idea_menu.png){v-zoom}

![quick_debug](/images/quick_debug.png){v-zoom}

## ClassLoader {#classloader}

To call a method, you need to obtain its **Class information** or **Instance information**, so you need to select the [ClassLoader](./classloader) of the calling method. The default class loader is selected by default.

::: tip
Class loaders are cached except for the first acquisition. If other classes are loaded through hot reload, you need to refresh the class loader through `Refresh`.
:::

## CurrentClass {#currentclass}

Set the class where the method to be called is located. The default is the class of the method where the mouse is located.

## CurrentMethod

Set the method to be called. The default is the method where the mouse is.

## XXL-Job Param

If the method to be called is the [xxl-job](./xxl-job) method, you can pass parameters here.

## Header

If you need to pass the [Header](./header) parameter to the current method, you can set it here.

## Pass method parameters {#debugtools-json}

DebugTools uses JSON format to pass method parameters, which we call `DebugToolsJson`. The format is:

```json
{
  "Method parameter name": {
    "type": "Parameter Type",
    "content": "Parameter Content"
  }
}
```

### type supported types

#### simple

::: details When the method parameter is a simple type, type is simple and `content is a specific value`. When waking up the control panel, the parameter type that meets the requirements will be automatically recognized as simple.

- Original type
- String, other CharSequence
- Number
- Date
- URI
- URL
- Locale
- Class

:::

::: details Conversion method

- `Spring environment`:
  - **Date**: Format `yyyy-MM-dd HH:mm:ss` string
  - **LocalDateTime**: Format `yyyy-MM-dd HH:mm:ss` string
  - **LocalDate**: Format `yyyy-MM-dd` string
  - **LocalTime**: Format `HH:mm:ss` string
  - **Other cases**: Convert through `convertIfNecessary` method of `org.springframework.beans.SimpleTypeConverter`
- `Non-Spring environment`:
  - **Date**: Format `yyyy-MM-dd HH:mm:ss` string
  - **LocalDateTime**: Format `yyyy-MM-dd HH:mm:ss` string
  - **LocalDate**: format `yyyy-MM-dd` string
  - **LocalTime**: format `HH:mm:ss` string
  - **Other cases**: convert through `convert` method of `cn.hutool.core.convert.Convert`

:::

#### enum

When the method parameter is an enumeration type, type is enum, and `content is the name information of the enumeration`. When waking up the control panel, if the parameter is `enum`, type will automatically be recognized as enum.

::: details Conversion method

**content** needs to fill in the `name` of the corresponding enumeration for conversion, using the `Enum.valueOf()` method

:::

::: details Example

The enumeration values are as follows

```java
public enum TestEnum {
    
    YES(1, "yes"),
  
    NO(0, "no");
    
    private final int code;
    
    private final String desc;
    
    public TestEnum(int code, String desc) {
        
    }
}

// method
public void test(TestEnum testEnum) {
    
}
```

If you want to pass the testEnum parameter to the test method, pass in YES

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

DebugTools assigns values ​​to Java entity objects in `JSON` format through the information in content.

When waking up the control panel, if the parameter type meets the requirements, it will automatically be identified as json_entity.

::: details Identification method

1. Array type
2. Entity of non-other special types

:::

::: details Example

The java entity to be passed is as follows:

```java
public class TestDTO {
    
    private String name;
    
    private Integer age;
    
}
```

The method to call is as follows:

```java
public class TestService {
    
    public void test(TestDTO testDTO) {
        
    }
}
```

Passing parameters when calling a method：

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

DebugTools converts the String in content into a lambda expression through [lambdaFromString](https://github.com/greenjoe/lambdaFromString).

When waking up the control panel, if the parameter type meets the requirements, it will automatically be identified as lambda.

::: details Identification method

The class is an `interface` and has the `java.lang.FunctionalInterface` annotation

:::

::: details Example

The lambda expression to be passed is as follows:

```java
package java.util.function;

import java.util.Objects;

@FunctionalInterface
public interface BiFunction<T, U, R> {
    
  R apply(T t, U u);

}
```

The method to be called is as follows：

```java
public class TestService {

  public Integer add(BiFunction<Integer, Integer, Integer> function) {
    return function.apply(1, 2);
  }
}
```

Passing parameters when calling a method：

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

When the method parameter is of Spring Bean type, `type is bean`, and `content` does not need to be specified.

When waking up the control panel, if the parameter meets the requirements, type will be automatically identified as bean.

::: details Identification method

The following annotations can be obtained on the class, either directly or indirectly:

1. `org.springframework.stereotype.Controller`
2. `org.springframework.stereotype.Service`
3. `org.springframework.stereotype.Component`
4. `org.springframework.stereotype.Repository`

:::

::: details Conversion method

- DebugTools will load the class information of the method through the selected [classloader](./classloader) to obtain the class information to be injected into the Bean.
- Get it from spring context first, if there are multiple, take the first one
- If it cannot be obtained, get it from jvm, if there are multiple, take the first one
- If it cannot be obtained, call the constructor to create

:::

::: details Example

```java
@Service
public class TestBean1 {
    
}
```

The method to be called is as follows：

```java
@Service
public class TestBean2 {

    public void test(TestBean1 testBean1) {
        
    }
}
```

Passing parameters when calling a method：

```json
{
  "function" : {
    "type": "bean"
  }
}
```

:::

#### request

When the method parameter is of request type, type is request, and `content` does not need to be specified.

When waking up the control panel, if the parameter meets the requirements, type will be automatically identified as request.

::: details Identification method

The class is `interface` and is fixed to `javax.servlet.http.HttpServletRequest`.

:::

::: details Conversion method

DebugTools implements `tomcat`'s `javax.servlet.http.HttpServletRequest` interface through a custom `MockHttpServletRequest` class.

If [header](./header) information is passed, MockHttpServletRequest with header information is used, otherwise a new MockHttpServletRequest is created.

:::

::: details Example

Call method to receive `HttpServletRequest` parameters

```java
import javax.servlet.http.HttpServletRequest;

public class TestService {

  public void test(HttpServletRequest request) {

  }
}

```

Passing parameters when calling a method：

```json
{
  "request" : {
    "type": "request"
  }
}
```

:::

#### response

When the method parameter is of response type, type is response, and `content` does not need to be specified.

When waking up the control panel, if the parameter matches, type will be automatically identified as response.

::: details Identification method

The class is `interface` and is fixed to `javax.servlet.http.HttpServletResponse`.

:::

::: details Conversion method

DebugTools implements `tomcat`'s `javax.servlet.http.HttpServletRequest` interface through a custom `MockHttpServletRequest` class.

If [header](./header) information is passed, MockHttpServletRequest with header information is used, otherwise a new MockHttpServletRequest is created.

:::

::: details Example

Call method to receive `HttpServletResponse` parameter

```java
import javax.servlet.http.HttpServletResponse;

public class TestService {

  public void test(HttpServletResponse response) {

  }
}

```

Passing parameters when calling a method：

```json
{
  "response" : {
    "type": "response"
  }
}
```

:::

#### file

When the method parameter is of `file` type, type is file and `content is the absolute path of the file`.

When waking up the control panel, if the parameter meets the requirements, type will be automatically identified as file.

::: details Identification method

1. methodArgClass.isAssignableFrom(Class.class) returns true.
2. The class is `interface` and is fixed to `org.springframework.web.multipart.MultipartFile`.

:::

::: details Conversion method

DebugTools obtains the absolute path of the file through content.

- Pass it in by creating a `File` instance
- If it is of `MultipartFile` type, create a `MockMultipartFile` instance and pass it in

:::

::: details Example

Call method to receive `HttpServletResponse` parameters

```java
import javax.servlet.http.HttpServletResponse;

public class TestService {

  public void test(HttpServletResponse response) {

  }
}

```

Passing parameters when calling a method：

```json
{
  "response" : {
    "type": "response"
  }
}
```

:::


#### class

When the method parameter is of type `java.lang.Class`, type is class, and `content is the full path of the class`.

When waking up the control panel, if the parameter meets the requirements, type will be automatically identified as class.

::: details Identification method

methodArgClass.isAssignableFrom(Class.class) returns true.

:::

::: details Conversion method

DebugTools obtains class information through content, and uses the selected [class loader](./classloader) to obtain the class instance and pass it in

:::

::: details Example

Calling a method to receive the `Class` parameter

```java
public class TestService {

  public String test(Class<?> clz) {
    return clz.getName();
  }
}

```

Passing parameters when calling a method：

```json
{
  "response" : {
    "type": "class",
    "content": "java.lang.String"
  }
}
```

:::


### Quick Operation

#### Format Conversion

When the front-end gives us parameters, we hope that it can be called directly with DebugTools, but DebugTools only accepts DebugToolsJson format, so we need to convert the parameters to DebugToolsJson first.

Currently supported `json`, `query` and `path` formats.

::: tip

**path** format is converted by attribute order.  
Entity type only supports **json** format conversion.

:::

##### Convert other formats to DebugToolsJson format

When converting other formats to DebugToolsJson, click <img src="/icon/import.svg" style="display: inline-block; width: 20px; height: 20px; vertical-align: middle;" /> in the debug panel to wake up. Click the `Convert` button and it will be automatically added to the debug panel.

![import.png](/images/import.png){v-zoom}

::: details Example

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

```json [Convert to DebugToolsJson format]
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

##### Convert DebugToolsJson to other formats

When converting DebugToolsJson to other formats, click <img src="/icon/export.svg" style="display: inline-block; width: 20px; height: 20px; vertical-align: middle;" /> in the debug panel to wake it up. Click the `Copy` button to copy it to the clipboard.

![import.png](/images/export.png){v-zoom}

::: details Example

::: code-group

```json [DebugToolsJson format to convert]
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

##### Pretty

Click <img src="/icon/pretty.svg" style="display: inline-block; width: 20px; height: 20px; vertical-align: middle;" /> in the debug panel to beautify the DebugToolsJson of the debug panel.

#### Fill in DebugToolsJson {#fill-debug-tools-json}

DebugTools can automatically generate DebugToolsJson based on method parameters, currently supporting three modes: `simple`, `current` and `all`.

If it has not been run before, DebugTools will automatically generate DebugToolsJson for the first time. You can modify the default generation mode through configuration.

![example_config.png](/images/example_config.png){v-zoom}

The `Child` class in the following example is as follows:

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

Click <img src="/icon/example_simple.svg" style="display: inline-block; width: 20px; height: 20px; vertical-align: middle;" /> in the debug panel to generate a simple DebugToolsJson, as follows:

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

Click <img src="/icon/example_current.svg" style="display: inline-block; width: 20px; height: 20px; vertical-align: middle;" /> in the debug panel to generate DebugToolsJson for the current class, as follows:

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

Click <img src="/icon/example_all.svg" style="display: inline-block; width: 20px; height: 20px; vertical-align: middle;" /> in the debug panel to generate DebugToolsJson for the current class and all parent class properties, as follows:

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

## Store {#store}

If there are many parameters, it is troublesome to enter them every time. After the call is completed, DebugTools will store the last request of this **unique method**, and it will be automatically filled when the debug panel of this method is woken up next time.

::: tip How to generate a unique identifier for a method

Generate a unique identifier through the `class + method name + method parameters` where the method is located. Currently, each method only retains the last request.

:::

DebugTools can also [Quickly call the last time](execute-last.md) method.