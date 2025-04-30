# Hot Deploy <Badge type="warning" text="beta" /> {#hot-deploy}

::: info
Hot deployment is complex and requires a lot of compatibility. If this project helps you save development time, you might as well click <a target="_blank" href="https://github.com/future0923/debug-tools"><img src="https://img.shields.io/github/stars/future0923/debug-tools?style=flat&logo=GitHub" style="display: inline-block; vertical-align: middle;" /></a>. Your recognition will make more people discover it, and your support is my motivation to update. If it doesn't work, please submit <a target="_blank" href="https://github.com/future0923/debug-tools/issues"><img src="https://img.shields.io/github/issues-closed/future0923/debug-tools?style=flat&logo=github" style="display: inline-block; vertical-align: middle;" /></a> to give feedback.
:::

## 1. What is hot deployment

The traditional deployment process is generally `submit code->pull code->package->deploy->restart project` before the written code can take effect.

Hot deployment can skip this tedious process. After the developer modifies the code, there is no need to manually trigger packaging or restart the service. The application can load the new logic and run in seconds, greatly shortening the feedback cycle.

Hot deployment is based on [hot reload](hot-reload). Hot reload only supports local environment, while hot deployment allows remote applications to be hot reloaded.

## 2. Enable hot deployment

::: tip
Hot deployment requires a specific JDK to take effect. Please refer to [JDK Installation](install#jdk) to complete JDK initialization.
:::

### 2.1 Add JVM parameters

[Java 8] Add the following JVM parameters when starting.

```shell
-XXaltjvm=dcevm -javaagent:${agentPath}/debug-tools-agent.jar=${conf}
```

[Java 11] Add the following JVM parameters when starting.

```shell
-javaagent:${agentPath}/debug-tools-agent.jar=${conf} --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/jdk.internal.loader=ALL-UNNAMED --add-opens java.base/java.io=ALL-UNNAMED --add-opens java.desktop/java.beans=ALL-UNNAMED --add-opens java.desktop/com.sun.beans=ALL-UNNAMED --add-opens java.desktop/com.sun.beans.introspect=ALL-UNNAMED --add-opens java.desktop/com.sun.beans.util=ALL-UNNAMED --add-opens java.base/sun.security.action=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED --add-opens java.base/java.net=ALL-UNNAMED
```

[Java 17/21] Add the following JVM parameters when starting.

```shell
-XX:+AllowEnhancedClassRedefinition -javaagent:${agentPath}/debug-tools-agent.jar=${conf} --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/jdk.internal.loader=ALL-UNNAMED --add-opens java.base/java.io=ALL-UNNAMED --add-opens java.desktop/java.beans=ALL-UNNAMED --add-opens java.desktop/com.sun.beans=ALL-UNNAMED --add-opens java.desktop/com.sun.beans.introspect=ALL-UNNAMED --add-opens java.desktop/com.sun.beans.util=ALL-UNNAMED --add-opens java.base/sun.security.action=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED --add-opens java.base/java.net=ALL-UNNAMED
```

- `agentPath` is the path of debug-tools-agent.jar
- `conf` is the configuration information passed to the agent, in the format of `key1=value1,key2=value2`

### 2.2 Optional configuration

| Optional key       | Meaning                                              | Value                              | Example                                            |
|--------------------|------------------------------------------------------|------------------------------------|----------------------------------------------------|
| hotswap            | Whether to enable hot reload/hot deployment          | true: Enable <br /> false: Disable | true                                               |
| server             | Whether to start the server for client connections   | true: Enable <br /> false: Disable | true                                               |
| tcpPort            | TCP port to listen to (valid only when server=true)  | Available ports                    | 12345                                              |
| httpPort           | HTTP port to listen to (valid only when server=true) | Available ports                    | 22222                                              |
| printSql           | Whether to print the executed SQL statement          | true: Enable <br /> false: Disable | true                                               |
| propertiesFilePath | External configuration file path                     | Configuration file address         | /etc/debug-tools/conf/debug-tools-agent.properties |

propertiesFilePath configuration

| key                         | meaning                                                                                                                                                                                                                            | default value                                     |
|-----------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------|
| hotswap                     | whether to enable hot reload/hot deployment (same as above)                                                                                                                                                                        | true                                              |
| server                      | whether to start the server for client connection (same as above)                                                                                                                                                                  | true                                              |
| tcpPort                     | TCP port to listen on (valid only when server=true) (same as above)                                                                                                                                                                | default, start from 12345 to find available ports |
| httpPort                    | HTTP port to listen on (valid only when server=true) (same as above)                                                                                                                                                               | default, start from 22222 to find available ports |
| lombokJarPath               | lombok.jar path, if lombok is used during remote dynamic compilation, it needs to be specified to compile (same as above)                                                                                                          | -                                                 |
| printSql                    | whether to print the executed SQL statement                                                                                                                                                                                        | false                                             |
| includedClassLoaderPatterns | ClassLoader to initialize hot reload/hot deployment. Only one of the excludedClassLoaderPatterns can be configured at the same time                                                                                                | -                                                 |
| excludedClassLoaderPatterns | The ClassLoader to be excluded from initialization reloading/hot deployment. Only one can be configured at the same time with includedClassLoaderPatterns                                                                          | -                                                 |
| pluginPackages              | Scan plugins written on other paths, multiple paths are separated by commas                                                                                                                                                        | -                                                 |
| extraClasspath              | MacOS/Linux loads extended class file path                                                                                                                                                                                         | /var/tmp/debug-tools/classes                      |
| extraClasspathWin           | Windows loads extended class file path                                                                                                                                                                                             | c:/var/tmp/debug-tools/classes                    |
| watchResources              | MacOS/Linux monitor resource path                                                                                                                                                                                                  | /var/tmp/debug-tools/resources                    |
| watchResourcesWin           | Windows monitor resource path                                                                                                                                                                                                      | c:/var/tmp/debug-tools/resources                  |
| spring.basePackagePrefix    | Spring base package prefix, multiple paths are separated by commas                                                                                                                                                                 | -                                                 |
| disabledPlugins             | Disabled plugins, multiple commas are separated                                                                                                                                                                                    | -                                                 |
| autoHotswap                 | Whether to automatically hot reload. Reloads class definitions in a running application after watching for changed class files in the ClassLoader's resources path. It uses the Java Instrumentation API to reload class bytecode. | false                                             |
| autoHotswap.port            | JPDA connection port, listens for changed files and performs hot reload, you need to specify the JPDA port when starting.                                                                                                          | -                                                 |

## 3. Connecting to a remote service

<!--@include: ./parts/connect-remote.md-->

## 4. Using hot reload

### 4.1 Multiple files

<!--@include: ./parts/hot-deploy-muti-file.md-->

### 4.2 Single file remote compilation

<!--@include: ./parts/hot-deploy-one-file.md-->