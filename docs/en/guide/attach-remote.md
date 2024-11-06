# Attach Remote Application {#attach-remote}

## Start

### Download debug-tools-boot.jar

::: code-group

```text [url]
https://download.debug-tools.cc/debug-tools-boot.jar
```

```sh [Build manually]
git clone https://github.com/future0923/debug-tools.git
cd debug-tools
mvn clean install -T 2C -Dmaven.test.skip=true
# dist dir
# debug-tools-boot.jar remote agent jar
```

```text [github]
https://github.com/future0923/debug-tools/releases
```

```text [gitee]
https://gitee.com/future94/debug-tools/releases
```

:::

::: details The manual build encountered a packaging failure problem

The current Maven packaging requires a `Java 1.8` build.

:::

### Attach

Run the `debug-tools-tools.jar` jar.

```shell
java -jar debug-tools-tools.jar
```

The output is as follows：

```text
[INFO] debug-tools-boot version: 3.0.0
[INFO] Found existing java process, please choose one and input the serial number of the process, eg : 1. Then hit ENTER.
* [1]: 8317 /home/work/debug-tools-test-application-3.0.0.jar
  [2]: 13167 /home/work/debug-tools-boot.jar
```

Enter the serial number of the attached application and press Enter, it will output Success Failure and print `tcpPort` and `httpPort` information.

```text
[INFO] Try to attach process 8317
[INFO] Attach process 8317 success. tcp port 12345, http port 22222.
```

### Config

The parameter description can be viewed through `java -jar debug-tools-boot.jar -h`.

```text
usage: debug-tools
 -hp,--http-port <arg>   target application server http port
                         default get available port.
 -tp,--tcp-port <arg>    target application server tcp port
                         default get available port.
```

| Parameter       |      Description      |                                      Default |
|-----------------|:---------------------:|---------------------------------------------:|
| -hp,--http-port | Specify the HTTP port | increment from 22222 to find available ports |
| -tp,--tcp-port  | Specify the TCP port  | increment from 12345 to find available ports |

## Connect

Click <img src="/pluginIcon.svg" style="display: inline-block; width: 20px; height: 20px; vertical-align: middle;" /> toolbar on the right side of Idea to wake up the DebugTools window. Click <img src="/icon/connect.svg" alt="连接" style="display: inline-block; width: 20px; height: 20px; vertical-align: middle;" /> to fill in the remote address.

![connect_tools_window](/images/connect_tools_window.png){v-zoom}

Enter **host** , **tcpPort** , **httpPort**, and click the `Save & Connent` button to connect to the remote application.

![connect_input.png](/images/connect_input.png){v-zoom}

After successfully attaching the app, DebugTools displays the attachment state.
- `R`: The logo is attached to the remote application，`L` for local application.
- `Connected`: The application has been successfully attached and the service has been successfully connected.
- `i.g.f.d.t.t.a.DebugToolsTestApplication`: application name.
  - Attach when specifying the application name is the specified `application name`.
  - If the application name is not specified, the Spring application `spring.application.name` configuration item.
  - When not specified, take the `Main-Class` in the startup jar.
  - Fetch `sun.java.command` in the startup command when not found.

![remote_attach_status](/images/remote_attach_status.png){v-zoom}

## Use

After the connection is successful, you can wake up the [debug panel](./quick-debug) to make a shortcut call to a remote method and [view results](./run-result), or you can use [groovy console](./groovy-execute).

## Advanced

If the attached app and Idea enable the `remote Debug`, DebugTools can not only quickly call remote methods, but also perform remote Debug breakpoint debugging.
