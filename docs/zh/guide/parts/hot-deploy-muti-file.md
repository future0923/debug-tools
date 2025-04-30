点击 <img src="/icon/hot_deployment.svg" style="display: inline-block; width: 20px; height: 20px; vertical-align: middle;" /> 按钮唤醒主页面。

![hot_deploy_button.png](/images/hot_deploy_button.png){v-zoom}

选择要部署的文件后

![hot_deploy_dialog.png](/images/hot_deploy_dialog.png){v-zoom}

- `Compiler Type`: 文件的编译方式
    - `Local Intellij Idea`：通过 idea 编译器来编译变动的文件
    - `Remote Attach Application`：通过附着应用远程动态编译变动的文件
- `Select Type`：文件选择的方式
    - `Java`：变动的 java 文件
    - `Resources`：变动的资源文件（待）
    - `Vcs`：选中通过 vcs工具来获取文件变动，不选中通过 文件修改时间 来获取文件变动

点击 `OK` 按钮，等待编译完成，编译完成后，会弹出编译成功的提示。

![hot_deploy_result.png](/images/hot_deploy_result.png){v-zoom}


::: tip
- 只有附着应用后才支持通过热部署的方式热重载变动文件
- 使用远程动态编译时，附着应用必须要通过 JDK 启动，不能使用 JRE 启动。
:::