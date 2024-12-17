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

### Information extraction

::: warning Powerful URL information extraction, no matter how you enter the URL format (such as below), it will extract the `/test` information
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