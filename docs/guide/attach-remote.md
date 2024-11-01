# 附着远程应用 {#attach-remote}

## 启动

### 下载debug-tools-boot.jar

::: code-group

```text [网址]
https://download.debug-tools.cc/debug-tools-boot.jar
```

```sh [手动构建]
git clone https://github.com/future0923/debug-tools.git
cd debug-tools
mvn clean install -T 2C -Dmaven.test.skip=true
# dist目录下
# debug-tools-boot.jar 远程agent包
```

```text [github]
https://github.com/future0923/debug-tools/releases
```

```text [gitee]
https://gitee.com/future94/debug-tools/releases
```

:::

::: details 手动构建遇到了打包失败问题

目前maven打包需要使用 `java1.8` 版本构建。

:::

### 附着

运行 `debug-tools-tools.jar` 包。

```shell
java -jar debug-tools-tools.jar
```

输出如下：

```text
[INFO] debug-tools-boot version: 3.0.0
[INFO] Found existing java process, please choose one and input the serial number of the process, eg : 1. Then hit ENTER.
* [1]: 8317 /home/work/debug-tools-test-application-3.0.0.jar
  [2]: 13167 /home/work/debug-tools-boot.jar
```

输入要入附着应用的序号后回车，会输出成功失败并打印 `tcpPort` 和 `httpPort` 信息。

```text
[INFO] Try to attach process 8317
[INFO] Attach process 8317 success. tcp port 12345, http port 22222.
```

### 配置

通过 `java -jar debug-tools-boot.jar -h` 可以查看参数说明。

```text
usage: debug-tools
 -hp,--http-port <arg>   target application server http port
                         default get available port.
 -tp,--tcp-port <arg>    target application server tcp port
                         default get available port.
```

| 参数              |     说明     |                  默认值 |
|-----------------|:----------:|---------------------:|
| -hp,--http-port | 指定 http 端口 | 默认从 22222 开始递增寻找可用端口 |
| -tp,--tcp-port  | 指定 tcp 端口  | 默认从 12345 开始递增寻找可用端口 |

## 连接

点击 Idea 右侧的 <img src="/pluginIcon.svg" style="display: inline-block; width: 20px; height: 20px; vertical-align: middle;" /> 工具栏唤醒 DebugTools 的窗口，点击 <img src="/icon/connect.svg" alt="连接" style="display: inline-block; width: 20px; height: 20px; vertical-align: middle;" /> 填写远程地址。

![connect_tools_window](/images/connect_tools_window.png){v-zoom}

输入 **host** 、**tcpPort** 和 **httpPort**，点击 `Save & Connent` 按钮连接远程应用。

![connect_input.png](/images/connect_input.png){v-zoom}

成功附着应用后，DebugTools 会在显示附着状态。
- `R`: 标识附着的是远程应用，`L`代表是本地应用。
- `Connected`: 应用已经附着成功并连接服务成功。
- `i.g.f.d.t.t.a.DebugToolsTestApplication`: 应用名称。
    - 附着时指定应用名时为`指定的应用名`。
    - 未指定应用名时如果是 Spring 应用取 `spring.application.name` 配置项。
    - 未指定时取启动时jar中的 `Main-Class`。
    - 未找到时取启动命令中的 `sun.java.command`。

![remote_attach_status](/images/remote_attach_status.png){v-zoom}

## 使用

连接成功后，可以唤醒[调试面板](./quick-debug)进行远程方法快捷调用并[查看结果](./run-result)，也可以使用[groovy控制台](./groovy-execute)。

## 高级

如果附着应用和Idea开启了 `远程Debug` 功能，通过 DebugTools 不但可以快速调用远程方法，还可以进行远程Debug断点调试。
