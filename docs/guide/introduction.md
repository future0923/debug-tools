
# DebugTools 是什么？ {#introduction}

DebugTools 是一款基于IntelliJ IDEA插件的Java开发调试工具。提供快捷调用任意Java方法、打印执行SQL语句与耗时、执行Groovy脚本对目标应用进行调试等功能。

<div class="tip custom-block" style="padding-top: 8px">

只是想尝试一下？跳到[快速开始](./quick-start)。

</div>

## 使用场景 {#use-cases}

- 可以快捷调用任意Java方法，不用像 Api 一样从 Controller 层进过复杂的业务校验调用进来。
- 还可以调用远程方法，处罚远程方法运行配合远程debug达到远程调试的目的。
- 方法可以直接调用测试是否达到结果，不用写测试用例（这样不对）。
- 不修改任何应用代码的情况下，可以打印执行的SQL语句与耗时。
- 不通过 xxl-job Server可以调用 client 端方法。
- 通过 Groovy 脚本，可以运行代码获取或修改附着应用信息调试。
- ......