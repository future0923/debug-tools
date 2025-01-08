# Attach Local Application {#attach-local}

## Start Debug {#start-debugging}

### Start Application {#start-application}

DebugTools uses the `Java Agent` technology to implement debugging, so you must ensure that the project has been started and completed when debugging.

### Attach Application {#attach-application}

The following is the attachment local method，[attach remote](./attach-remote) click to view.

Click on the <img src="/pluginIcon.svg" style="display: inline-block; width: 20px; height: 20px; vertical-align: middle;" /> toolbar on the right side of Idea to wake up the DebugTools window. Click <img src="/icon/add.svg" alt="加号" style="display: inline-block; width: 20px; height: 20px; vertical-align: middle;" /> to get a list of applications that can be attached locally.

![tools_window](/images/tools_window.png){v-zoom}

Select the application you want to debug and click the `Attach` button to attach the application.

![application_list.png](/images/application_list.png){v-zoom}

After successfully attaching the app, DebugTools displays the attachment state.
- `L`: The logo is attached to the local application, `R` for remote application.
- `Connected`: The application has been successfully attached and the service has been successfully connected.
- `i.g.f.d.t.t.a.DebugToolsTestApplication`: application name.
    - Attach when specifying the application name is the specified `application name`.
    - If the application name is not specified, the Spring application `spring.application.name` configuration item.
    - When not specified, take the `Main-Class` in the startup jar.
    - Fetch `sun.java.command` in the startup command when not found.

![attach_status](/images/attach_status.png){v-zoom}

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