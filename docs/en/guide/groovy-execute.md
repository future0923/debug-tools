# Execute Groovy Script {#execute-groovy-script}

Groovy itself can naturally support Java code, so DebugTools can write Groovy code and run it based on the attached application.

## Position

### ToolsWindow

Click the <img src="/pluginIcon.svg" style="display: inline-block; width: 20px; height: 20px; vertical-align: middle;" /> toolbar on the right side of Idea to wake up the DebugTools window. Click <img src="/icon/groovy.svg" alt="G" style="display: inline-block; width: 20px; height: 20px; vertical-align: middle;" /> on the left to open the Groovy console.

![groovy_tools_window](/images/groovy_tools_window.png){v-zoom}

### Right-click Menu

In the Idea main window, invoke the right-click menu and click the `Groovy Console` button to open the console.

![groovy_console_menu](/images/groovy_console_menu.png){v-zoom}

## Write

After clicking the Groovy controller, a `console.groovy` file will be created in `Idea -> Project -> Scratches and Consoles -> Debug Tools Plugins -> groovy`, where you can write Groovy code and run it.

![groovy_position](/images/groovy_position.png){v-zoom}

Write the Groovy code to run. You can click the [complete example code](./groovy-function#complete-example) below to view it.

![groovy_console](/images/groovy_console.png){v-zoom}

::: info Why create a file?
Convenient. When writing, idea can provide groovy code prompts, and can also prompt the code according to your current project.
:::

## Run

In the `console.groovy` file, invoke the right-click menu and click the `Run 'console.groovy' With Debug Tools` button. DebugTools will run the script with the attached application.

![groovy_run_menu](/images/groovy_run_menu.png){v-zoom}

## Result

The running results will be displayed in a pop-up at the bottom, and the [toString](./run-result#toString) and [debug](./run-result#debug) methods are provided for viewing.

![groovy_run_result](/images/groovy_run_result.png){v-zoom}

