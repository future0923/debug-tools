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
