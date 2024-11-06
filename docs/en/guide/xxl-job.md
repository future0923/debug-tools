# xxl-job parameters

`DebugTools` can be used to quickly call task methods without relying on the server side of [xxl-job](https://github.com/xuxueli/xxl-job).

If we write a task method (as shown below) through the `@XxlJob` annotation, we can call this task method through `DebugTools`.

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

If you get the parameters passed by **xxl-job server** through `XxlJobHelper.getJobParam()` or `XxlJobContext.getXxlJobContext()`, you need to set the parameters in the debug panel of DebugTools. DebugTools will automatically build a `XxlJobContext` context object and set the parameters to the context.

**For example**: We set the parameter to `DebugTools`.

![xxl_job_quick_debug](/images/xxl_job_quick_debug.png){v-zoom}

Click Run and view the method return value through the **Debug** method. You can see that the passed parameter DebugTools can be obtained through the context.

![xxl_job_debug_result](/images/xxl_job_debug_result.png){v-zoom}

::: tip
When you click the `Run` button to call, DebugTools will record the debug parameter information.  
When you wake up the debug panel next time, the parameter information of the last debug will be automatically filled in.  
When you use `Execute Last` to [quickly call the last request](./execute-last), the parameter information of the last debug will also be carried.
:::