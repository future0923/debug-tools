# xxl-job参数

`DebugTools` 可以不依赖[xxl-job](https://github.com/xuxueli/xxl-job)的server端而快捷调用的任务方法。

如我们通过 `@XxlJob` 注解等方式编写了一个任务方法（如下），那么我们可以通过 `DebugTools` 调用这个任务方法。

```java
package io.github.future0923.debug.tools.test.application.job;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.stereotype.Component;

/**
 * @author future0923
 */
@Component
public class TestJob {

    @XxlJob("testJobParam")
    public ReturnT<String> testJobParam() {
        String jobParam = XxlJobHelper.getJobParam();
        ReturnT<String> returnT = new ReturnT<>();
        returnT.setContent(jobParam);
        return returnT;
    }
}
```

如果你通过 `XxlJobHelper.getJobParam()` 或者 `XxlJobContext.getXxlJobContext()` 来获取 **xxl-job server** 端传入的参数，那么你需要在 DebugTools 的调试面板中设置参数。DebugTools 会自动构建一个 `XxlJobContext` 上下文对象并将参数设置到上下文中。

**如**：我们设置参数为 DebugTools。

![xxl_job_quick_debug](/images/xxl_job_quick_debug.png){v-zoom}

点击运行，通过 **Debug** 方式查看方法返回值可以看到，传入的参数 DebugTools 已经可以通过上下文获取到。

![xxl_job_debug_result](/images/xxl_job_debug_result.png){v-zoom}

::: tip
当点击 `Run` 按钮调用之后，DebugTools 会记录调试传入参数信息。  
当下次唤醒调试面板时，会自动填充上次调试的参数信息。  
通过 `Execute Last` [快速调用上一次请求](./execute-last)时，也会携带上次调试的参数信息。
:::