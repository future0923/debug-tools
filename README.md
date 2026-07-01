<br/>

<p align="center">
    <a href="https://github.com/future0923/debug-tools/blob/main/README.md">
      中文
    </a>
    |
    <a href="https://github.com/future0923/debug-tools/blob/main/README-en.md" >
      English
    </a>
</p>

<p align="center">
  <strong>集成于 IntelliJ IDEA 的插件，专注于提升开发效率与缩短调试周期</strong>
</p>

<p align="center">
  <a target="_blank" href="https://debug-tools.cc">https://debug-tools.cc</a>
</p>

<p align="center">
    <a href="https://debug-tools.cc">
      <img src="https://img.shields.io/badge/文档-简体中文-blue.svg" alt="简体中文文档" />
    </a>
    <a href="https://debug-tools.cc/en" >
      <img src="https://img.shields.io/badge/Document-English-blue.svg" alt="EN docs" />
    </a>
</p>

<p align="center">
    <img src="https://img.shields.io/badge/License-GPL%203.0-blue.svg?label=license" alt="GPL 3.0" />
    <img src="https://img.shields.io/jetbrains/plugin/d/24463?style=flat&color=blue" alt="github contributors"/>
    <img src="https://img.shields.io/badge/QQ群-518757118-blue.svg" alt="518757118" />
    <img src="https://img.shields.io/badge/Email-future94@qq.com-blue.svg" alt="future94@qq.com" />
</p>

# 功能

## 调用任意Java方法

DebugTools 支持在 IntelliJ IDEA 中直接调用项目内的 Java 方法，本地和远程都支持，支持多个应用多个方法同时调用。

无需新增 Controller、编写临时测试用例或从请求入口逐层触发，只需要选择目标方法、填写参数并执行，即可快速验证业务逻辑。

**适用场景**
- 可调用普通类方法、静态方法、Spring Bean 方法、Dubbo 服务、XXL-JOB 任务、MQ 消费方法、MyBatis Mapper 方法、流式 SSE 响应等。
- 验证某个 Service、Mapper、工具类或组件方法的返回结果
- 跳过 Controller、网关、鉴权、参数组装等外层流程，直接调试核心业务逻辑
- 快速调用 Spring 管理的 Bean 方法，包括 Dubbo、XXL-JOB、MQ 等框架内的方法
- 调用 MyBatis Mapper 方法，验证 SQL 参数、查询结果和执行耗时
- 给方法传递复杂参数、Header 参数或 XXL-JOB 参数
- 远程调用目标应用方法，并配合远程 Debug 定位问题
- 结合 **热重载** 修改代码后立即再次调用验证

详细操作使用详见 [文档](https://debug-tools.cc/guide/method/quick-start.html)

## 热重载

传统编写代码时，需要重启应用才能生效，而热重载可以在**不重启应用下让编写的代码生效立刻**，让开发者编写的代码改动瞬间生效，极大提升迭代效率。

- 支持类(包括代理类)的属性和方法变动、Spring、Solon、Mybatis等主流框架
- 支持中间件工具类

详细操作使用详见 [文档](https://debug-tools.cc/guide/method/hot-reload.html)

## 热部署

传统的部署流程一般为提交代码->拉取代码->打包->部署->重启项目后才能让编写的代码生效。

而热部署可以跳过这繁琐的流程，开发者修改代码后无需手动触发打包或重启服务，应用即可实时加载新逻辑并运行，极大缩短反馈周期。

我们开发/测试等环境引入热部署后，团队整体开发效率可提升大大提升，尤其适用于敏捷开发模式下的高频迭代场景。

详细操作使用详见 [文档](https://debug-tools.cc/guide/method/hot-deploy.html)

## SQL语句与耗时

不修改任何应用代码的情况下，可以打印执行的SQL语句、耗时、执行历史。

详细操作使用详见 [文档](https://debug-tools.cc/guide/method/sql.html)

## 搜索HttpUrl

通过给定的URL信息直接跳转到相应的方法定义位置。

内置强大的URL信息提取功能，各种形式的URL都能精准定位。

详细操作使用详见 [文档](https://debug-tools.cc/guide/method/search-http.html)

## groovy

Groovy 天然支持 java 语法，执行 Groovy 脚本，可以运行代码获取或修改附着应用信息调试。

Groovy 脚本会在目标 JVM 内执行，可以访问目标应用中的 Java 类、Spring Bean，以及 DebugTools 提供的 内置函数。

详细操作使用详见 [文档](https://debug-tools.cc/guide/method/groovy-execute.html)

# 支持这个项目

本项目完全开源，实现复杂且需要兼容情况较多，如果这个项目帮你节省了开发时间，不妨点个⭐Star，你的支持是我更新的动力。

> 喝杯咖啡 ☕️ 或者来杯奶茶 🧋，让作者更有精神，写出更棒的代码！

<div style="display:flex; gap:12px;">
  <img src="/images/39fb4dee427ef71afcd03793b0d61747.jpg" width="220" />
  <img src="/images/531ac581e7d2a861ed683bbcbf548dc2.jpg" width="220" />
</div>

# 社区

本开源项目已连接并认可[LINUX DO 社区](https://linux.do)