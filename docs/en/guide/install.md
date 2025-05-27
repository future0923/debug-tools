# Installation instructions {#install}

## 1. Install the plugin {#install-plugin}

### 1.1 Marketplace store (recommended) {#marketplace}

1. Open `IDE Settings` and select `Plugins`

2. Search `DebugTools` in `Marketplace` and click `install`

3. Restart the application

![marketplace](/images/marketplace.png){v-zoom}

### 1.2 Self-installation {#manual-installation}

::: code-group

```text [Store URL]
https://plugins.jetbrains.com/plugin/24463-debugtools
```

```text [Offline installation]
https://download.debug-tools.cc/DebugToolsIdeaPlugin.zip
```

```sh [Manual build]
git clone https://github.com/future0923/debug-tools.git
cd debug-tools
# Maven packaging needs to use `java17+` version to build
mvn clean install -T 2C -Dmaven.test.skip=true
# In the dist directory
# debug-tools-boot.jar remote agent package
cd debug-tools-idea
# When grade packages the Idea plug-in, it needs to use the `java17+` version to build
./gradlew clean buildPlugin
# In the dist directory
# DebugTools-{version}.zip IDEA plug-in package
```

```text [github]
https://github.com/future0923/debug-tools/releases
```

```text [gitee]
https://gitee.com/future94/debug-tools/releases
```

:::

## 2. Install JDK {#jdk}

Specific JDK support is only required when using the [hot-deploy](hot-deploy) and [hot-reload](hot-reload) functions.

### 2.1 JDK 8 {#jdk8}

#### 2.1.1 Use the packaged JDK package directly

::: details Download from github

[https://github.com/future0923/debug-tools/releases/tag/dcevm-jdk-1.8.0_181](https://github.com/future0923/debug-tools/releases/tag/dcevm-jdk-1.8.0_181)

- [windows-jdk-8u181.zip](https://github.com/future0923/debug-tools/releases/download/dcevm-jdk-1.8.0_181/linux-x64-jdk-8u181.tar.gz)
- [mac-jdk-8u181.zip](https://github.com/future0923/debug-tools/releases/download/dcevm-jdk-1.8.0_181/mac-jdk-8u181.zip)
- [linux-x64-jdk-8u181.tar.gz](https://github.com/future0923/debug-tools/releases/download/dcevm-jdk-1.8.0_181/windows-jdk-8u181.zip)

:::

::: details DebugTools official website download

- [windows-jdk-8u181.zip](https://download.debug-tools.cc/dcevm-jdk-1.8.0_181/linux-x64-jdk-8u181.tar.gz)
- [mac-jdk-8u181.zip](https://download.debug-tools.cc/dcevm-jdk-1.8.0_181/mac-jdk-8u181.zip)
- [linux-x64-jdk-8u181.tar.gz](https://download.debug-tools.cc/dcevm-jdk-1.8.0_181/windows-jdk-8u181.zip)

:::

#### 2.1.2 Self-installation

::: details Windows/Mac OS

Download the corresponding version of the .jar file. <span style="color: red;">Currently only the following versions of JDK are supported, please select the corresponding version. </span>

| java version | download by debug tools                                                                                | [download by github](https://github.com/future0923/debug-tools/releases/tag/dcevm-installer)                                       |
|--------------|--------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------|
| 1.8.0_181    | [DCEVM-8u181-installer.jar](https://download.debug-tools.cc/dcevm-installer/DCEVM-8u181-installer.jar) | [DCEVM-8u181-installer.jar](https://github.com/future0923/debug-tools/releases/download/dcevm-installer/DCEVM-8u181-installer.jar) |
| 1.8.0_172    | [DCEVM-8u172-installer.jar](https://download.debug-tools.cc/dcevm-installer/DCEVM-8u172-installer.jar) | [DCEVM-8u172-installer.jar](https://github.com/future0923/debug-tools/releases/download/dcevm-installer/DCEVM-8u172-installer.jar) |
| 1.8.0_152    | [DCEVM-8u152-installer.jar](https://download.debug-tools.cc/dcevm-installer/DCEVM-8u152-installer.jar) | [DCEVM-8u152-installer.jar](https://github.com/future0923/debug-tools/releases/download/dcevm-installer/DCEVM-8u152-installer.jar) |
| 1.8.0_144    | [DCEVM-8u144-installer.jar](https://download.debug-tools.cc/dcevm-installer/DCEVM-8u144-installer.jar) | [DCEVM-8u144-installer.jar](https://github.com/future0923/debug-tools/releases/download/dcevm-installer/DCEVM-8u144-installer.jar) |
| 1.8.0_112    | [DCEVM-8u112-installer.jar](https://download.debug-tools.cc/dcevm-installer/DCEVM-8u112-installer.jar) | [DCEVM-8u112-installer.jar](https://github.com/future0923/debug-tools/releases/download/dcevm-installer/DCEVM-8u112-installer.jar) |
| 1.8.0_92     | [DCEVM-8u92-installer.jar](https://download.debug-tools.cc/dcevm-installer/DCEVM-8u92-installer.jar)   | [DCEVM-8u92-installer.jar](https://github.com/future0923/debug-tools/releases/download/dcevm-installer/DCEVM-8u92-installer.jar)   |
| 1.8.0_74     | [DCEVM-8u74-installer.jar](https://download.debug-tools.cc/dcevm-installer/DCEVM-8u74-installer.jar)   | [DCEVM-8u74-installer.jar](https://github.com/future0923/debug-tools/releases/download/dcevm-installer/DCEVM-8u74-installer.jar)   |
| 1.8.0_66     | [DCEVM-8u66-installer.jar](https://download.debug-tools.cc/dcevm-installer/DCEVM-8u66-installer.jar)   | [DCEVM-8u66-installer.jar](https://github.com/future0923/debug-tools/releases/download/dcevm-installer/DCEVM-8u66-installer.jar)   |
| 1.8.0_51     | [DCEVM-8u51-installer.jar](https://download.debug-tools.cc/dcevm-installer/DCEVM-8u51-installer.jar)   | [DCEVM-8u51-installer.jar](https://github.com/future0923/debug-tools/releases/download/dcevm-installer/DCEVM-8u51-installer.jar)   |
| 1.8.0_45     | [DCEVM-8u45-installer.jar](https://download.debug-tools.cc/dcevm-installer/DCEVM-8u45-installer.jar)   | [DCEVM-8u45-installer.jar](https://github.com/future0923/debug-tools/releases/download/dcevm-installer/DCEVM-8u45-installer.jar)   |

Run the corresponding `java -jar DCEVM-8uXX-installer.jar` file, find the corresponding version, and click the `Install DCEVM as altjvm` button.

![dcevm-installer.png](/images/dcevm-installer.png){v-zoom}

:::

::: details Linux

If you enter `java -XXaltjvm=dcevm -version`, you will get the following prompt

```text
Error: missing `dcevm' JVM at `/home/java/jdk1.8.0_291/jre/lib/amd64/dcevm/libjvm.so'.
Please install or use the JRE or JDK that contains these missing components.
```

Download the corresponding version file and rename it to `libjvm.so` and put it in the directory extracted above.

| java version | download by debug tools                                             | [download by github](https://github.com/future0923/debug-tools/releases/tag/libjvm.so)             |
|--------------|---------------------------------------------------------------------|----------------------------------------------------------------------------------------------------|
| >= 1.8.0_181 | [libjvm181.so](https://download.debug-tools.cc/libjvm/libjvm181.so) | [libjvm181.so](https://github.com/future0923/debug-tools/releases/download/libjvm.so/libjvm181.so) |
| 1.8.0_172    | [libjvm172.so](https://download.debug-tools.cc/libjvm/libjvm172.so) | [libjvm172.so](https://github.com/future0923/debug-tools/releases/download/libjvm.so/libjvm172.so) |
| 1.8.0_152    | [libjvm152.so](https://download.debug-tools.cc/libjvm/libjvm152.so) | [libjvm152.so](https://github.com/future0923/debug-tools/releases/download/libjvm.so/libjvm152.so) |
| 1.8.0_144    | [libjvm144.so](https://download.debug-tools.cc/libjvm/libjvm144.so) | [libjvm144.so](https://github.com/future0923/debug-tools/releases/download/libjvm.so/libjvm144.so) |
| 1.8.0_112    | [libjvm112.so](https://download.debug-tools.cc/libjvm/libjvm112.so) | [libjvm112.so](https://github.com/future0923/debug-tools/releases/download/libjvm.so/libjvm112.so) |
| 1.8.0_92     | [libjvm92.so](https://download.debug-tools.cc/libjvm/libjvm92.so)   | [libjvm92.so](https://github.com/future0923/debug-tools/releases/download/libjvm.so/libjvm92.so)   |
| 1.8.0_74     | [libjvm74.so](https://download.debug-tools.cc/libjvm/libjvm74.so)   | [libjvm74.so](https://github.com/future0923/debug-tools/releases/download/libjvm.so/libjvm74.so)   |
| <= 1.8.0_66  | [libjvm66.so](https://download.debug-tools.cc/libjvm/libjvm66.so)   | [libjvm66.so](https://github.com/future0923/debug-tools/releases/download/libjvm.so/libjvm66.so)   |

:::

### 2.2 JDK 11 {#jdk11}

::: details trava-jdk-11-dcevm

Use [trava-jdk-11-dcevm](https://github.com/TravaOpenJDK/trava-jdk-11-dcevm/releases) JDK to support hot deployment/hot reload.

- MacOS latest version [Dcevm-11.0.15+1](https://github.com/TravaOpenJDK/trava-jdk-11-dcevm/releases/download/dcevm-11.0.15%2B1/Openjdk11u-dcevm-mac-x64.tar.gz) Download address
- Windows latest version [Dcevm-11.0.15+1](https://github.com/TravaOpenJDK/trava-jdk-11-dcevm/releases/download/dcevm-11.0.15%2B1/Openjdk11u-dcevm-windows-x64.zip) Download address
- Windows latest version [Dcevm-11.0.15+1](https://github.com/TravaOpenJDK/trava-jdk-11-dcevm/releases/download/dcevm-11.0.15%2B1/Openjdk11u-dcevm-linux-x64.tar.gz) Download address
- Other versions can be downloaded from [https://github.com/TravaOpenJDK/trava-jdk-11-dcevm/releases](https://github.com/TravaOpenJDK/trava-jdk-11-dcevm/releases).

:::

::: details JetBrainsRuntime

Using [JetBrainsRuntime](https://github.com/JetBrains/JetBrainsRuntime/tree/jbr11) JDK can support hot deployment/hot reload.

<span style = "color: red;" > Please download the JDK with dcevm. </span>

It is recommended to use the latest version [11_0_15-b2043.56](https://github.com/JetBrains/JetBrainsRuntime/releases/tag/jbr11_0_15b2043.56)

:::

### 2.3 JDK 17/21 {#jdk17-21}

Using [JetBrainsRuntime](https://github.com/JetBrains/JetBrainsRuntime) JDK can support hot deployment/hot reload. 

<span style = "color: red;" > Please download the JDK with dcevm. </span>

::: info

If the Apple system prompts that the JDK is damaged or the developer cannot be verified after downloading the JDK, you can enter `sudo xattr -r -d com.apple.quarantine /$jdkPath`, **$jdkPath** is your jdk directory

:::

## 3. Download debug-tools-agent.jar

`debug-tools-agent.jar` is a Java Agent package, which is the core of DebugTools functions. DebugTools functions are all implemented through Java Agent.

Only [hot-deploy](hot-deploy) requires this jar, and the others are already built into the Idea plugin.

::: code-group

```text [URL]
https://download.debug-tools.cc/debug-tools-agent.jar
```

```sh [Manual build]
git clone https://github.com/future0923/debug-tools.git
cd debug-tools
# Currently, maven packaging needs to be built using the `java17+` version.
mvn clean install -T 2C -Dmaven.test.skip=true
# In the dist directory
# debug-tools-agent.jar remote agent package
```

```text [github]
https://github.com/future0923/debug-tools/releases
```

```text [gitee]
https://gitee.com/future94/debug-tools/releases
```

:::

## 4. Download debug-tools-boot.jar

Use `debug-tools-boot.jar` to dynamically start the debug tools server service after the application starts.

::: code-group

```text [URL]
https://download.debug-tools.cc/debug-tools-boot.jar
```

```sh [Manual build]
git clone https://github.com/future0923/debug-tools.git
cd debug-tools
# Currently, maven packaging needs to be built using the `java17+` version.
mvn clean install -T 2C -Dmaven.test.skip=true
# In the dist directory
# debug-tools-boot.jar remote agent package
```

```text [github]
https://github.com/future0923/debug-tools/releases
```

```text [gitee]
https://gitee.com/future94/debug-tools/releases
```

:::