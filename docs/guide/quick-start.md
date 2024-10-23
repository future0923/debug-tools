# 快速开始 {#quick-start}

## 安装 {#installation}

### Marketplace 商店（推荐） {#marketplace}

1. 打开 `IDE Settings` 并选择 `Plugins`
2. 在 `Marketplace` 搜索 `DebugTools` 并点击 `install`
3. 重启应用

![marketplace](/images/marketplace.png){v-zoom}

### 自行安装 {#manual-installation}
::: code-group

```text [商店网址]
https://plugins.jetbrains.com/plugin/24463-debugtools
```

```text [离线安装]
https://download.debug-tools.cc/
```

```sh [手动构建]
git clone https://github.com/future0923/debug-tools.git
cd debug-tools
mvn clean install -T 2C -Dmaven.test.skip=true
# dist目录下
# debug-tools-boot.jar 远程agent包
cd debug-tools-idea
./gradlew clean buildPlugin
# dist目录下
# DebugTools-{version}.zip IDEA插件包
```

```text [github]
https://github.com/future0923/debug-tools/releases
```

```text [gitee]
https://gitee.com/future94/debug-tools/releases
```

:::

::: details 手动构建遇到了打包失败问题

目前maven打包需要使用 `java1.8` 版本构建，grade打包Idea插件时需要使用`java17+`版本构建。

:::

## 开始调试 {#start-debugging}

### 启动应用 {#start-application}

DebugTools 通过 `Java Agent` 技术来实现调试，所以调试时必须保证项目已经启动完成。

### 附着应用 {#attach-application}

点击 Idea 右侧的 <img src="/pluginIcon.svg" style="display: inline-block; width: 20px; height: 20px; vertical-align: middle;" /> 工具栏唤醒 DebugTools 的窗口，点击 <img src="/icon/add.svg" alt="加号" style="display: inline-block; width: 20px; height: 20px; vertical-align: middle;" /> 获取本地可以附着的应用列表。

![tools_window](/images/tools_window.png){v-zoom}

选择要调试的应用，点击 `Attach` 按钮进行应用附着。

![application_list.png](/images/application_list.png){v-zoom}

成功附着应用后，DebugTools 会在显示附着状态。
- `L`: 标识附着的是本地应用，`R`代表是远程应用。
- `Connected`: 应用已经附着成功并连接服务成功。
- `i.g.f.d.t.t.a.DebugToolsTestApplication`: 应用名称。
  - 附着时指定应用名时为`指定的应用名`。
  - 未指定应用名时如果是 Spring 应用取 `spring.application.name` 配置项。
  - 未指定时取启动时jar中的 `Main-Class`。
  - 未找到时取启动命令中的 `sun.java.command`。

![attach_status](/images/attach_status.png){v-zoom}

### 调用方法 {#invoke-method}

在要调用的方法上唤醒右键菜单，点击 `Quick Debug` 唤醒调试面板。

::: details 如我们要快速调用 `TestService` 的 `test` 方法

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

::: details 输入调用方法时参数的值
如 `name=DebugTools`、`age=18`，DebugJson 格式会自动生成，详细点击了解。这里我们在 `content`中传入对应的值即可。
:::

![quick_debug](/images/quick_debug.png){v-zoom}

点击 `Run` 按钮调用方法。

### 展示结果 {#show-result}

调用成功后会在 DebugTools 窗口中展示结果（方法的返回值）。

- `toString`: 展示方法返回值调用ToString方法后的结果。
- `json`: 将方法返回值通过Json的方式展示。
- `debug`: 将方法返回值通过类型Idea Debug的样式展示。

![run_result](/images/run_result.png){v-zoom}

::: tip Debug 方式启动应用调用
如果应用通过 `Debug` 方式启动时，调用的目标方法有 `断点`，执行完断点后会返回执行结果。  
所以可以 `Debug` 方式启动应用，通过 `DebugTools` 快速调用方法，增加断点对目标方法进行调试。
:::