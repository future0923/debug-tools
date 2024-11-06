Click on the <img src="/pluginIcon.svg" style="display: inline-block; width: 20px; height: 20px; vertical-align: middle;" /> toolbar on the right side of Idea to wake up the DebugTools window. Click <img src="/icon/add.svg" alt="加号" style="display: inline-block; width: 20px; height: 20px; vertical-align: middle;" /> to get a list of applications that can be attached locally.

![tools_window](/images/tools_window.png){v-zoom}

Select the application you want to debug and click the `Attach` button to attach the application.

![application_list.png](/images/application_list.png){v-zoom}

After successfully attaching the app, DebugTools displays the attachment state.
- `L`: The logo is attached to the local application, `R` for remote application.
- `Connected`: The application has been successfully attached and the service has been successfully connected.
- `i.g.f.d.t.t.a.DebugToolsTestApplication`: application name.
    - Attach when specifying the application name is the specified `application name`.
    - If the application name is not specified, the Spring application `spring.application.name` configuration item.
    - When not specified, take the `Main-Class` in the startup jar.
    - Fetch `sun.java.command` in the startup command when not found.

![attach_status](/images/attach_status.png){v-zoom}