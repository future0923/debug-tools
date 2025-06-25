---
# https://vitepress.dev/reference/default-theme-home-page
layout: home

hero:
  name: "DebugTools"
  text: 'Java development and debugging plugin integrated into IntelliJ IDEA'
  tagline: "Focus on improving development efficiency and shortening debugging cycles"
  actions:
    - theme: brand
      text: What is DebugTools?
      link: /en/guide/introduction
    - theme: alt
      text: Quickstart
      link: /en/guide/install
    - theme: alt
      text: GitHub
      link: https://github.com/java-hot-deploy/debug-tools
    - theme: alt
      text: Gitee
      link: https://gitee.com/future94/debug-tools
  image:
    src: /logo.webp
    alt: DebugTools

features:
  - icon:
      src: /icon/hot_deployment.svg
    title: Hot Deploy
    details: The traditional deployment process is generally to submit code -> pull code -> package -> deploy -> restart the project before the written code can take effect. Hot deployment can skip this tedious process. After the developer modifies the code, there is no need to manually trigger packaging or restart the service. The application can load the new logic in real time and run, greatly shortening the feedback cycle. After we introduced hot deployment in development/testing environments, the overall development efficiency of the team can be greatly improved, especially for high-frequency iteration scenarios in agile development mode.
    link: /en/guide/hot-deploy
  - icon:
      src: /icon/hotswap_on.svg
    title: Hot Reload
    details: Traditionally, when writing code, you need to restart the application to make it effective. However, hot reload can make the written code effective immediately without restarting the application, making the code changes written by developers take effect instantly, greatly improving iteration efficiency. Supports changes to properties and methods of classes (including proxy classes), Spring, Solon, Mybatis and other mainstream frameworks. At the same time, it is compatible with multiple JDK versions such as jdk8, jdk11, jdk17, jdk21, etc.
    link: /en/guide/hot-reload
  - icon: 🚗
    title: Call any Java method
    details: Quickly call any Java method (local/remote), such as static methods, instance methods, methods of Beans managed by Spring (Dubbo, XxlJob, MQ, etc.), Mybatis Mapper methods, etc., support multi-dimensional and multi-type parameter passing, passing header parameter information for authentication, passing XxlJob parameters for task execution, etc. There is no need to write test cases or call from API tools layer by layer, which greatly saves developers' debugging time.
    link: /en/guide/attach-local
  - icon: 🤔
    title: Execute Groovy script
    details: With the attached application, you can write Groovy scripts and debug the attached application. You can quickly obtain application information and execute the corresponding functional code. It also has built-in common Groovy functions to help developers locate application problems.
    link: /en/guide/groovy-execute
  - icon: 👀
    title: Printing SQL statements and time consuming
    details: Without modifying any code or configuration, you can format and print the SQL statements that are actually run and output the execution time. Supports any database connected via JDBC, such as MySQL, PostgreSQL, SQLServer, ClickHouse, Oracle, Sqlite, etc.
    link: /en/guide/sql
  - icon: 🔍
    title: Search Http Url
    details: Jump directly to the corresponding method definition location through the given URL information. Built-in powerful URL information extraction function, all forms of URLs can be accurately located.
    link: /en/guide/search-http


---