点击 Idea 右侧的 <img src="/pluginIcon.svg" style="display: inline-block; width: 20px; height: 20px; vertical-align: middle;" /> 工具栏唤醒 DebugTools 的窗口，点击 <img src="/icon/add.svg" alt="加号" style="display: inline-block; width: 20px; height: 20px; vertical-align: middle;" /> 获取本地可以附着的应用列表。

![tools_window](/images/tools_window.png){v-zoom}

选择要调试的应用，点击 `Attach` 按钮进行应用附着。

![application_list.png](/images/application_list.png){v-zoom}

成功附着应用后，DebugTools 会在显示附着状态。
- `L`: 标识附着的是本地应用，`R`代表是远程应用。
- `Connected`: 应用已经附着成功并连接服务成功。
- `i.g.f.d.t.t.a.DebugToolsTestApplication`: 应用名称。
    - 附着时指定应用名时为`指定的应用名`。
    - 未指定应用名时如果是 Spring 应用取 `spring.application.name` 配置项。
    - 未指定时取启动时jar中的 `Main-Class`。
    - 未找到时取启动命令中的 `sun.java.command`。

![attach_status](/images/attach_status.png){v-zoom}