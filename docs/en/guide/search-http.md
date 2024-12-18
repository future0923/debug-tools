# Search http url to jump directly to the corresponding method definition {#search-http-url}

## purpose {#purpose}

When we develop a project, we define multiple methods through Spring's Mapping annotation and generate the final URL. It is very inconvenient when we search for the corresponding code location through the URL.

## use {#use}

### Shortcut keys 

Default shortcut keys are macOS `command option N` / Windows `ctrl alt N`. You can modify the shortcut keys you want in the following locations.

![search_http_keymap.png](/images/search_http_keymap.png){v-zoom}

Wake up the search box and enter the URL. Select the corresponding URL and jump to the location defined by the code.

![search_http.png](/images/search_http.png){v-zoom}

### ToolWindow

Click the <img src="/pluginIcon.svg" style="display: inline-block; width: 20px; height: 20px; vertical-align: middle;" /> toolbar on the right side of Idea to wake up the DebugTools window, and click <img src="/icon/search.svg" alt="S" style="display: inline-block; width: 20px; height: 20px; vertical-align: middle;" /> on the left to wake up the search box.

![search_tools_window.png](/images/search_tools_window.png){v-zoom}

### Match Path information {#match-path}

#### Powerful information extraction

::: tip No matter how you enter the URL format (such as below), the `/test` information will be extracted
- `localhost/test?test=12`
- `http://localhost/test?test=12`
- `https://debug-tools.cc/test?test=12`
- `http://debug-tools.cc/test?test=12`
- `debug-tools.cc/test?test=12`
- `www.sada.com/test?test=12`
- `dasf.com/test?test=12`
- `cass.com/test`
- `hezhdsaong.com/test`
- `192.31.1.3/test`
- `192.31.1.3:31/test`
- `http://192.31.1.3:31/test`
:::

![url-extract.png](/images/url-extract.png){v-zoom}

#### Remove ContextPath information

Many times we configure `server.servlet.context-path`, which will result in the corresponding method not being matched during search; or there will be redundant Path information when there is gateway forwarding.

We can configure the `ContextPath` information to be removed in the configuration, and multiple pieces of information can be split by separators. The separators supported by DebugTools are `,`, `,` and `newline symbol` (`\r`, `\n`, `\r\n`).

![config_context_path.png](/images/config_context_path.png){v-zoom}

For the above configuration, I will get the configurations to be removed as `contextPath1`, `contextPath2`, `contextPath3` and `contextPath4`.

::: tip When you configure the ContentPath information to be removed, the `/test` information will be extracted no matter how you enter the URL format (such as below)
- `localhost/contextPath1/test?test=12`
- `http://localhost/contextPath2/test?test=12`
- `https://debug-tools.cc/contextPath3/test?test=12`
- `http://debug-tools.cc/contextPath4/test?test=12`
- `debug-tools.cc/contextPath1/test?test=12`
- `www.sada.com/contextPath2test?test=12`
- `dasf.com/contextPath3/test?test=12`
- `cass.com/contextPath4/test`
- `hezhdsaong.com/contextPath1/test`
- `192.31.1.3/contextPath2/test`
- `192.31.1.3:31/contextPath3/test` 
- `http://192.31.1.3:31/contextPath4/test`
::: 

![remove_context_path_demo.png](/images/remove_context_path_demo.png){v-zoom}