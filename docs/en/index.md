---
# https://vitepress.dev/reference/default-theme-home-page
layout: home

hero:
  name: "DebugTools"
  text: "Java debug tool based on IntelliJ IDEA plugins"
  tagline: Quickly call Java methods (local/remote), print SQL statements and time consuming, execute Groovy scripts
  actions:
    - theme: brand
      text: What is DebugTools?
      link: /guide/introduction
    - theme: alt
      text: Quickstart
      link: /guide/quick-start
    - theme: alt
      text: GitHub
      link: https://github.com/future0923/debug-tools

features:
  - title: Call any Java method
    details: Quickly call any Java methods, such as static methods, instance methods, methods of Spring-managed beans (Dubbo, XxlJob, MQ, etc.), Mybatis Mapper methods, etc., support passing header parameter information for authentication, support passing XxlJob parameters for task execution.
  - title: Printing SQL statements and time consuming
    details: Format print MySQL, PostgreSQL, SQLServer, ClickHouse, Oracle statements and output execution time.
  - title: Execute Groovy script
    details: Depending on the attachment application, you can execute Groovy scripting and debug the attachment application.
---