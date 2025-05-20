/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
