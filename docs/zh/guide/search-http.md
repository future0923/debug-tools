# 搜索 http 地址以直接跳转到相应的方法定义 {#search-http-url}

## 用途 {#purpose}

当我们开发项目时，通过 Spring 的 Mapping 注解定义了多个方法，生成最终了 url，当我们通过 url 搜索对应的代码位置时十分的不方便。

## 使用 {#use}

### 快捷键

默认快捷键 macOS `command option N` / windows `ctrl alt N`，可以在如下位置修改自己想要的快捷键。

![search_http_keymap.png](/images/search_http_keymap.png){v-zoom}

唤醒搜索框输入url，选择对应的url后跳转代码定义的位置。

![search_http.png](/images/search_http.png){v-zoom}

### 工具栏

点击 Idea 右侧的 <img src="/pluginIcon.svg" style="display: inline-block; width: 20px; height: 20px; vertical-align: middle;" /> 工具栏唤醒 DebugTools 的窗口，在左侧点击 <img src="/icon/search.svg" alt="S" style="display: inline-block; width: 20px; height: 20px; vertical-align: middle;" /> 唤醒搜索框。

![search_tools_window.png](/images/search_tools_window.png){v-zoom}

### 信息提取

::: warning 强大的url信息提取，无论你如何输入url的格式(如下面)都会提提取出来 `/test` 信息
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