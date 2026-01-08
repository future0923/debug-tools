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
package io.github.future0923.debug.tools.base.utils;

import io.github.future0923.debug.tools.base.hutool.core.io.FileUtil;
import io.github.future0923.debug.tools.base.hutool.core.io.LineHandler;
import io.github.future0923.debug.tools.base.hutool.core.io.watch.SimpleWatcher;
import io.github.future0923.debug.tools.base.hutool.core.io.watch.WatchMonitor;
import io.github.future0923.debug.tools.base.hutool.core.io.watch.WatchUtil;
import io.github.future0923.debug.tools.base.hutool.core.io.watch.watchers.DelayWatcher;
import io.github.future0923.debug.tools.base.hutool.core.util.StrUtil;
import io.github.future0923.debug.tools.base.logging.Logger;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 热重载时忽略哪些静态字段
 *
 * @author future0923
 */
public class HotswapIgnoreStaticFieldUtils {

    private static final Logger logger = Logger.getLogger(HotswapIgnoreStaticFieldUtils.class);

    /**
     * 缓存
     */
    private static Map<String, Set<String>> CACHE = new HashMap<>();

    /**
     * 配置文件
     */
    private static File configFile;

    /**
     * 创建
     */
    public static void create(String pathname) {
        if (StrUtil.isBlank(pathname)) {
            return;
        }
        configFile = new File(pathname);
        reload();
        watch();
    }

    /**
     * 重新加载配置文件到缓存
     */
    private static synchronized void reload() {
        CACHE = getContentRuleMap(configFile);
    }

    /**
     * 获取指定路径的配置文件规则内容
     */
    public static Map<String, Set<String>> getContentRuleMap(String pathname) {
        if (StrUtil.isBlank(pathname)) {
            return Collections.emptyMap();
        }
        return getContentRuleMap(new File(pathname));
    }

    /**
     * 获取指定路径的配置文件规则内容
     */
    public static Map<String, Set<String>> getContentRuleMap(File file) {
        if (file == null || !file.exists() || !file.isFile()) {
            return Collections.emptyMap();
        }
        Map<String, Set<String>> newCache = new HashMap<>();
        FileUtil.readLines(file, StandardCharsets.UTF_8, (LineHandler) line -> {
            String trim = StrUtil.trim(line);
            if (StrUtil.isBlank(trim) || trim.startsWith("#") || trim.startsWith(";")) {
                return;
            }
            String className, fieldName;
            String[] split = trim.split("#");
            if (split.length == 2) {
                className = split[0];
                fieldName = split[1];
            } else {
                int index = trim.lastIndexOf(".");
                if (index < 0) {
                    return;
                }
                className = trim.substring(0, index);
                fieldName = trim.substring(index + 1);
            }
            newCache.computeIfAbsent(className, k -> ConcurrentHashMap.newKeySet()).add(fieldName);
        });
        return newCache;
    }

    /**
     * 监听
     */
    private static void watch() {
        WatchMonitor monitor = WatchUtil.createAll(configFile, new DelayWatcher(new SimpleWatcher() {

            @Override
            public void onCreate(WatchEvent<?> event, Path currentPath) {
                logger.debug("watch {} create", currentPath);
                reload();
            }

            @Override
            public void onModify(WatchEvent<?> event, Path currentPath) {
                logger.debug("watch {} modify", currentPath);
                reload();
            }

            @Override
            public void onDelete(WatchEvent<?> event, Path currentPath) {
                logger.debug("watch {} delete", currentPath);
                reload();
            }
        }, 1000));
        monitor.start();
        logger.debug("watch hotswap ignore static field file: {}", configFile.getAbsolutePath());
    }

    /**
     * 是否忽略
     *
     * @param className 类名
     * @param fieldName 字段名
     * @return 是否忽略
     */
    public static boolean isIgnored(String className, String fieldName) {
        return isIgnored(className, fieldName, CACHE);
    }

    /**
     * 是否忽略
     *
     * @param className 类名
     * @param fieldName 字段名
     * @return 是否忽略
     */
    public static boolean isIgnored(String className, String fieldName, Map<String, Set<String>> ruleMap) {
        Set<String> fields = ruleMap.get(className);
        if (fields == null) {
            return false;
        }
        return fields.contains("*") || fields.contains(fieldName);
    }

    /**
     * 删除指定的配置
     *
     * @param file          文件
     * @param className     类名
     * @param fieldName     字段名
     */
    public static void remove(File file, String className, String fieldName) {
        if (file == null || !file.exists() || !file.isFile()) {
            return;
        }
        List<String> lines = new LinkedList<>();
        FileUtil.readLines(file, StandardCharsets.UTF_8, (LineHandler) line -> {
            String trim = StrUtil.trim(line);
            if (StrUtil.isBlank(trim) || trim.startsWith("#") || trim.startsWith(";")) {
                lines.add(line);
                return;
            }
            String classNameConf, fieldNameConf;
            String[] split = trim.split("#");
            if (split.length == 2) {
                classNameConf = split[0];
                fieldNameConf = split[1];
            } else {
                int index = trim.lastIndexOf(".");
                if (index < 0) {
                    lines.add(line);
                    return;
                }
                classNameConf = trim.substring(0, index);
                fieldNameConf = trim.substring(index + 1);
            }
            if (StrUtil.equals(className, classNameConf)
                    && ("*".equals(fieldNameConf) || StrUtil.equals(fieldName, fieldNameConf))) {
                return;
            }
            lines.add(line);
        });
        FileUtil.writeLines(lines, file, StandardCharsets.UTF_8);
    }
}
