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

import java.lang.reflect.Constructor;
import java.util.Date;

/**
 * @author future0923
 */
@SuppressWarnings("JavaReflectionMemberAccess")
public class XxlJobEnvUtil {

    public static void setXxlJobParam(String jobParam) throws Exception {
        if (DebugToolsStringUtils.isBlank(jobParam)) {
            XxlJobContext.setXxlJobContext(null);
            return;
        }
        String logFileName = XxlJobFileAppender.makeLogFileName(new Date(), -1);
        XxlJobContext.setXxlJobContext(createXxlJobContext(jobParam, logFileName));
    }

    private static XxlJobContext createXxlJobContext(String jobParam, String logFileName) throws Exception {
        try {
            Constructor<XxlJobContext> constructor = XxlJobContext.class.getConstructor(long.class, String.class, long.class, long.class, String.class, int.class, int.class);
            return constructor.newInstance(-1L, jobParam, -1L, System.currentTimeMillis(), logFileName, 1, 1);
        } catch (NoSuchMethodException ignored) {
            return createXxlJobContextFor2x(jobParam, logFileName);
        }
    }

    private static XxlJobContext createXxlJobContextFor2x(String jobParam, String logFileName) throws Exception {
        Constructor<XxlJobContext> constructor = XxlJobContext.class.getConstructor(long.class, String.class, String.class, int.class, int.class);
        return constructor.newInstance(-1L, jobParam, logFileName, 1, 1);
    }
}
