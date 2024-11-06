# 运行结果 {#run-result}

![tostring_result](/images/tostring_result.png){v-zoom}

## 公共信息

### Class loader

显示调用时选择的类加载器信息。

### Current class

显示调用时选择的类信息。

### Current method

显示调用时选择的方法信息。

### Parameter types

显示调用时选择的方法参数信息。

## 正常运行 {#normal-run}

DebugTools 提供 `toString`、`json` 和 `debug` 三种方式查看运行结果。

### toString {#toString}

通过调用 `toString()` 方法的方式展示 `快捷调用方法` 和 [Groovy脚本](./groovy-execute) 的运行结果。

![tostring_result](/images/tostring_result.png){v-zoom}

::: warning 特殊情况

- 如果无返回值（Void），则展示

```text
Void
```

- 返回结果为 Null，则展示

```text
NULL
```

:::

### json {#json}

将 `快捷调用方法` 和 [Groovy脚本](./groovy-execute) 的运行结果转为Json格式进行展示。

![json_result](/images/json_result.png){v-zoom}

::: warning 特殊情况

- 如果无返回值（Void），则展示

```json
{
  "result": "Void"
}
```

- 返回结果为 Null，则展示

```json
{
  "result": "Null"
}
```

:::

### debug {#debug}

将 `快捷调用方法` 和 [Groovy脚本](./groovy-execute) 的运行结果转为类似于 Idea Debug 的样式进行展示。

![debug-result](/images/debug_result.png){v-zoom}

**快捷操作**：在选中行下右键可以唤醒菜单

- `Copy Name`：复制选中行的Name，如下图复制的就是 `settlementBatchNo`。
- `Copy Value`：复制选中行的Value，如下图复制的就是 `2024002`。在选中行不唤醒菜单直接点击 `Ctrl+C` 也可以快速复制Value（同Idea一致）。

![debug_result_opt](/images/debug_result_opt.png){v-zoom}

> [!IMPORTANT]  
> 当结果 **无返回值（Void）** 或者 **返回结果为 Null** 时不支持 debug 方式查看

## 异常情况

DebugTools 提供 `console` 和 `debug` 两种方式查看运行结果。

### console {#console}

DebugTools 提供类似 Idea 控制台一样的日志输出，可以查看异常信息，点击蓝色信息可以快速定位到异常代码位置。

![console_exception.png](/images/console_exception.png){v-zoom}

### debug

DebugTools 提供类似 Idea Debug 的日志输出，可以查看异常信息，功能同正常的 `debug` 一样。

![debug_exception.png](/images/debug_exception.png){v-zoom}