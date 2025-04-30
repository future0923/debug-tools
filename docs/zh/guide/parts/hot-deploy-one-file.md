通过右键菜单的 `Remote Compile 'xxx' to Hot Reload` 按钮可以通过远程动态编译的方式热重载单个变动文件.

![hot_compile_file_button.png](/images/hot_compile_file_button.png){v-zoom}

热部署完成后，会弹出编译成功的提示。

![hot_deploy_result.png](/images/hot_deploy_result.png){v-zoom}

::: tip
- 只有附着应用后才支持通过热部署的方式热重载变动文件
- 使用远程动态编译时，附着应用必须要通过 JDK 启动，不能使用 JRE 启动。
:::