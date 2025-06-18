# 打印执行SQL与耗时 {#print_sql}

## 用途 {#purpose}

**痛点：** 当我们测试业务的时候，需要查看实际执行的SQL与耗时时间，很多上层的驱动会有多种开启打印的方式，很多都需要修改代码或修改配置打印。

DebugTools 通过在 [jdbc](https://www.oracle.com/database/technologies/appdev/jdbc.html) 层通过 `修改数据库驱动字节码` 实现在运行时打印SQL与耗时，从而避免上层数据库链接池的不同影响SQL的打印。

**理论上支持所有通过Jdbc链接的数据库驱动：**

- [MySQL](https://www.mysql.com/)
- [PostgreSQL](https://www.postgresql.org/)
- [SQLServer](https://www.microsoft.com/en-us/sql-server/)
- [ClickHouse](https://clickhouse.com/)
- [Oracle](https://www.oracle.com/database/technologies/)
- ...

## 使用 {#use}

### Idea {#idea}

在 `setting -> Other Settings -> DebugTools` 中配置开启或者关闭

![print_sql_setting](/images/print_sql_setting.png){v-zoom}

成功会在日志中输出 `Print xxx(mysql/oracle/...) log bytecode enhancement successful`

![print_sql_success](/images/print_sql_success.png){v-zoom}

**打印效果如下：**

```text
Execute consume Time: 3 ms; Execute SQL: 
SELECT
    id,
    name,
    age,
    version 
FROM
    dp_user 
WHERE
    id=1
```

::: tip 注意

由于 DebugTools 是通过字节码的方式打印SQL，所以修改配置后需要重新启动应用才能生效。

:::

### Agent {#agent}

通过使用 `java -javaagent:/path/to/your/debug-tools-agent-{version}.jar -jar your-app.jar` 启动应用

::: details agent下载地址

https://download.debug-tools.cc/debug-tools-agent.jar

:::


## 警告 {#warning}

> [!WARNING] 最好不要在生产环境中使用
> 因为 DebugTools 通过 **修改数据库驱动字节码** 来实现SQL的打印，可能出现兼容不到位或者其他考虑不到的情况，生产环境可能存在风险。
