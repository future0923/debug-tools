# Quick Start {#quick-start}

## Install {#installation}

### Marketplace（recommend） {#marketplace}

1. Open `IDE Settings` and select `Plugins`.
2. Search for `DebugTools` in the `Marketplace` and click `install`.
3. Restart.

![marketplace](/images/marketplace.png){v-zoom}

### Manual Install {#manual-installation}

::: code-group

```text [Marketplace]
https://plugins.jetbrains.com/plugin/24463-debugtools
```

```text [Offline]
https://download.debug-tools.cc/DebugToolsIdeaPlugin.zip
```

```sh [Build manually]
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

```text [github]
https://github.com/future0923/debug-tools/releases
```

```text [gitee]
https://gitee.com/future94/debug-tools/releases
```

:::

::: details The manual build encountered a packaging failure problem

At present, the maven packaging needs to be built with the `java17+` version, and the grade packaging Idea plugins needs to be built with the `java17+` version.

:::

## Start Debug {#start-debugging}

### Start Application {#start-application}

DebugTools uses the `Java Agent` technology to implement debugging, so you must ensure that the project has been started and completed when debugging.

### Attach Application {#attach-application}

The following is the attachment local method，[attach remote](./attach-remote) click to view.

<!--@include: ./attach-local-application.md-->

### Invoke Method {#invoke-method}

Wake up the context menu on the method to be invoked, click `Quick Debug` to wake up the [debug panel](./quick-debug).

![idea_menu.png](/images/idea_menu.png){v-zoom}

::: details If we want to quickly call the `test` method of `TestService`

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

:::

::: details Enter the value of the parameter when calling the method

For example, `name = DebugTools`, `age = 18`, the DebugJson format will be automatically generated, click to [learn more](./quick-debug#debugtools-json). Here we can pass the corresponding value in `content`.

:::

![quick_debug](/images/quick_debug.png){v-zoom}

Click the `Run` button to call the method.

### Show Result {#show-result}

After the call is successful, the [run result](./run-result) (the return value of the method) will be displayed in the DebugTools window.

- [toString](./run-result#toString): Shows the return value of the method after calling the ToString method.
- [json](./run-result#json): The return value of the method is displayed by Json.
- [debug](./run-result#debug): Displays the method return value in the style of type Idea Debug.

![run_result](/images/run_result.png){v-zoom}

::: tip Debug start application call

If the application is started by the `Debug` way, the target method called has a `breakpoint`, and the execution result will be returned after the breakpoint is executed.  
So you can start the application in the `Debug` way, call the method quickly through the `DebugTools`, and add the breakpoint to debug the target method.
:::