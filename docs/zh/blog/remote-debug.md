# 远程调试

## 参数详解

| 参数             | 作用                                                  | 说明                                                                                                        |
|----------------|-----------------------------------------------------|-----------------------------------------------------------------------------------------------------------|
| agentlib:jdwp	 | 启用 Java Debug Wire Protocol (JDWP)，是 Java 提供的远程调试协议 |                                                                                                           |
| transport	     | 使用的传输方式                                             | dt_socket: socket <br> dt_shmem: 共享内存(仅Windows)                                                           |
| server         | 是否作为调试服务器启动                                         | y: 作为服务器启动，等待调试器连接  <br> [例子1](#example1) [例子2](#example2) <br> n: JVM作为客户端，主动连接调试器 <br> [例子3](#example3) |
| suspend        | 是否在启动后挂起                                            | true: 启动后挂起等待调试器连接后才执行，调试器断开后挂起等待连接 <br> false: 不需要等待调试器                                                  |
| address        | 监听的端口号或共享内存名称                                       |                                                                                                           |

## Debug使用

### 例子1 {#example1}

如我在调试远程运行的程序agent代码时，我需要设置一下参数，如下

```shell
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5005 -XXaltjvm=dcevm -javaagent:/Users/weilai/Documents/debug-tools/debug-tools-attach/target/debug-tools-agent.jar=hotswap=true -jar debug-tools-test-spring-boot-mybatis.jar
```

因为我要调试 agent，所以设置 `suspend=y`，等我连接时候才运行，要不执行太快根本无法调试 `premain` 方法。

![wait_connect.png](/images/wait_connect.png){v-zoom}

打开 idea 配置远程 debug。

![add_idea_remote_debug.png](/images/add_idea_remote_debug.png){v-zoom}

![idea_remote_debug.png](/images/idea_remote_debug.png){v-zoom}

运行远程调试

![run_remote_debug.png](/images/run_remote_debug.png){v-zoom}

远程应用运行开始执行，debug生效。

### 例子2 {#example2}

如我在调试远程运行的程序代码时（如调试controller代码），我需要设置一下参数，如下

```shell
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -XXaltjvm=dcevm -javaagent:/Users/weilai/Documents/debug-tools/debug-tools-attach/target/debug-tools-agent.jar=hotswap=true -jar debug-tools-test-spring-boot-mybatis.jar
```

这里我设置 `suspend=n`，不需要等我连接时候才运行，运行时直接启动，我要调试启动后的代码，所以不需要等待连接才启动。

打开 idea 配置远程 debug。

![add_idea_remote_debug.png](/images/add_idea_remote_debug.png){v-zoom}

![idea_remote_debug.png](/images/idea_remote_debug.png){v-zoom}

运行远程调试

![run_remote_debug.png](/images/run_remote_debug.png){v-zoom}

打断点后，通过API调用或者使用[DebugTools](../guide/attach-remote)调用远程方法，debug生效。

### 例子3 {#example3}

使用 `server=n` 模式下远程调试例子，适合 JVM 主动连接 IDEA 的场景。
- 防火墙只允许出站连接
- 容器不方便开放端口
- 你希望 JVM 主动连接开发机

场景如下：
- 我电脑(Idea)所在ip为 **192.168.0.104**
- 想让服务器或 Docker 容器中的 JVM 主动连回来
- 使用端口是 **5005**

打开 idea 配置远程 debug。

![add_idea_remote_debug.png](/images/add_idea_remote_debug.png){v-zoom}

修改 `Debugger mode` 为 `Listen to remote JVM`。

![listen_to_jvm.png](/images/listen_to_jvm.png){v-zoom}

启动后，IDEA 会开始监听本地 5005 端口，等待远程 JVM 来连接

这时候我们启动远程应用，设置 `server=n,address=192.168.0.104:5005`。`suspend`参数还是一样含义，等不等待建立连接在执行。

```shell
java -agentlib:jdwp=transport=dt_socket,server=n,suspend=y,address=192.168.0.104:5005 -XXaltjvm=dcevm -javaagent:/Users/weilai/Documents/debug-tools/debug-tools-attach/target/debug-tools-agent.jar=hotswap=true -jar debug-tools-test-spring-boot-mybatis-4.0.0-SNAPSHOT.jar
```

![server_n_application.png](/images/server_n_application.png){v-zoom}

idea 收到连接后，会自动连接上。断点生效。

![server_n_idea.png](/images/server_n_idea.png){v-zoom}

