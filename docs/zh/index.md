---
# https://vitepress.dev/reference/default-theme-home-page
layout: home

hero:
  name: "DebugTools"
  text: "一款Java开发调试的IntelliJ IDEA插件"
  tagline: 热重载、快速调用任意Java方法(本地/远程)、搜索HttpUrl跳转代码定义、打印SQL语句与耗时、执行Groovy脚本
  actions:
    - theme: brand
      text: 什么是 DebugTools?
      link: /zh/guide/introduction
    - theme: alt
      text: 快速开始
      link: /zh/guide/quick-start
    - theme: alt
      text: GitHub
      link: https://github.com/future0923/debug-tools
  image:
    src: /logo.webp
    alt: DebugTools

features:
  - icon:
      src: /icon/hotswap_on.svg
    title: 热重载
    details: 无需重启应用即可让编写的代码生效，支持类(包括代理类)的属性和方法变动、SpringBoot、MybatisPlus等。
    link: /zh/guide/hot-reload
  - icon: 🚀
    title: 调用任意Java方法
    details: 快捷调用任意Java方法，如静态方法、实例方法、通过Spring管理的Bean的方法(Dubbo、XxlJob、MQ等)、Mybatis Mapper方法等，支持传递header参数信息进行鉴权，支持传递XxlJob参数进行任务执行。
    link: /zh/guide/attach-local
  - icon: 🔍
    title: 搜索HttpUrl
    details: 搜索 http 地址以直接跳转到相应的方法定义。
    link: /zh/guide/search-http
  - icon: 👀
    title: 打印SQL语句与耗时
    details: 格式化打印MySQL、PostgreSQL、SQLServer、ClickHouse、Oracle语句，并输出执行时间。
    link: /zh/guide/sql
  - icon: 🤔 
    title: 执行Groovy脚本
    details: 依托附着应用，可以执行编写Groovy脚本及对附着应用调试。
    link: /zh/guide/groovy-execute
---