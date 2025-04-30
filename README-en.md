<br/>

<div style="text-align: center;">
    <a href="https://github.com/future0923/debug-tools/blob/main/README.md">
      中文
    </a>
    |
    <a href="https://github.com/future0923/debug-tools/blob/main/README-en.md" >
      English
    </a>
</div>

<p style="text-align: center;">
  <strong>A plug-in integrated into IntelliJ IDEA, focusing on improving development efficiency and shortening debugging cycles</strong>
</p>

<p style="text-align: center;">
  <a target="_blank" href="https://debug-tools.cc">https://debug-tools.cc</a>
</p>

<div style="text-align: center;">
    <a href="https://debug-tools.cc">
      <img src="https://img.shields.io/badge/文档-简体中文-blue.svg" alt="简体中文文档" />
    </a>
    <a href="https://debug-tools.cc/en" >
      <img src="https://img.shields.io/badge/Document-English-blue.svg" alt="EN docs" />
    </a>
    <a target="_blank" href="https://deepwiki.com/future0923/debug-tools" >
      <img src="https://img.shields.io/badge/DeepWiki-English-blue.svg" alt="Deepwiki docs" />
    </a>
</div>

<div style="text-align: center;">
    <a target="_blank" href="LICENSE">
        <img src="https://img.shields.io/badge/License-Apache%202.0-blue.svg?label=license" alt="apache 2.0" />
    </a>
    <a target="_blank" href='https://plugins.jetbrains.com/plugin/24463-debugtools'>
        <img src="https://img.shields.io/jetbrains/plugin/d/24463?style=flat&color=blue" alt="github contributors"/>
    </a>
    <a href='https://github.com/future0923/debug-tools'>
        <img src="https://img.shields.io/github/stars/future0923/debug-tools.svg?style=flat&label=stars" alt="github stars"/>
    </a>
    <a href='https://github.com/future0923/debug-tools'>
        <img src="https://img.shields.io/github/forks/future0923/debug-tools.svg?style=flat&label=forks" alt="github forks"/>
    </a>
    <a href='https://github.com/future0923/debug-tools'>
        <img src="https://img.shields.io/github/contributors/future0923/debug-tools.svg?style=flat&label=contributors&color=blue" alt="github contributors"/>
    </a>
    <a target="_blank" href="https://debug-tools.cc/guide/install#jdk8">
        <img src="https://img.shields.io/badge/JDK-8-blue.svg" alt="jdk 8" />
    </a>
    <a target="_blank" href="https://debug-tools.cc/guide/install#jdk11">
        <img src="https://img.shields.io/badge/JDK-11-blue.svg" alt="jdk 11" />
    </a>
    <a target="_blank" href="https://debug-tools.cc/guide/install#jdk17-21">
        <img src="https://img.shields.io/badge/JDK-17-blue.svg" alt="jdk 17" />
    </a>
    <a target="_blank" href="https://debug-tools.cc/guide/install#jdk17-21">
        <img src="https://img.shields.io/badge/JDK-21-blue.svg" alt="jdk 21" />
    </a>
    <a target="_blank" href="https://qm.qq.com/cgi-bin/qm/qr?k=ztAKCGYQkhbTnwlgcumIUbEKOtbJTQ4h&jump_from=webapi&authKey=uLgjTI6vb2aVmmQF3hKRmTSLCJlO6ku0scrmMXWaHagtO3aztN+ZJMOs7xeHNuKO">
        <img src="https://img.shields.io/badge/QQ群-518757118-blue.svg" alt="518757118" />
    </a>
    <a target="_blank" href="mailto:future94@qq.com">
        <img src="https://img.shields.io/badge/Email-future94@qq.com-blue.svg" alt="518757118" />
    </a>
</div>

## 功能

- [Hot deployment in seconds](https://debug-tools.cc/en/guide/hot-deploy.html)：The traditional deployment process is generally to submit code -> pull code -> package -> deploy -> restart the project before the written code can take effect. Hot deployment can skip this tedious process. After the developer modifies the code, there is no need to manually trigger packaging or restart the service. The application can load the new logic in real time and run, greatly shortening the feedback cycle. After we introduced hot deployment in development/testing environments, the overall development efficiency of the team can be greatly improved, especially for high-frequency iteration scenarios in agile development mode.
- [Hot reload in seconds](https://debug-tools.cc/en/guide/hot-reload.html): When writing code in the traditional way, you need to restart the application to make it effective. However, hot reload can make the written code effective immediately without restarting the application, so that the code changes written by the developer can take effect instantly, greatly improving the iteration efficiency. Supports changes in properties and methods of classes (including proxy classes), Spring, Mybatis and other mainstream frameworks.
- [Call any Java method](https://debug-tools.cc/en/guide/attach-local.html): There is no need to call it layer by layer after complex business verification from the Controller layer like API. The method can be directly called to test whether the result is achieved without writing test cases. Combined with hot reload, it can be modified quickly without restarting.
- [Call remote method](https://debug-tools.cc/en/guide/attach-remote.html): Trigger the remote method to run with remote debug to achieve the purpose of remote debugging. Through hot deployment, you can quickly debug the application.
- [SQL statements and time consumption](https://debug-tools.cc/en/guide/sql.html): Without modifying any application code, you can print the executed SQL statements and time consumption.
- [xxl-job](https://debug-tools.cc/en/guide/xxl-job.html): You can call client-side methods without going through xxl-job Server, and support context parameter passing.
- [groovy](https://debug-tools.cc/en/guide/groovy-execute.html): Execute Groovy scripts, you can run code to obtain or modify attached application information for debugging.

## Quick Start

### Installation

It is very easy to install DebugTools. You can quickly install it by following the installation [document](https://debug-tools.cc/en/guide/install.html#install-plugin).

### Usage

- [Hot Deployment](https://debug-tools.cc/en/guide/hot-deploy.html)
- [Hot Reload](https://debug-tools.cc/en/guide/hot-reload.html)
- [Calling Any Java Method](https://debug-tools.cc/en/guide/attach-local.html)
- [Calling Remote Method](https://debug-tools.cc/en/guide/attach-remote.html)
- [SQL Statements and Time](https://debug-tools.cc/en/guide/sql.html)
- [xxl-job](https://debug-tools.cc/en/guide/xxl-job.html)
- [groovy](https://debug-tools.cc/en/guide/groovy-execute.html)

## Support this project

This project is completely open source, complex to implement and requires many compatibility issues. If this project helps you save development time, please click <a target="_blank" href="https://github.com/future0923/debug-tools"><img src="https://img.shields.io/github/stars/future0923/debug-tools?style=flat&logo=GitHub" style="display: inline-block; vertical-align: middle;" /></a>. Your recognition will make more people discover it, and your support is my motivation to update.

If the project does not achieve the expected results, please submit <a target="_blank" href="https://github.com/future0923/debug-tools/issues"><img src="https://img.shields.io/github/issues-closed/future0923/debug-tools?style=flat&logo=github" style="display: inline-block; vertical-align: middle;" /></a> feedback, and I will deal with it as soon as possible.

If you are interested in DebugTools, you are sincerely invited to join the DebugTools open source project! Here, we work together to solve practical problems with code. Whether you are good at Java, Idea plug-in development, or are keen on front-end development, or have unique insights into architecture design and document writing, you can find a stage here. We encourage the collision of different ideas and look forward to your new ideas and solutions. The code of this project is completely open source. Let us work together to polish the project to be more perfect and leave our wonderful mark in the open source community!

## Contact me

- Issue: [issues](https://github.com/future0923/debug-tools/issues)
- Email: [future94@qq.com](mailto:future94@qq.com)
- QQ group: [518757118](https://qm.qq.com/cgi-bin/qm/qr?k=ztAKCGYQkhbTnwlgcumIUbEKOtbJTQ4h&jump_from=webapi&authKey=uLgjTI6vb2aVmmQF3hKRmTSLCJlO6ku0scrmMXWaHagtO3aztN+ZJMOs7xeHNuKO)
- WeChat

![wechat.png](docs/public/wechat.png)