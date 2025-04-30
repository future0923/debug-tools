The `Remote Compile 'xxx' to Hot Reload` button in the right-click menu can be used to hot reload a single changed file through remote dynamic compilation.

![hot_compile_file_button.png](/images/hot_compile_file_button.png){v-zoom}

After hot deployment is completed, a prompt will pop up indicating successful compilation.

![hot_deploy_result.png](/images/hot_deploy_result.png){v-zoom}

::: tip

- Hot reloading of changed files through hot deployment is only supported after the application is attached
- When using remote dynamic compilation, the attached application must be started through JDK, not JRE.

:::