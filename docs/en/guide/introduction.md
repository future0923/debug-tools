# What is DebugTools? {#introduction}

DebugTools is a Java development and debugging plug-in integrated into IntelliJ IDEA, focusing on improving development efficiency and shortening debugging cycles.

<div class="tip custom-block" style="padding-top: 8px">
The plug-in is complex to implement and requires a lot of compatibility. If this project helps you save development time, you might as well click <a target="_blank" href="https://github.com/java-hot-deploy/debug-tools"><img src="https://img.shields.io/github/stars/java-hot-deploy/debug-tools?style=flat&logo=GitHub" style="display: inline-block; vertical-align: middle;" /></a>. Your recognition will make more people discover it, and your support is my motivation for updating. If it doesn't work, please submit <a target="_blank" href="https://github.com/java-hot-deploy/debug-tools/issues"><img src="https://img.shields.io/github/issues-closed/java-hot-deploy/debug-tools?style=flat&logo=github" style="display: inline-block; vertical-align: middle;" /></a> to give us some feedback.
</div>

## Use cases {#use-cases}

- [Hot-deploy](hot-deploy): The traditional deployment process is generally to submit code->pull code->package->deploy->restart the project before the written code can take effect. Hot deployment can skip this tedious process. After the developer modifies the code, there is no need to manually trigger packaging or restart the service. The application can load the new logic in real time and run, greatly shortening the feedback cycle. After we introduce hot deployment in development/testing environments, the overall development efficiency of the team can be greatly improved, especially for high-frequency iteration scenarios under the agile development model.
- [Hot reload](hot-reload): When writing code traditionally, you need to restart the application to take effect, but hot reload can make the written code take effect immediately without restarting the application, so that the code changes written by the developer can take effect instantly, greatly improving the iteration efficiency. Supports changes to the properties and methods of classes (including proxy classes), Spring, Mybatis and other mainstream frameworks. At the same time, it is compatible with multiple JDK versions such as jdk8, jdk11, jdk17, and jdk21.
- [Call any Java method](attach-local): There is no need to call it layer by layer after complex business verification from the Controller layer like Api. The method can be directly called to test whether the result is achieved without writing test cases. Combined with hot reload, it can be modified quickly without restarting.
- [Call remote method](attach-remote): Trigger the remote method to run and cooperate with remote debug to achieve the purpose of remote debugging. Through hot deployment, the application can be debugged quickly.
- [SQL statements and time consumption](sql): Without modifying any application code, the executed SQL statements and time consumption can be printed.
- [xxl-job](xxl-job): You can call client methods without going through xxl-job Server, and support context parameter passing.
- [groovy](groovy-execute): Execute Groovy scripts, you can run code to get or modify attached application information for debugging.
- ......

If you have customization needs, you can submit [issue](https://github.com/java-hot-deploy/debug-tools/issues) feedback.

## Demonstration of the effect.

- hot deploy

<video controls width="640">
  <source src="https://download.debug-tools.cc/mp4/hot_deploy.mp4" type="video/mp4">https://download.debug-tools.cc/mp4/hot_deploy.mp4
</video>

- hot reload

<video controls width="640">
  <source src="https://download.debug-tools.cc/mp4/hot_reload.mp4" type="video/mp4">https://download.debug-tools.cc/mp4/hot_reload.mp4
</video>

- call any Java method

<video controls width="640">
  <source src="https://download.debug-tools.cc/mp4/quick_debug.mp4" type="video/mp4">https://download.debug-tools.cc/mp4/quick_debug.mp4
</video>

- search HttpUrl

<video controls width="640">
  <source src="https://download.debug-tools.cc/mp4/search_url.mp4" type="video/mp4">https://download.debug-tools.cc/mp4/search_url.mp4
</video>
