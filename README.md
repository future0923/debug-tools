[English](README.md) | [中文](README-zh.md)

# DebugTools

- Quickly call Java methods (local/remote)
- Search http url to jump directly to the corresponding method definition.
- Print SQL statements and time consuming
- Execute Groovy scripts

## Documentation

To check out docs, visit [debug-tools.cc](https://debug-tools.cc).

## Changelog

Detailed changes for each release are documented in the [CHANGELOG](https://github.com/future0923/debug-tools/blob/main/CHANGELOG.md).

## Contribution

Please make sure to read the [Contributing Guide](https://github.com/future0923/debug-tools/blob/main/.github/contributing.md) before making a pull request.

## Quickstart

### Install

#### Marketplace

1. Open `IDE Settings` and select `Plugins`.
2. Search for `DebugTools` in the `Marketplace` and click `install`.
3. Restart.

![marketplace](/docs/public/images/marketplace.png)

#### Url

```text
https://plugins.jetbrains.com/plugin/24463-debugtools
```

#### Download

```text
https://download.debug-tools.cc/DebugToolsIdeaPlugin.zip
```

#### Build

```sh 
git clone https://github.com/future0923/debug-tools.git
cd debug-tools
mvn clean install -T 2C -Dmaven.test.skip=true
# dist dir
# debug-tools-boot.jar remote agent jar
cd debug-tools-idea
./gradlew clean buildPlugin
# dist dir
# DebugTools-{version}.zip IDEA plugin
```

#### github

```text
https://github.com/future0923/debug-tools/releases
```

#### gitee

```text
https://gitee.com/future94/debug-tools/releases
```

### Start Application

DebugTools uses the `Java Agent` technology to implement debugging, so you must ensure that the project has been started and completed when debugging.

### Attach Application

Click on the <img src="/docs/public/pluginIcon.svg" style="display: inline-block; width: 20px; height: 20px; vertical-align: middle;" /> toolbar on the right side of Idea to wake up the DebugTools window. Click <img src="/docs/public/icon/add.svg" alt="加号" style="display: inline-block; width: 20px; height: 20px; vertical-align: middle;" /> to get a list of applications that can be attached locally.

![tools_window](/docs/public/images/tools_window.png)

Select the application you want to debug and click the `Attach` button to attach the application.

![application_list.png](/docs/public/images/application_list.png)

After successfully attaching the app, DebugTools displays the attachment state.
- `L`: The logo is attached to the local application, `R` for remote application.
- `Connected`: The application has been successfully attached and the service has been successfully connected.
- `i.g.f.d.t.t.a.DebugToolsTestApplication`: application name.
  - Attach when specifying the application name is the specified `application name`.
  - If the application name is not specified, the Spring application `spring.application.name` configuration item.
  - When not specified, take the `Main-Class` in the startup jar.
  - Fetch `sun.java.command` in the startup command when not found.

![attach_status](/docs/public/images/attach_status.png)

### Invoke Method

Wake up the context menu on the method to be invoked, click `Quick Debug` to wake up the debug panel.

![idea_menu.png](/docs/public/images/idea_menu.png)

If we want to quickly call the `test` method of `TestService`

```java
package io.github.future0923.debug.tools.test.application.service;

import org.springframework.stereotype.Service;

@Service
public class TestService {

    public String test(String name, Integer age) {
        return "name = " + name + ", age = " + age;
    }
}
```

Enter the value of the parameter when calling the method

For example, `name = DebugTools`, `age = 18`, the DebugJson format will be automatically generated, Here we can pass the corresponding value in `content`.

![quick_debug](/docs/public/images/quick_debug.png)

Click the `Run` button to call the method.

### Show Result

After the call is successful, the run result (the return value of the method) will be displayed in the DebugTools window.

- **toString**: Shows the return value of the method after calling the ToString method.
- **json**: The return value of the method is displayed by Json.
- **debug**: Displays the method return value in the style of type Idea Debug.

![run_result](/docs/public/images/run_result.png)
![json_result](/docs/public/images/json_result.png)
![debug_result](/docs/public/images/debug_result.png)