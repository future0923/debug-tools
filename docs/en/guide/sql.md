# Print SQL execution and time {#print_sql}

## Purpose {#purpose}

**Pain point:** When we test the business, we need to view the actual SQL execution and time consumption. Many upper-level drivers have multiple ways to enable printing, and many of them require code modification or configuration modification for printing.

DebugTools uses [bytebuddy](https://bytebuddy.net/#/) to print SQL and time consumption at runtime by `modifying the database driver bytecode` at the [jdbc](https://www.oracle.com/database/technologies/appdev/jdbc.html) layer, thereby avoiding the impact of different upper-level database connection pools on SQL printing.

**Theoretically supports all database drivers connected through Jdbc:**

- [MySQL](https://www.mysql.com/)
- [PostgreSQL](https://www.postgresql.org/)
- [SQLServer](https://www.microsoft.com/en-us/sql-server/)
- [ClickHouse](https://clickhouse.com/)
- [Oracle](https://www.oracle.com/database/technologies/)
- ...

## Use {#use}

### Idea {#idea}

Configure to open or close in `setting -> Other Settings -> DebugTools`

![print_sql_setting](/images/print_sql_setting.png){v-zoom}

Success will output `Print xxx(mysql/oracle/...) log bytecode enhancement in the log successful`

![print_sql_success](/images/print_sql_success.png){v-zoom}

**The printing effect is as follows:**

```text
Execute consume Time: 3 ms; Execute SQL:Execute consume Time: 3 ms; Execute SQL: 
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

::: tip

Since DebugTools prints SQL in bytecode, you need to restart the application to take effect after modifying the configuration.

:::

### Agent {#agent}

Start the application by using `java -javaagent:/path/to/your/debug-tools-agent-{version}.jar -jar your-app.jar`

::: details agent download address

https://download.debug-tools.cc/debug-tools-agent.jar

:::

## Warning {#warning}

> [!WARNING] It is best not to use it in a production environment
> Because DebugTools implements SQL printing by **modifying the database driver bytecode**, there may be incompatibility or other unconsidered situations, and there may be risks in the production environment.