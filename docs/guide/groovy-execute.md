# 执行Groovy脚本 {#execute-groovy-script}

Groovy 自身天然可以支持 Java 代码，所以 DebugTools 可以编写 Groovy 代码并依托附着应用运行。

## 位置

### ToolsWindow

点击 Idea 右侧的 <img src="/pluginIcon.svg" style="display: inline-block; width: 20px; height: 20px; vertical-align: middle;" /> 工具栏唤醒 DebugTools 的窗口，在左侧点击 <img src="/icon/groovy.svg" alt="G" style="display: inline-block; width: 20px; height: 20px; vertical-align: middle;" /> 打开 Groovy 控制台。

![groovy_tools_window](/images/groovy_tools_window.png){v-zoom}

### 右键菜单

在 Idea 主窗口中唤醒右键菜单点击 `Groovy Console` 按钮打开控制台。

![groovy_console_menu](/images/groovy_console_menu.png){v-zoom}

## 编写

点击 Groovy 控制器后会在 `Idea -> Project -> Scratches and Consoles -> Debug Tools Plugins -> groovy` 位置创建 `console.groovy` 文件，可以编写 Groovy 代码并运行。

![groovy_position](/images/groovy_position.png){v-zoom}

编写要运行的 Groovy 代码，下图[完整示例代码](./groovy-function#complete-example)可以点击查看

![groovy_console](/images/groovy_console.png){v-zoom}

::: info 为什么要创建一个文件？
方便。编写时idea可以提供 groovy 的代码提示，还可以跟你当前所在项目对代码进行提示。
:::

## 运行

在 `console.groovy` 文件中唤醒个右键菜单点击 `Run 'console.groovy' With Debug Toools` 按钮后 DebugTools 会通过附着应用运行该脚本。

![groovy_run_menu](/images/groovy_run_menu.png){v-zoom}

## 结果

运行结果会在底部弹出展示，提供 [toString](./run-result#toString) 和 [debug](./run-result#debug) 方法查看。

![groovy_run_result](/images/groovy_run_result.png){v-zoom}

