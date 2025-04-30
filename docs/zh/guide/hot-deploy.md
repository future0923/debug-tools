# 热部署 <Badge type="warning" text="beta" /> {#hot-deploy}

::: info
热部署实现复杂且需要兼容情况较多，如果这个项目帮你节省了开发时间，不妨点个 <a target="_blank" href="https://github.com/future0923/debug-tools"><img src="https://img.shields.io/github/stars/future0923/debug-tools?style=flat&logo=GitHub" style="display: inline-block; vertical-align: middle;" /></a>，你的认可会让更多人发现它，你的支持是我更新的动力。如果不生效麻烦提交 <a target="_blank" href="https://github.com/future0923/debug-tools/issues"><img src="https://img.shields.io/github/issues-closed/future0923/debug-tools?style=flat&logo=github" style="display: inline-block; vertical-align: middle;" /></a> 反馈一下。
:::

## 1. 什么是热部署

传统的部署流程一般为 `提交代码->拉取代码->打包->部署->重启项目` 后才能让编写的代码生效。

热部署可以跳过这繁琐的流程，开发者修改代码后无需手动触发打包或重启服务，应用即可`秒级`加载新逻辑并运行，极大缩短反馈周期。

热部署基于 [热重载](hot-reload) 实现，热重载仅支持本地环境，热部署则让远程应用也可以热重载。

## 2. 开启热部署

::: tip
热部署需要特定的jdk才能生效，请先参考[JDK安装](quick-start#jdk)完成JDK的初始化。
:::

### 2.1 添加JVM参数

【Java 8】 启动时添加如下的 JVM 参数。

```shell
-XXaltjvm=dcevm -javaagent:${agentPath}/debug-tools-agent.jar=${conf}
```

【Java 11】启动时添加如下的 JVM 参数。

```shell
-javaagent:${agentPath}/debug-tools-agent.jar=${conf} --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/jdk.internal.loader=ALL-UNNAMED --add-opens java.base/java.io=ALL-UNNAMED --add-opens java.desktop/java.beans=ALL-UNNAMED --add-opens java.desktop/com.sun.beans=ALL-UNNAMED --add-opens java.desktop/com.sun.beans.introspect=ALL-UNNAMED --add-opens java.desktop/com.sun.beans.util=ALL-UNNAMED --add-opens java.base/sun.security.action=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED --add-opens java.base/java.net=ALL-UNNAMED
```

【Java 17/21】启动时添加如下的 JVM 参数。

```shell
-XX:+AllowEnhancedClassRedefinition -javaagent:${agentPath}/debug-tools-agent.jar=${conf} --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/jdk.internal.loader=ALL-UNNAMED --add-opens java.base/java.io=ALL-UNNAMED --add-opens java.desktop/java.beans=ALL-UNNAMED --add-opens java.desktop/com.sun.beans=ALL-UNNAMED --add-opens java.desktop/com.sun.beans.introspect=ALL-UNNAMED --add-opens java.desktop/com.sun.beans.util=ALL-UNNAMED --add-opens java.base/sun.security.action=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED --add-opens java.base/java.net=ALL-UNNAMED
```

- `agentPath` 为 debug-tools-agent.jar 的路径
- `conf` 为传入 agent 的配置信息，格式为 `key1=value1,key2=value2`

### 2.2 可选配置

| 可选key项             | 含义                                             | value取值                 | 取值示例                                               |
|--------------------|------------------------------------------------|-------------------------|----------------------------------------------------|
| hotswap            | 是否开启热重载/热部署                                    | true:开启 <br /> false:关闭 | ture                                               |
| server             | 是否启动Server给客户端连接                               | true:开启 <br /> false:关闭 | ture                                               |
| tcpPort            | 监听的TCP端口 (server=true时才生效)                     | 可用端口                    | 12345                                              |
| httpPort           | 监听的HTTP端口  (server=true时才生效)                   | 可用端口                    | 22222                                              |
| printSql           | 是否打印执行的SQL语句                                   | true:开启 <br /> false:关闭 | ture                                               |
| propertiesFilePath | 外部配置文件路径                                       | 配置文件地址                  | /etc/debug-tools/conf/debug-tools-agent.properties |

propertiesFilePath 配置

| key                         | 含义                                                                                                    | 默认值                              |
|-----------------------------|-------------------------------------------------------------------------------------------------------|----------------------------------|
| hotswap                     | 是否开启热重载/热部署 (同上面)                                                                                     | true                             |
| server                      | 是否启动Server给客户端连接 (同上面)                                                                                | true                             |
| tcpPort                     | 监听的TCP端口 (server=true时才生效) (同上面)                                                                      | 默认从 12345 开始递增寻找可用端口             |
| httpPort                    | 监听的HTTP端口  (server=true时才生效) (同上面)                                                                    | 默认从 22222 开始递增寻找可用端口             |
| lombokJarPath               | lombok.jar 路径，远程动态编译时如果使用了lombok需要指定才能编译 (同上面)                                                        | -                                |
| printSql                    | 是否打印执行的SQL语句                                                                                          | false                            |
| includedClassLoaderPatterns | 要初始化热重载/热部署的ClassLoader。 与 excludedClassLoaderPatterns 只能同时配置一个                                       | -                                |
| excludedClassLoaderPatterns | 要排除初始化重载/热部署的ClassLoader。 与 includedClassLoaderPatterns 只能同时配置一个                                      | -                                |
| pluginPackages              | 扫描其它路径上编写的插件，多个路径逗号分隔                                                                                 | -                                |
| extraClasspath              | MacOS/Linux 加载扩展的class文件路径                                                                            | /var/tmp/debug-tools/classes     |
| extraClasspathWin           | Windows 加载扩展的class文件路径                                                                                | c:/var/tmp/debug-tools/classes   |
| watchResources              | MacOS/Linux 监听资源路径                                                                                    | /var/tmp/debug-tools/resources   |
| watchResourcesWin           | Windows 监听资源路径                                                                                        | c:/var/tmp/debug-tools/resources |
| spring.basePackagePrefix    | Spring基础package前缀，多个路径逗号分隔                                                                            | -                                |
| disabledPlugins             | 禁用的插件，多个逗号分隔                                                                                          | -                                |
| autoHotswap                 | 是否自动热重载。在 ClassLoader 的 resources 路径下监视更改的类文件后在运行中的应用程序中重新加载类定义。它使用Java Instrumentation API来重新加载类字节码。 | false                            |
| autoHotswap.port            | JPDA连接端口，监听更改文件后进行热重载，启动时需要指定JPDA端口。                                                                  | -                                |

## 3. 连接远程服务

<!--@include: ./parts/connect-remote.md-->

## 4. 使用热重载

### 4.1 多个文件

<!--@include: ./parts/hot-deploy-muti-file.md-->

### 4.2 单文件远程编译

<!--@include: ./parts/hot-deploy-one-file.md-->
