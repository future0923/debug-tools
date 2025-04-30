Click the <img src="/icon/hot_deployment.svg" style="display: inline-block; width: 20px; height: 20px; vertical-align: middle;" /> button to wake up the main page.

![hot_deploy_button.png](/images/hot_deploy_button.png){v-zoom}

After selecting the file to be deployed

![hot_deploy_dialog.png](/images/hot_deploy_dialog.png){v-zoom}

- `Compiler Type`: file compilation method
  - `Local Intellij Idea`: compile the changed files through the idea compiler
  - `Remote Attach Application`: remote dynamic compilation of the changed files through the attached application
- `Select Type`: file selection method
  - `Java`: changed java files
  - `Resources`: changed resource files (pending)
  - `Vcs`: select to obtain file changes through the vcs tool, and do not select to obtain file changes through the file modification time

Click the `OK` button and wait for the compilation to complete. After the compilation is completed, a prompt for successful compilation will pop up.

![hot_deploy_result.png](/images/hot_deploy_result.png){v-zoom}

::: tip
- Hot reloading of changed files through hot deployment is supported only after the application is attached
- When using remote dynamic compilation, the attached application must be started through JDK, not JRE.
:::