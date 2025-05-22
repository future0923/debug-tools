# Hot reload <Badge type="warning" text="beta" /> {#hot-reload}

::: info
Hot reload is complex to implement and requires many compatibility scenarios. If this project helps you save development time, you might as well click <a target="_blank" href="https://github.com/future0923/debug-tools"><img src="https://img.shields.io/github/stars/future0923/debug-tools?style=flat&logo=GitHub" style="display: inline-block; vertical-align: middle;" /></a>. Your recognition will make more people discover it, and your support is my motivation to update. If it doesn't work, please submit <a target="_blank" href="https://github.com/future0923/debug-tools/issues"><img src="https://img.shields.io/github/issues-closed/future0923/debug-tools?style=flat&logo=github" style="display: inline-block; vertical-align: middle;" /></a> for feedback.
:::

When writing code traditionally, you need to restart the application to take effect, while hot reload can make the written code take effect immediately without restarting the application, so that the code changes written by the developer can take effect instantly, greatly improving the iteration efficiency. Supports changes to properties and methods of classes (including proxy classes), Spring, Mybatis and other mainstream frameworks. At the same time, it is compatible with multiple JDK versions such as jdk8, jdk11, jdk17, jdk21, etc.

## 1. Enable hot reload

Click <img src="/icon/hotswap.svg" style="display: inline-block; width: 20px; height: 20px; vertical-align: middle;" /> and the icon will change to <img src="/icon/hotswap_on.svg" style="display: inline-block; width: 25px; height: 25px; vertical-align: middle;" />, indicating that hot reload is enabled. Start the project hot reload in the big airplane mode to take effect.

- Off state

![hotswap_off](/images/hotswap_off.png){v-zoom}

- On state

![hotswap_on](/images/hotswap_on.png){v-zoom}

::: tip
Hot reload requires a specific JDK to take effect. Please refer to [JDK installation](install#jdk) to complete JDK initialization
:::

> If the startup project prompts `DCEVM is not installed`, check whether the [JDK installation](install#jdk) is correct. JDK8 Check whether the command `java -XXaltjvm=dcevm -version` can be output normally.
> ![dcevm_not_install.png](/images/dcevm_not_install.png){v-zoom}

## 2. Trigger hot reload

Start the application in the state of the big plane, the project outputs the following log, and prints the loaded hot reload plug-in.

```text
DebugTools: 2025-01-07 16:41:07.909 INFO [main] i.g.f.d.t.h.c.HotswapAgent 44 : open hot reload unlimited runtime class redefinition.{3.3.0}
DebugTools: 2025-01-07 16:41:08.498 INFO [main] i.g.f.d.t.h.c.c.PluginRegistry 132: Discovered plugins: [JdkPlugin, ClassInitPlugin, AnonymousClassPatch, WatchResources, HotSwapper, Proxy, Spring, MyBatis]
```

### 2.1 Compile project {#compile-project}

After the application is started, hot reload can be triggered by compiling and building

![build_project.png](/images/build_project.png){v-zoom}

### 2.2 Debug hot update {#compile-reload-file}

If the application is started by `Debug`, hot reload can be triggered by the following method, and breakpoint information can also be updated.

- Use the `Compile and Reload Modified Files` button in the right-click menu.

![compile_reload_file.png](/images/compile_reload_file.png){v-zoom}

- Use the `Code changed` button on the main file page.

![compile_code_changed.png](/images/compile_code_changed.png){v-zoom}

### 2.3 Hot deployment

<!--@include: ./parts/hot-deploy-muti-file.md-->

::: tip
During hot deployment, idea may sometimes fail to obtain the latest breakpoint information. If you need to update the breakpoint in time, please use [method 2](#compile-reload-file)
:::

### 2.4 Single file remote compilation

<!--@include: ./parts/hot-deploy-one-file.md-->

::: tip
During hot deployment, idea may sometimes fail to obtain the latest breakpoint information. If you need to update the breakpoint in time, please use [Method 2](#compile-reload-file)
:::

### 2.5 Single XML file

The changed `xml` file can also trigger hot reload separately through the `Compile 'xxx.xml' to Target` method in the right-click menu.

![compile_xml.png](/images/compile_xml.png){v-zoom}

::: tip
- The implementation method is to move the xml file from `src/main/resources` to the corresponding `target/`.
- You can also trigger the xml file through [Method 1](#compile-project).
  :::

## 3. In which cases can hot reload be performed

### 3.1 Ordinary class files

- Add new class files
- **Add/modify** **properties/methods/inner classes** in existing classes.
- Anonymous inner class
- Enumeration class

Click [class file hot reload](hot-reload-class.md) for details

### 3.2 Proxy class

- Java JDK proxy class.
- Cglib proxy class.

Click [proxy class hot reload](hot-reload-proxy.md) for details

### 3.3 SpringBoot Bean

- Controller
- Service
- Component
- Repository

Click [SpringBoot](hot-reload-springboot.md) for details

### 3.4 MyBatis

- Mapper (new/modified)

- Xml (new/modified)

::: tip Note

MyBatis currently supports `Spring` environment, other situations are unknown.

:::

Click [MyBatis](hot-reload-mybatis.md) for details

### 3.5 MyBatisPlus

- Entity (new/modified)

- Mapper (new/modified)

- Xml (new/modified)

::: tip

MyBatisPlus currently supports `Spring` environment, other situations are unknown.

:::

Click [MyBatisPlus](hot-reload-mybatis-plus.md) for details

### 3.6 dynamic-datasource

- `@DS` annotation added
- `@DS` annotation modified

Click [DynamicDatasource](dynamic-datasource.md) for details

### 3.7 HuTool

Support [hutool](https://hutool.cn) hot reload

- `ReflectUtil` reflection tool class
- `BeanDesc` bean descriptor
- `JSONUtil` json tool class

### 3.8 Gson

Support [Gson](https://github.com/google/gson) toolkit hot reload

### 3.9 EasyExcel

Support [EasyExcel](https://github.com/alibaba/easyexcel) hot reload

### 3.10 Jackson

Support [Jackson](https://github.com/FasterXML/jackson-databind) hot reload

### 3.11 FastJson、FastJson2

Support [FastJson](https://github.com/alibaba/fastjson)、[FastJson2](https://github.com/alibaba/fastjson2) hot reload

### 3.12 Others

Hot reload can also be used in other situations. I won’t give examples here. If it doesn’t work, please submit an [issue](https://github.com/future0923/debug-tools/issues) to give feedback.