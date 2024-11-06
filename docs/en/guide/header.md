# Header Parameters {#header-params}

Many project calls require the transmission of `header` information for information transmission or authentication. The `javax.servlet.http.HttpServletRequest` interface of `tomcat` is implemented through the custom `MockHttpServletRequest` class, and the transmitted `header` information is saved in `MockHttpServletRequest`. This request information is set to `org.springframework.web.context.request.RequestContextHolder` of `spring` for subsequent use.

::: warning
If the Header parameter is passed, it will be set to `RequestContextHolder`. If there is a need to determine whether it is a Web environment operation, it will be affected.
:::

## Global Header Parameters {#global-header-params}

Click the <img src="/pluginIcon.svg" style="display: inline-block; width: 20px; height: 20px; vertical-align: middle;" /> toolbar on the right side of Idea to wake up the DebugTools window, and click `Add` to add the Header information to be passed.

![global_header_tools_window](/images/global_header_tools_window.png){v-zoom}

- **Add**: Add a Header parameter.
- **Auth**: Quickly add a Header parameter with the key as the Authorization parameter.
- **DelAll**: Clear all Header parameters.
- **Save**: Save the Header parameters.
- **Remove**: Delete the current Header parameters.

::: tip
You need to click the `Save` button every time you modify the Header parameter, otherwise it will not take effect.  
All quick call methods will carry the `Header` parameter.
:::

## Method Header Parameter {#method-header-params}

Click `Debug Panel` to set the `Header` parameter for the current call method separately.

![quick_debug_header](/images/quick_debug_header.png){v-zoom}

- **Add**: Add a Header parameter.

- **Remove**: Delete the current Header parameter.

::: tip Parameter Priority
When calling, the global parameters and method parameters will be merged and passed. If there are the same parameters, the method parameters will be used.
:::