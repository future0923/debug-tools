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
package io.github.future0923.debug.tools.hotswap.core.config;

import io.github.future0923.debug.tools.base.logging.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Locale;
import java.util.Properties;

import static java.lang.Boolean.parseBoolean;

/**
 * 配置package的日志级别（LOGGER.my.package=LEVEL）写入{@link PluginConfiguration#properties}配置
 */
public class LogConfigurationHelper {
    private static final Logger LOGGER = Logger.getLogger(LogConfigurationHelper.class);

    public static final String LOGGER_PREFIX = "LOGGER";
    public static final String DATETIME_FORMAT = "LOGGER_DATETIME_FORMAT";
    private static final String LOGFILE = "LOGFILE";
    private static final String LOGFILE_APPEND = "LOGFILE.append";

    /**
     * 配置package的日志级别（LOGGER.my.package=LEVEL）
     */
    public static void configureLog(Properties properties) {
        for (String property : properties.stringPropertyNames()) {
            if (property.startsWith(LOGGER_PREFIX)) {
                if (property.startsWith(DATETIME_FORMAT)) {
                    String dateTimeFormat = properties.getProperty(DATETIME_FORMAT);
                    if (dateTimeFormat != null && !dateTimeFormat.isEmpty()) {
                        Logger.setDateTimeFormat(dateTimeFormat);
                    }
                } else {
                    String classPrefix = getClassPrefix(property);
                    Logger.Level level = getLevel(property, properties.getProperty(property));

                    if (level != null) {
                        if (classPrefix == null)
                            Logger.setLevel(level);
                        else
                            Logger.setLevel(classPrefix, level);
                    }
                }
            } else if (property.equals(LOGFILE)) {
                String logfile = properties.getProperty(LOGFILE);
                boolean append = parseBoolean(properties.getProperty(LOGFILE_APPEND, "false"));
                try {
                    PrintStream ps = new PrintStream(new FileOutputStream(new File(logfile), append));
                    Logger.getHandler().setPrintStream(ps);
                } catch (FileNotFoundException e) {
                    LOGGER.error("Invalid configuration property {} value '{}'. Unable to create/open the file.",
                            e, LOGFILE, logfile);
                }
            }
        }
    }

    // resolve level from enum
    private static Logger.Level getLevel(String property, String levelName) {
        try {
            return Logger.Level.valueOf(levelName.toUpperCase(Locale.ENGLISH));
        } catch (IllegalArgumentException e) {
            LOGGER.warning("Invalid configuration value for property '{}'. Unknown LOG level '{}'.", property, levelName);
            return null;
        }
    }

    // get package name from logger
    private static String getClassPrefix(String property) {
        if (property.equals(LOGGER_PREFIX)) {
            return null;
        } else {
            return property.substring(LOGGER_PREFIX.length() + 1);
        }
    }
}
