
# DebugTools 是什么？ {#introduction}

DebugTools 是一款集成于 IntelliJ IDEA 的 Java 开发调试插件，专注于提升开发效率与缩短调试周期。

<div class="tip custom-block" style="padding-top: 8px">
插件实现复杂且需要兼容情况较多，如果这个项目帮你节省了开发时间，不妨点个 <a target="_blank" href="https://github.com/java-hot-deploy/debug-tools"><img src="https://img.shields.io/github/stars/java-hot-deploy/debug-tools?style=flat&logo=GitHub" style="display: inline-block; vertical-align: middle;" /></a>，你的认可会让更多人发现它，你的支持是我更新的动力。如果不生效麻烦提交 <a target="_blank" href="https://github.com/java-hot-deploy/debug-tools/issues"><img src="https://img.shields.io/github/issues-closed/java-hot-deploy/debug-tools?style=flat&logo=github" style="display: inline-block; vertical-align: middle;" /></a> 反馈一下。
</div>

## 使用场景 {#use-cases}

- [热部署](hot-deploy)：传统的部署流程一般为提交代码->拉取代码->打包->部署->重启项目后才能让编写的代码生效。而热部署可以跳过这繁琐的流程，开发者修改代码后无需手动触发打包或重启服务，应用即可实时加载新逻辑并运行，极大缩短反馈周期。我们开发/测试等环境引入热部署后，团队整体开发效率可提升大大提升，尤其适用于敏捷开发模式下的高频迭代场景。
- [热重载](hot-reload)：传统编写代码时，需要重启应用才能生效，而热重载可以在不重启应用下让编写的代码生效立刻，让开发者编写的代码改动瞬间生效，极大提升迭代效率。支持类(包括代理类)的属性和方法变动、Spring、Mybatis等主流框架。同时适配 jdk8、jdk11、jdk17、jdk21 等多个JDK版本。
- [调用任意Java方法](attach-local)：无需像 Api 一样从 Controller 层进过复杂的业务校验后一层层调用进来，方法可以直接调用测试是否达到结果，不用写测试用例。结合热重载可以快速修改，无需重启。
- [调用远程方法](attach-remote)：触发远程方法运行配合远程debug达到远程调试的目的，通过热部署可以快捷调试应用。
- [SQL语句与耗时](sql)：不修改任何应用代码的情况下，可以打印执行的SQL语句与耗时。
- [xxl-job](xxl-job)：不通过 xxl-job Server可以调用 client 端方法，并支持上下文传参。
- [groovy](groovy-execute)：执行 Groovy 脚本，可以运行代码获取或修改附着应用信息调试。
- ......

如果你有订制需要，可以提交 [issue](https://github.com/java-hot-deploy/debug-tools/issues) 反馈。

## 效果演示

- 热部署

<video controls width="640">
  <source src="https://download.debug-tools.cc/mp4/hot_deploy.mp4" type="video/mp4">https://download.debug-tools.cc/mp4/hot_deploy.mp4
</video>

- 热重载

<video controls width="640">
  <source src="https://download.debug-tools.cc/mp4/hot_reload.mp4" type="video/mp4">https://download.debug-tools.cc/mp4/hot_reload.mp4
</video>

- 调用任意Java方法

<video controls width="640">
  <source src="https://download.debug-tools.cc/mp4/quick_debug.mp4" type="video/mp4">https://download.debug-tools.cc/mp4/quick_debug.mp4
</video>

- 搜索HttpUrl

<video controls width="640">
  <source src="https://download.debug-tools.cc/mp4/search_url.mp4" type="video/mp4">https://download.debug-tools.cc/mp4/search_url.mp4
</video>
