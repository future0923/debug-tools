package io.github.future0923.debug.power.test.application.job;

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
