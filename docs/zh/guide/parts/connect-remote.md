
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
