# Window Tools

![idea_tools_window.png](/images/idea_tools_window.png){v-zoom}

## Debug Panel

### Attach Status

After successfully attaching a [local application](./attach-local) or attaching a [remote application](./attach-remote), DebugTools will display the attachment status.

- Application:
  - `R`: indicates that the application is a remote application
  - `L`: indicates that the application is a local application
- Connection Status:
  - `UnAttached`: The application is not attached.
  - `Connected`: The application has been attached successfully and connected to the service successfully.
  - `connecting`: Connecting to the service.
  - `Reconnect`: Reconnecting.
  - `Fail`: Failed to connect to the service.
- `i.g.f.d.t.t.a.DebugToolsTestApplication`: Application name.
  - Specified `application name` is used when specifying the application name during attachment.
  - If the application name is not specified, if it is a Spring application, the `spring.application.name` configuration item is used.
  - If not specified, the `Main-Class` in the startup jar is used.
  - If not found, the `sun.java.command` in the startup command is used.
- Operation buttons:
  - `Close`: Disconnect from DebugToolsServer.
  - `Stop`: Attach the application and stop running DebugToolsServer.

![remote_attach_status](/images/remote_attach_status.png){v-zoom}

### Global Header

DebugTools sets the global [header](./header) parameter information here.

![global_header_tools_window](/images/global_header_tools_window.png){v-zoom}

- **Add**: Add a Header parameter.
- **Auth**: Quickly add a Header parameter with the key Authorization parameter.
- **DelAll**: Clear all Header parameters.
- **Save**: Save Header parameters.
- **Remove**: Delete the current Header parameters.

## Attach Local Applications

Click the <img src="/pluginIcon.svg" style="display: inline-block; width: 20px; height: 20px; vertical-align: middle;" /> toolbar on the right side of Idea to wake up the DebugTools window, and click <img src="/icon/add.svg" alt="Plus" style="display: inline-block; width: 20px; height: 20px; vertical-align: middle;" /> to get a list of local applications that can be attached.

![tools_window](/images/tools_window.png){v-zoom}

Select the application to be debugged and click the `Attach` button to attach the application.

![application_list.png](/images/application_list.png){v-zoom}

## Attach Remote Application

Click the <img src="/pluginIcon.svg" style="display: inline-block; width: 20px; height: 20px; vertical-align: middle;" /> toolbar on the right side of Idea to wake up the DebugTools window, click <img src="/icon/connect.svg" alt="Connect" style="display: inline-block; width: 20px; height: 20px; vertical-align: middle;" /> to fill in the remote address.

![connect_tools_window](/images/connect_tools_window.png){v-zoom}

Enter **host**, **tcpPort** and **httpPort**, and click the `Save & Connent` button to connect to the remote application.

![connect_input.png](/images/connect_input.png){v-zoom}

## Clear Cache

Click the <img src="/pluginIcon.svg" style="display: inline-block; width: 20px; height: 20px; vertical-align: middle;" /> toolbar on the right side of Idea to wake up the DebugTools window, and click <img src="/icon/clear.svg" alt="Clear" style="display: inline-block; width: 20px; height: 20px; vertical-align: middle;" /> to clear the DebugTools cache.

![clear_cache.png](/images/clear_cache.png){v-zoom}

### Core jar cache

Clear the loaded core jar cache.

::: info

When a version should not be effective, you can clear the core jar and reload it.

:::

### Method param cache

Clear the stored [method last call parameters](./quick-debug#store) cache.

### Global header

Clear all [global header](./header##global-header-params) information.

### Clear all

Clear all cache information.

## Open Groovy console

Click the <img src="/pluginIcon.svg" style="display: inline-block; width: 20px; height: 20px; vertical-align: middle;" /> toolbar on the right side of Idea to wake up the DebugTools window, and click <img src="/icon/groovy.svg" alt="G" style="display: inline-block; width: 20px; height: 20px; vertical-align: middle;" /> on the left to open the Groovy console.

![groovy_tools_window](/images/groovy_tools_window.png){v-zoom}