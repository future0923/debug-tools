/*
 * Copyright (C) 2024-2025 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
