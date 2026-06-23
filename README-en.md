<br/>

<p align="center">
    <a href="https://github.com/future0923/debug-tools/blob/main/README.md">
      中文
    </a>
    |
    <a href="https://github.com/future0923/debug-tools/blob/main/README-en.md" >
      English
    </a>
    |
    <a href="https://linux.do" >
    LinuxDO Friend Link
    </a>
</p>

<p align="center">
  <strong>A plug-in integrated into IntelliJ IDEA, focusing on improving development efficiency and shortening debugging cycles</strong>
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

# Features

## Call Any Java Method

DebugTools supports directly calling Java methods in your project from IntelliJ IDEA. Both local and remote calls are supported, and multiple applications and methods can be called at the same time.

You do not need to add a Controller, write temporary test cases, or trigger the call layer by layer from a request entry. Select the target method, fill in the parameters, and execute it to quickly verify business logic.

**Use Cases**
- Call ordinary class methods, static methods, Spring Bean methods, Dubbo services, XXL-JOB tasks, MQ consumer methods, MyBatis Mapper methods, streaming SSE responses, and more.
- Verify the return result of a Service, Mapper, utility class, or component method.
- Skip outer processes such as Controller, gateway, authentication, and parameter assembly, and debug core business logic directly.
- Quickly call Spring-managed Bean methods, including methods inside frameworks such as Dubbo, XXL-JOB, and MQ.
- Call MyBatis Mapper methods to verify SQL parameters, query results, and execution time.
- Pass complex parameters, Header parameters, or XXL-JOB parameters to methods.
- Remotely call methods in the target application and locate issues with remote Debug.
- Combine with **hot reload** to verify changes immediately after modifying code.

For detailed usage, see the [documentation](https://debug-tools.cc/en/guide/method/quick-start.html).

## Hot Reload

In traditional development, you need to restart the application before code changes take effect. Hot reload lets code changes take effect immediately **without restarting the application**, greatly improving iteration efficiency.

- Supports property and method changes for classes (including proxy classes), Spring, Solon, MyBatis, and other mainstream frameworks.
- Supports middleware utility classes.

For detailed usage, see the [documentation](https://debug-tools.cc/en/guide/method/hot-reload.html).

## Hot Deployment

The traditional deployment process is usually submit code -> pull code -> package -> deploy -> restart the project before code changes take effect.

Hot deployment skips this tedious process. After developers modify code, the application can load the new logic in real time and run it without manually triggering packaging or restarting the service, greatly shortening the feedback cycle.

After introducing hot deployment in development and testing environments, overall team development efficiency can be greatly improved, especially in agile development scenarios with frequent iterations.

For detailed usage, see the [documentation](https://debug-tools.cc/en/guide/method/hot-deploy.html).

## SQL Statements and Execution Time

Without modifying any application code, you can print executed SQL statements, execution time, and execution history.

For detailed usage, see the [documentation](https://debug-tools.cc/en/guide/method/sql.html).

## Search HttpUrl

Jump directly to the corresponding method definition location through the given URL information.

The built-in URL information extraction feature can accurately locate URLs in various formats.

For detailed usage, see the [documentation](https://debug-tools.cc/en/guide/method/search-http.html).

## Groovy

Groovy naturally supports Java syntax. By executing Groovy scripts, you can run code to obtain or modify information in the attached application for debugging.

Groovy scripts run inside the target JVM and can access Java classes, Spring Beans, and built-in functions provided by DebugTools.

For detailed usage, see the [documentation](https://debug-tools.cc/en/guide/method/groovy-execute.html).

# Support This Project

This project is fully open source, complex to implement, and needs to handle many compatibility cases. If it has helped you save development time, please consider giving it a Star. Your support motivates me to keep updating it.

> Buy me a coffee or milk tea so the author can stay energized and write better code!

<div style="display:flex; gap:12px;">
  <img src="/images/39fb4dee427ef71afcd03793b0d61747.jpg" width="220" />
  <img src="/images/531ac581e7d2a861ed683bbcbf548dc2.jpg" width="220" />
</div>

# Contact Me

- QQ group: [518757118](https://qm.qq.com/cgi-bin/qm/qr?k=ztAKCGYQkhbTnwlgcumIUbEKOtbJTQ4h&jump_from=webapi&authKey=uLgjTI6vb2aVmmQF3hKRmTSLCJlO6ku0scrmMXWaHagtO3aztN+ZJMOs7xeHNuKO)
- WeChat

<img src="images/wechat.png" width="200" height="200" v-zoom  alt=""/>
