# 快速开始 {#quick-start}

## 安装 {#installation}

### Marketplace 商店（推荐） {#marketplace}

1. 打开 `IDE Settings` 并选择 `Plugins`
2. 在 `Marketplace` 搜索 `DebugTools` 并点击 `install`
3. 重启应用

![marketplace](/images/marketplace.png){v-zoom}

::: tip 2025.1版本的 Intellij Idea
由于 jetbrains Marketplace 插件验证器的BUG，3.4.4 版本无法安装，请使用下面的 [离线安装](https://download.debug-tools.cc/DebugToolsIdeaPlugin.zip) 或者 [Github](https://github.com/future0923/debug-tools/releases) 或者 [Gitee](https://gitee.com/future94/debug-tools/releases) 安装。
:::

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

目前maven打包需要使用 `java17+` 版本构建，grade打包Idea插件时需要使用`java17+`版本构建。

:::