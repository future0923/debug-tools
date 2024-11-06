# Header参数 {#header-params}

很多项目的调用都需要传递 `header` 信息进行信息传递或者鉴权，通过自定义的 `MockHttpServletRequest` 类实现了 `tomcat` 的 `javax.servlet.http.HttpServletRequest` 接口，将传递的 `header` 信息保存在 `MockHttpServletRequest` 中，将这个请求信息设置到 `spring` 的 `org.springframework.web.context.request.RequestContextHolder` 中方便后续的使用。

::: warning 警告
传递了 Header 参数，就会设置到 `RequestContextHolder` 中，如果有需要判断是否是 Web 环境的操作会被影响。
:::

## 全局Header参数 {#global-header-params}

点击 Idea 右侧的 <img src="/pluginIcon.svg" style="display: inline-block; width: 20px; height: 20px; vertical-align: middle;" /> 工具栏唤醒 DebugTools 的窗口，点击 `Add` 增加要传递的 Header 信息。

![global_header_tools_window](/images/global_header_tools_window.png){v-zoom}

- **Add**：增加一个 Header 参数。
- **Auth**：快捷增加一个 key 为 Authorization 参数的 Header 参数。
- **DelAll**：清空所有 Header 参数。
- **Save**：保存 Header 参数。
- **Remove**：删除当前 Header 参数。

::: tip 注意
每次修改 Header 参数都需要点击 `Save` 按钮，否则不会生效。  
所有的快捷调用方法都会携带 `Header` 参数。
:::

## 方法Header参数 {#method-header-params}

点击 `控制面板` 可以对当前调用方法单独设置此次调用的 `Header` 参数。

![quick_debug_header](/images/quick_debug_header.png){v-zoom}

- **Add**：增加一个 Header 参数。
- **Remove**：删除当前 Header 参数。

::: tip 参数优先级
调用时会将全局参数和方法参数合并传递，如果有相同的参数，会使用方法参数。
:::