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
https://download.debug-tools.cc/DebugToolsIdeaPlugin.zip
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

下面为本地方法，[附着远程](./attach-remote)点击查看。

<!--@include: ./attach-local-application.md-->

### 调用方法 {#invoke-method}

在要调用的方法上唤醒右键菜单，点击 `Quick Debug` 唤醒 [调试面板](./quick-debug)。

![idea_menu.png](/images/idea_menu.png){v-zoom}

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
如 `name=DebugTools`、`age=18`，DebugJson 格式会自动生成，详细[点击了解](./quick-debug#debugtools-json)。这里我们在 `content`中传入对应的值即可。
:::

![quick_debug](/images/quick_debug.png){v-zoom}

点击 `Run` 按钮调用方法。

### 展示结果 {#show-result}

调用成功后会在 DebugTools 窗口中展示 [运行结果](./run-result)（方法的返回值）。

- [toString](./run-result#toString): 展示方法返回值调用ToString方法后的结果。
- [json](./run-result#json): 将方法返回值通过Json的方式展示。
- [debug](./run-result#debug): 将方法返回值通过类型Idea Debug的样式展示。

![run_result](/images/run_result.png){v-zoom}

::: tip Debug 方式启动应用调用
如果应用通过 `Debug` 方式启动时，调用的目标方法有 `断点`，执行完断点后会返回执行结果。  
所以可以 `Debug` 方式启动应用，通过 `DebugTools` 快速调用方法，增加断点对目标方法进行调试。
:::