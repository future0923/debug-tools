# 窗口工具

![idea_tools_window.png](/images/idea_tools_window.png){v-zoom}

## 控制面板

### Attach Status

[附着本地应用](./attach-local) 或 [附着远程应用](./attach-remote) 成功后，DebugTools 会在显示附着状态。

- 应用：
  - `R`: 标识附着的是远程应用
  - `L`: 标识附着的是本地应用
- 连接情况：
  - `UnAttached`: 未附着应用。
  - `Connected`: 应用已经附着成功并连接服务成功。
  - `connecting`: 正在连接服务。
  - `Reconnect`: 正在重新连接。
  - `Fail`: 连接服务失败。
- `i.g.f.d.t.t.a.DebugToolsTestApplication`: 应用名称。
    - 附着时指定应用名时为`指定的应用名`。
    - 未指定应用名时如果是 Spring 应用取 `spring.application.name` 配置项。
    - 未指定时取启动时jar中的 `Main-Class`。
    - 未找到时取启动命令中的 `sun.java.command`。
- 操作按钮：
  - `Close`: 断开与 DebugToolsServer 连接。
  - `Stop`: 附着应用停止运行 DebugToolsServer。

![remote_attach_status](/images/remote_attach_status.png){v-zoom}

### Global Header

DebugTools 在此设置全局 [header](./header) 参数信息。

![global_header_tools_window](/images/global_header_tools_window.png){v-zoom}

- **Add**：增加一个 Header 参数。
- **Auth**：快捷增加一个 key 为 Authorization 参数的 Header 参数。
- **DelAll**：清空所有 Header 参数。
- **Save**：保存 Header 参数。
- **Remove**：删除当前 Header 参数。

## 附着本地应用

点击 Idea 右侧的 <img src="/pluginIcon.svg" style="display: inline-block; width: 20px; height: 20px; vertical-align: middle;" /> 工具栏唤醒 DebugTools 的窗口，点击 <img src="/icon/add.svg" alt="加号" style="display: inline-block; width: 20px; height: 20px; vertical-align: middle;" /> 获取本地可以附着的应用列表。

![tools_window](/images/tools_window.png){v-zoom}

选择要调试的应用，点击 `Attach` 按钮进行应用附着。

![application_list.png](/images/application_list.png){v-zoom}

## 附着远程应用

点击 Idea 右侧的 <img src="/pluginIcon.svg" style="display: inline-block; width: 20px; height: 20px; vertical-align: middle;" /> 工具栏唤醒 DebugTools 的窗口，点击 <img src="/icon/connect.svg" alt="连接" style="display: inline-block; width: 20px; height: 20px; vertical-align: middle;" /> 填写远程地址。

![connect_tools_window](/images/connect_tools_window.png){v-zoom}

输入 **host** 、**tcpPort** 和 **httpPort**，点击 `Save & Connent` 按钮连接远程应用。

![connect_input.png](/images/connect_input.png){v-zoom}

## 清理缓存

点击 Idea 右侧的 <img src="/pluginIcon.svg" style="display: inline-block; width: 20px; height: 20px; vertical-align: middle;" /> 工具栏唤醒 DebugTools 的窗口，点击 <img src="/icon/clear.svg" alt="清除" style="display: inline-block; width: 20px; height: 20px; vertical-align: middle;" /> 可以清理 DebugTools 缓存。

![clear_cache.png](/images/clear_cache.png){v-zoom}

### Core jar cache

清理加载的 core jar 缓存。

::: info

当有版本应该未生效时可以清理核心jar重新加载

:::

### Method param cache

清理存储的[方法上一次调用参数](./quick-debug#store)缓存。

### Global header

清理所有的 [global header](./header##global-header-params) 信息。

### Clear all

清理掉所有的缓存信息。

## 打开Groovy控制台

点击 Idea 右侧的 <img src="/pluginIcon.svg" style="display: inline-block; width: 20px; height: 20px; vertical-align: middle;" /> 工具栏唤醒 DebugTools 的窗口，在左侧点击 <img src="/icon/groovy.svg" alt="G" style="display: inline-block; width: 20px; height: 20px; vertical-align: middle;" /> 打开 Groovy 控制台。

![groovy_tools_window](/images/groovy_tools_window.png){v-zoom}