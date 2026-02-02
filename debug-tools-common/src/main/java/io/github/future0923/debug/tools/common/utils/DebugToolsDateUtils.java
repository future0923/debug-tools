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
package io.github.future0923.debug.tools.common.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author caoayu
 */
public class DebugToolsDateUtils {

    // DateTimeFormatter 是线程安全的，可以静态共享
    private static final List<DateTimeFormatter> LOCAL_DATE_TIME_FORMATTERS = new ArrayList<>();
    private static final List<DateTimeFormatter> LOCAL_DATE_FORMATTERS = new ArrayList<>();
    private static final List<DateTimeFormatter> LOCAL_TIME_FORMATTERS = new ArrayList<>();

    static {
        // 初始化LocalDateTime格式
        LOCAL_DATE_TIME_FORMATTERS.add(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LOCAL_DATE_TIME_FORMATTERS.add(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        LOCAL_DATE_TIME_FORMATTERS.add(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
        LOCAL_DATE_TIME_FORMATTERS.add(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));
        LOCAL_DATE_TIME_FORMATTERS.add(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        LOCAL_DATE_TIME_FORMATTERS.add(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        LOCAL_DATE_TIME_FORMATTERS.add(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));

        // 初始化LocalDate格式
        LOCAL_DATE_FORMATTERS.add(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LOCAL_DATE_FORMATTERS.add(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        LOCAL_DATE_FORMATTERS.add(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        LOCAL_DATE_FORMATTERS.add(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // 初始化LocalTime格式
        LOCAL_TIME_FORMATTERS.add(DateTimeFormatter.ofPattern("HH:mm:ss"));
        LOCAL_TIME_FORMATTERS.add(DateTimeFormatter.ofPattern("HH:mm"));
        LOCAL_TIME_FORMATTERS.add(DateTimeFormatter.ofPattern("HHmmss"));
        LOCAL_TIME_FORMATTERS.add(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
        LOCAL_TIME_FORMATTERS.add(DateTimeFormatter.ofPattern("HH:mm:ss.S"));
        LOCAL_TIME_FORMATTERS.add(DateTimeFormatter.ofPattern("HHmmssSSS"));
    }

    /**
     * 解析LocalDateTime，尝试多种格式
     */
    public static LocalDateTime parseLocalDateTime(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Date string cannot be null or empty");
        }

        // 1. 尝试解析为 LocalDateTime
        for (DateTimeFormatter formatter : LOCAL_DATE_TIME_FORMATTERS) {
            try {
                return LocalDateTime.parse(dateStr, formatter);
            } catch (Exception e) {
                // 忽略，尝试下一个
            }
        }

        // 2. 尝试解析为 LocalDate 并补全时间
        try {
            LocalDate localDate = parseLocalDate(dateStr);
            return localDate.atStartOfDay();
        } catch (Exception e) {
            // 忽略
        }

        throw new IllegalArgumentException("Unable to parse LocalDateTime: " + dateStr);
    }

    /**
     * 解析LocalDate，尝试多种格式
     */
    public static LocalDate parseLocalDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Date string cannot be null or empty");
        }
        for (DateTimeFormatter formatter : LOCAL_DATE_FORMATTERS) {
            try {
                return LocalDate.parse(dateStr, formatter);
            } catch (Exception e) {
                // 忽略
            }
        }
        throw new IllegalArgumentException("Unable to parse LocalDate: " + dateStr);
    }

    /**
     * 解析Date，底层复用 LocalDateTime 解析逻辑，解决线程安全问题
     */
    public static Date parseDate(String dateStr) {
        try {
            LocalDateTime localDateTime = parseLocalDateTime(dateStr);
            return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to parse Date: " + dateStr, e);
        }
    }

    /**
     * 解析LocalTime，尝试多种格式
     */
    public static LocalTime parseLocalTime(String timeStr) {
        if (timeStr == null || timeStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Time string cannot be null or empty");
        }
        for (DateTimeFormatter formatter : LOCAL_TIME_FORMATTERS) {
            try {
                return LocalTime.parse(timeStr, formatter);
            } catch (Exception e) {
                // 忽略，尝试下一个格式
            }
        }
        throw new IllegalArgumentException("Unable to parse LocalTime: " + timeStr);
    }
}