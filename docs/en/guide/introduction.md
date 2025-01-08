
# What is DebugTools？ {#introduction}

DebugTools is a Java development debugging tool based on IntelliJ IDEA extension/plugins. It provides hot reload, functions such as shortcut calling any Java method, printing and executing SQL statements and time-consuming, and executing Groovy scripts to debug target applications.

<div class="tip custom-block" style="padding-top: 8px">

Just want to try it out? Skip to the [Quickstart](./quick-start)。

</div>

## Use Cases {#use-cases}

- Hot reload allows the written code to take effect without restarting the application. It supports changes to properties and methods of classes (including proxy classes), SpringBoot, MybatisPlus, etc.
- You can quickly call any Java methods without sophisticated business validation calls coming in from the Controller layer like the API.
- You can also call the remote method to trigger the remote method to run and cooperate with remote debugging to achieve the purpose of remote debugging.
- The method can be called directly to test whether the result is achieved without writing a test case.
- Without modifying any application code, you can print the executed SQL statements with time.
- Client-side methods can be invoked without going through the xxl-job Server.
- Execute the Groovy script, you can run the code to get or modify the attached application information to debug.
- ......