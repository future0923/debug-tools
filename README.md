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
    <img src="https://img.shields.io/github/stars/future0923/debug-tools.svg?style=flat&label=stars" alt="github stars"/>
    <img src="https://img.shields.io/github/forks/future0923/debug-tools.svg?style=flat&label=forks" alt="github forks"/>
    <img src="https://img.shields.io/github/contributors/future0923/debug-tools.svg?style=flat&label=contributors&color=blue" alt="github contributors"/>
    <img src='https://gitee.com/future94/debug-tools/badge/star.svg?theme=dark' alt='star' />
    <img src='https://gitee.com/future94/debug-tools/badge/fork.svg?theme=dark' alt='fork' />
    <img src="https://img.shields.io/badge/JDK-8-blue.svg" alt="jdk 8" />
    <img src="https://img.shields.io/badge/JDK-11-blue.svg" alt="jdk 11" />
    <img src="https://img.shields.io/badge/JDK-17-blue.svg" alt="jdk 17" />
    <img src="https://img.shields.io/badge/JDK-21-blue.svg" alt="jdk 21" />
    <img src="https://img.shields.io/badge/JDK-25-blue.svg" alt="jdk 25" />
    <img src="https://img.shields.io/badge/QQ群-518757118-blue.svg" alt="518757118" />
    <img src="https://img.shields.io/badge/Email-future94@qq.com-blue.svg" alt="518757118" />
</p>

## 功能

- [秒级热部署](https://debug-tools.cc/guide/hot-deploy.html)：传统的部署流程一般为提交代码->拉取代码->打包->部署->重启项目后才能让编写的代码生效。而热部署可以跳过这繁琐的流程，开发者修改代码后无需手动触发打包或重启服务，应用即可实时加载新逻辑并运行，极大缩短反馈周期。我们开发/测试等环境引入热部署后，团队整体开发效率可提升大大提升，尤其适用于敏捷开发模式下的高频迭代场景。
- [秒级热重载](https://debug-tools.cc/guide/hot-reload.html)：传统编写代码时，需要重启应用才能生效，而热重载可以在不重启应用下让编写的代码生效立刻，让开发者编写的代码改动瞬间生效，极大提升迭代效率。支持类(包括代理类)的属性和方法变动、Spring、Solon、Mybatis等主流框架。
- [调用任意Java方法](https://debug-tools.cc/guide/attach-local.html)：无需像 Api 一样从 Controller 层进过复杂的业务校验后一层层调用进来，方法可以直接调用测试是否达到结果，不用写测试用例。结合热重载可以快速修改，无需重启。
- [调用远程方法](https://debug-tools.cc/guide/attach-remote.html)：触发远程方法运行配合远程debug达到远程调试的目的，通过热部署可以快捷调试应用。
- [SQL语句与耗时](https://debug-tools.cc/guide/sql.html)：不修改任何应用代码的情况下，可以打印执行的SQL语句与耗时。
- [搜索HttpUrl](https://www.debug-tools.cc/guide/search-http.html)：通过给定的URL信息直接跳转到相应的方法定义位置。内置强大的URL信息提取功能，各种形式的URL都能精准定位。
- [xxl-job](https://debug-tools.cc/guide/xxl-job.html)：不通过 xxl-job Server可以调用 client 端方法，并支持上下文传参。
- [groovy](https://debug-tools.cc/guide/groovy-execute.html)：执行 Groovy 脚本，可以运行代码获取或修改附着应用信息调试。

## 效果演示

- 热部署

![hot_deploy.gif](/images/gif/hot_deploy.gif)

- 热重载

![hot_reload.gif](/images/gif/hot_reload.gif)

- 调用任意Java方法

![quick_debug.gif](/images/gif/quick_debug.gif)

- 搜索HttpUrl

![search_url.gif](/images/gif/search_url.gif)

## 支持这个项目

本项目完全开源，实现复杂且需要兼容情况较多，如果这个项目帮你节省了开发时间，不妨点个⭐Star，你的支持是我更新的动力。

> 喝杯咖啡 ☕️ 或者来杯奶茶 🧋，让作者更有精神，写出更棒的代码！

<div style="display:flex; gap:12px;">
  <img src="/images/39fb4dee427ef71afcd03793b0d61747.jpg" width="220" />
  <img src="/images/531ac581e7d2a861ed683bbcbf548dc2.jpg" width="220" />
</div>

## 联系我

- QQ群：[518757118](https://qm.qq.com/cgi-bin/qm/qr?k=ztAKCGYQkhbTnwlgcumIUbEKOtbJTQ4h&jump_from=webapi&authKey=uLgjTI6vb2aVmmQF3hKRmTSLCJlO6ku0scrmMXWaHagtO3aztN+ZJMOs7xeHNuKO)
- 微信

<img src="images/wechat.png" width="200" height="200" v-zoom  alt=""/>