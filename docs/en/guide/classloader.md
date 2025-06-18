# ClassLoader {#classloader}

## DebugTools ClassLoader {#debugtools-classloader}

DebugTools uses Java Agent to implement debugging, which is **completely isolated from the attached application** and will not affect the normal operation of the attached application at all.

::: details DebugTools Isolation Logic
- Debug Tools internal classes are isolated from the classes of the attached application through a custom class loader `DebugToolsClassloader`.
- The dependent third-party libraries are renamed and isolated from the classes of the attached applications through the `relocations` function provided by [maven-shade-plugin](https://maven.apache.org/plugins/maven-shade-plugin/).
:::

## Attach Application ClassLoader {#attach-app-classloader}

DebugTools needs to call methods of the attached application, but its own operation is completely isolated from the attached application. Therefore, if you want to call methods of the attached application, you need to obtain the Class object or instance object through the class loader of the attached application and then call its method.

::: details Attach application class loader acquisition logic
Due to the hierarchical structure of class loaders, there is no public method to directly obtain all the class loaders in use when the Java application is running. Here, the agent is used to obtain all loaded classes and the class loaders that load them.
:::

When you invoke the debugger, DebugTools automatically selects the default class loader for the target application.

![classloader](/images/classloader.png){v-zoom}

::: details Default class loader selection logic {#default-classloader}
1. Get the `MAINIFEST.MF` information in the startup `Jar`. 
2. Start-Class takes the class loader of the class corresponding to `Start-Class`.
3. If there is no Start-Class, get the class loader of the class corresponding to `Main-Class`.
4. There is no class loader for `fixed classes`. Currently the fixed class set is `org.slf4j.Logger`, `org.apache.log4j.Logger`, `org.springframework.beans.factory.BeanFactory`.
:::

When a method is to be called that is not in the default class loader, you can choose to switch to another class loader.

![classloader_list](/images/classloader_list.png){v-zoom}

After selecting the corresponding loader, DebugTools will find the method of the application class object or instance object through the selected class loader. The result will also display the corresponding class loader information.

![classloader_result](/images/classloader_result.png){v-zoom}

::: tip
- The [xxl-job parameter](./xxl-job) settings attempt to be set by the `xxl-job` class object loaded by the attached application. Currently, the `2.4.1` version is used.
- The [header parameter](./header) is set using a custom `MockHttpServletRequest` class that implements the `javax.servlet.http.HttpServletRequest` interface of tomcat, which is attempted to be obtained by the attached application.
:::

## Groovy class loader {#groovy-classloader}

First try to load it through the `DebugToolsClassLoader` class loader. If it fails, try to load it through the [Attached Application Default Class Loader](#default-classloader)