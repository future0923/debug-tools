# 热重载 <Badge type="warning" text="beta" /> {#hot-reload} 

::: info
热重载实现复杂且需要兼容情况较多，如果这个项目帮你节省了开发时间，不妨点个 <a target="_blank" href="https://github.com/java-hot-deploy/debug-tools"><img src="https://img.shields.io/github/stars/java-hot-deploy/debug-tools?style=flat&logo=GitHub" style="display: inline-block; vertical-align: middle;" /></a>，你的认可会让更多人发现它，你的支持是我更新的动力。如果不生效麻烦提交 <a target="_blank" href="https://github.com/java-hot-deploy/debug-tools/issues"><img src="https://img.shields.io/github/issues-closed/java-hot-deploy/debug-tools?style=flat&logo=github" style="display: inline-block; vertical-align: middle;" /></a> 反馈一下。
:::

传统编写代码时，需要重启应用才能生效，而热重载可以在不重启应用下让编写的代码生效立刻，让开发者编写的代码改动瞬间生效，极大提升迭代效率。支持类(包括代理类)的属性和方法变动、Spring、Mybatis等主流框架。同时适配 jdk8、jdk11、jdk17、jdk21 等多个JDK版本。

## 1. 开启热重载

点击 <img src="/icon/hotswap.svg" style="display: inline-block; width: 20px; height: 20px; vertical-align: middle;" /> 图标变为 <img src="/icon/hotswap_on.svg" style="display: inline-block; width: 25px; height: 25px; vertical-align: middle;" /> 表示开启热重载，大飞机模式下启动项目热重载即可生效。

- 关闭状态

![hotswap_off](/images/hotswap_off.png){v-zoom}

- 开启状态

![hotswap_on](/images/hotswap_on.png){v-zoom}

::: tip
热重载需要特定的jdk才能生效，请先参考[JDK安装](install#jdk)完成JDK的初始化
:::

> 启动项目如果提示 `DCEVM is not installed` ，检查[JDK安装](install#jdk)是否正确。JDK8 检查命令 `java -XXaltjvm=dcevm -version` 是否能正常输出。
> ![dcevm_not_install.png](/images/dcevm_not_install.png){v-zoom}

## 2. 触发热重载

在大飞机的状态下启动应用，项目输出如下日志，并打印载入的热重载插件。

```text
DebugTools: 2025-01-07 16:41:07.909    INFO [main] i.g.f.d.t.h.c.HotswapAgent 44 : open hot reload unlimited runtime class redefinition.{3.3.0}
DebugTools: 2025-01-07 16:41:08.498    INFO [main] i.g.f.d.t.h.c.c.PluginRegistry 132 : Discovered plugins: [JdkPlugin, ClassInitPlugin, AnonymousClassPatch, WatchResources, HotSwapper, Proxy, Spring, MyBatis]
```

### 2.1 编译项目 {#compile-project}

应用启动后可以通过编译构建的方式触发热重载

![build_project.png](/images/build_project.png){v-zoom}

### 2.2 Debug热更新 {#compile-reload-file}

如果应用通过 `Debug` 的方式启动，则可以通过下面方式触发热重载，同时还可以更新断点信息。

- 通过右键菜单的 `Compile and Reload Modified Files` 按钮.
  
![compile_reload_file.png](/images/compile_reload_file.png){v-zoom}

- 文件主页面的 `Code changed` 按钮.

![compile_code_changed.png](/images/compile_code_changed.png){v-zoom}

### 2.3 热部署

<!--@include: ./parts/hot-deploy-muti-file.md-->

::: tip
热部署时idea可能有时无法获取到最新的断点信息，如果需要及时更新断点请使用[方式2](#compile-reload-file)
:::

### 2.4 单文件远程编译[install.md](install.md)

<!--@include: ./parts/hot-deploy-one-file.md-->

::: tip
热部署时idea可能有时无法获取到最新的断点信息，如果需要及时更新断点请使用[方式2](#compile-reload-file)
:::

### 2.5 单XML文件

变动的 `xml` 文件可以还可以通过右键菜单的 `Compile 'xxx.xml' to Target` 方式单独触发热重载.

![compile_xml.png](/images/compile_xml.png){v-zoom}

::: tip
- 实现方式就是将 xml 文件从 `src/main/resources` 移动到对应的 `target/` 下。
- 也可以通过[方式1](#compile-project)触发 xml 文件。
:::

## 3. 哪些情况可以热重载

### 3.1 普通的class文件

- 新增类文件
- 存在的类 **增加/修改** 类中的 **属性/方法/内部类**。
- 匿名内部类
- 枚举类

详细点击 [class文件热重载](hot-reload-class.md) 查看

### 3.2 代理类

- java JDK 代理类。
- Cglib 代理类。

详细点击 [代理类热重载](hot-reload-proxy.md) 查看

### 3.3 SpringBoot Bean

- Controller
- Service
- Component
- Repository

详细点击 [SpringBoot](hot-reload-springboot.md) 查看

### 3.4 MyBatis

- Mapper（新增/修改）
- Xml（新增/修改）

::: tip 注意

MyBatis 目前支持在 `Spring` 环境下，其他情况未知。

:::

详细点击 [MyBatis](hot-reload-mybatis.md) 查看

### 3.5 MyBatisPlus

- Entity（新增/修改）
- Mapper（新增/修改）
- Xml（新增/修改）

::: tip 注意

MyBatisPlus 目前支持在 `Spring` 环境下，其他情况未知。

:::

详细点击 [MyBatisPlus](hot-reload-mybatis-plus.md) 查看

### 3.6 dynamic-datasource

- `@DS`注解新增
- `@DS`注解修改

详细点击 [DynamicDatasource](dynamic-datasource.md) 查看

### 3.7 HuTool

支持 [hutool](https://hutool.cn) 热重载

- `ReflectUtil` 反射工具类
- `BeanDesc` bean描述符
- `JSONUtil` json工具类

### 3.8 Gson

支持 [Gson](https://github.com/google/gson) 工具包热重载

### 3.9 EasyExcel

支持 [EasyExcel](https://github.com/alibaba/easyexcel) 热重载

### 3.10 Jackson

支持 [Jackson](https://github.com/FasterXML/jackson-databind) 热重载

### 3.11 FastJson、FastJson2

支持 [FastJson](https://github.com/alibaba/fastjson)、[FastJson2](https://github.com/alibaba/fastjson2)热重载

### 3.12 hibernate-validator

支持 [hibernate-validator](https://github.com/hibernate/hibernate-validator) 工具包热重载

### 3.13 其他

其他情况热重载尝试一下，这里不一一举例了，如果不能生效麻烦提交个 [issues](https://github.com/java-hot-deploy/debug-tools/issues) 反馈一下。