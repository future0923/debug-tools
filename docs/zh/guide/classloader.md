# 类加载器 {#classloader}

## DebugTools类加载器 {#debugtools-classloader}

DebugTools 采用 Java Agent 来实现调试，**与附着应用完全隔离**，完全不会影响附着应用的正常运行。

::: details DebugTools 隔离逻辑
- 内部的类通过自定义类加载器 `DebugToolsClassloader` 实现与附着应用的类隔离。
- 依赖的第三方类库通过 [maven-shade-plugin](https://maven.apache.org/plugins/maven-shade-plugin/) 提供的 `relocations` 功能进行包重命名与附着应用的类隔离。如：`net.bytebuddy` 包重命名为 `io.github.future0923.debug.tools.dependencies.net.bytebuddy`。
:::

## 附着应用类加载器 {#attach-app-classloader}

DebugTools 需要调用附着应用的方法，但是自身运行与附着应用完全隔离开，所以如果想要调用附着应用方法，需要通过附着应用的类加载器获取到 Class 对象或者 实例对象后调用其方法。

::: details 附着应用类加载器获取逻辑
由于类加载器的层次结构，Java 应用程序运行时并没有公开的方法可以直接获取所有正在使用的类加载器。这里通过 agent 获取所有加载的类并获取到加载他们所有得类加载器。
:::

当唤醒调试面板时，DebugTools 会自动选择目标应用的默认类加载器。

![classloader](/images/classloader.png){v-zoom}

::: details 默认类加载器选中逻辑 {#default-classloader}
1. 获取启动 `Jar` 中的 `MAINIFEST.MF` 信息。 
2. 有 Start-Class 取 `Start-Class` 对应的类的类加载器。
3. 没有 Start-Class 则获取 `Main-Class` 对应的类的类加载器。
4. 没有取 `固定类` 的类加载器。目前固定类集合为 `org.slf4j.Logger`、`org.apache.log4j.Logger`、 `org.springframework.beans.factory.BeanFactory`。
:::

当要调用的方法不是默认的类加载器时，可以选择切换到其他类加载器。

![classloader_list](/images/classloader_list.png){v-zoom}

选择对应的加载器后，DebugTools 会通过选择的类加载器找到对应用 class 对象或者 实例对象的方法。结果也会展示对应的类加载器信息。

![classloader_result](/images/classloader_result.png){v-zoom}

::: tip 注意
- [xxl-job参数](./xxl-job) 设置尝试通过附着应用加载的 `xxl-job` 类对象设置的，目前采用的是`2.4.1`版本。
- [header参数](./header) 设置通过自定义的 `MockHttpServletRequest` 类实现，该类实现了 `tomcat` 的 `javax.servlet.http.HttpServletRequest` 接口，该相关类尝试通过附着应用获取。
:::

## Groovy类加载器 {#groovy-classloader}

先尝试通过 `DebugToolsClassLoader` 类加载加载，加载不到通过[附着应用默认类加载器](#default-classloader)加载