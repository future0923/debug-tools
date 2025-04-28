package io.github.future0923.debug.tools.extension.xxljob;

import com.xxl.job.core.context.XxlJobContext;
import com.xxl.job.core.log.XxlJobFileAppender;
import io.github.future0923.debug.tools.base.utils.DebugToolsStringUtils;

import java.util.Date;

/**
 * @author future0923
 */
public class XxlJobEnvUtil {

    public static void setXxlJobParam(String jobParam) {
        String logFileName = XxlJobFileAppender.makeLogFileName(new Date(), -1);
        XxlJobContext xxlJobContext = new XxlJobContext(-1, jobParam, logFileName, 1, 1);
        if (DebugToolsStringUtils.isNotBlank(jobParam)) {
            XxlJobContext.setXxlJobContext(xxlJobContext);
        } else {
            XxlJobContext.setXxlJobContext(null);
        }
    }
}
