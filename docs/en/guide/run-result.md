# Run Results {#run-result}

![tostring_result](/images/tostring_result.png){v-zoom}

## Public Information

### Class loader

Shows the class loader information selected when calling.

### Current class

Shows the class information selected when calling.

### Current method

Shows the method information selected when calling.

### Parameter types

Shows the method parameter information selected when calling.

## Normal run {#normal-run}

DebugTools provides three ways to view the run results: `toString`, `json`, and `debug`.

### toString {#toString}

Shows the run results of `Quick call method` and [Groovy script](./groovy-execute) by calling the `toString()` method.

![tostring_result](/images/tostring_result.png){v-zoom}

::: warning Special case

- If there is no return value (Void), then display

```text
Void
```

- If the return result is Null, then display

```text
NULL
```

:::

### json {#json}

Convert the running results of `Quick Call Method` and [Groovy Script](./groovy-execute) to Json format for display.

![json_result](/images/json_result.png){v-zoom}

::: warning Special case

- If there is no return value (Void), then display

```json
{
  "result": "Void"
}
```

- If the return result is Null, then display

```json
{
 "result": "Null"
}
```

:::

### debug {#debug}

Convert the running results of `Quick Call Method` and [Groovy Script](./groovy-execute) to a style similar to Idea Debug for display.

![debug-result](/images/debug_result.png){v-zoom}

**Quick operation**: Right-click under the selected row to wake up the menu

- `Copy Name`: Copy the Name of the selected row. The following figure copies `settlementBatchNo`.
- `Copy Value`: copy the value of the selected row. The following figure shows `2024002`. You can also quickly copy the value by clicking `Ctrl+C` on the selected row without waking up the menu (same as Idea).

![debug_result_opt](/images/debug_result_opt.png){v-zoom}

> [!IMPORTANT]
> When the result is **Void** or **Null**, debug mode viewing is not supported

## Abnormal Situation

DebugTools provides `console` and `debug` to view the running results.

### console {#console}

DebugTools provides log output similar to Idea console, you can view abnormal information, click the blue information to quickly locate the abnormal code location

![console_exception.png](/images/console_exception.png){v-zoom}

### debug

DebugTools provides log output similar to Idea Debug, which can view exception information and has the same function as normal `debug`.

![debug_exception.png](/images/debug_exception.png){v-zoom}