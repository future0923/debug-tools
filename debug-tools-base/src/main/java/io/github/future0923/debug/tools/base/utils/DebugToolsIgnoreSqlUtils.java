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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * sql打印时忽略内容
 */
public class DebugToolsIgnoreSqlUtils {

    private static final Logger logger = Logger.getLogger(DebugToolsIgnoreSqlUtils.class);

    /**
     * 缓存
     */
    private static Map<String, Set<Pattern>> CACHE = new HashMap<>();

    /**
     * 配置文件
     */
    private static File configFile;

    /**
     * 打印哪些包下的 sql
     */
    private static final String SQL_PRINT_PACKAGES = "sql.print.packages";

    /**
     * 忽略哪些包下的 sql
     */
    private static final String SQL_PRINT_IGNORE_PACKAGES = "sql.print.ignore-packages";

    /**
     * 打印哪些 sql 语句
     */
    private static final String SQL_PRINT_STATEMENT = "sql.print.statement";

    /**
     * 忽略哪些 sql 语句
     */
    private static final String SQL_PRINT_IGNORE_STATEMENT = "sql.print.ignore-statement";

    public static Set<Pattern> getSqlPrintPackages() {
        return CACHE.get(SQL_PRINT_PACKAGES);
    }

    public static Set<Pattern> getSqlPrintIgnorePackages() {
        return CACHE.get(SQL_PRINT_IGNORE_PACKAGES);
    }

    public static Set<Pattern> getSqlPrintStatement() {
        return CACHE.get(SQL_PRINT_STATEMENT);
    }

    public static Set<Pattern> getSqlPrintIgnoreStatement() {
        return CACHE.get(SQL_PRINT_IGNORE_STATEMENT);
    }

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
    public static Map<String, Set<Pattern>> getContentRuleMap(File file) {
        if (file == null || !file.exists() || !file.isFile()) {
            return Collections.emptyMap();
        }
        Map<String, Set<Pattern>> newCache = new HashMap<>();
        String currentSection = null;
        List<String> lines = FileUtil.readLines(file, StandardCharsets.UTF_8);
        for (String line : lines) {
            String trim = StrUtil.trim(line);
            if (StrUtil.isBlank(trim)) {
                continue;
            }
            if (line.startsWith("[[") && line.endsWith("]]")) {
                currentSection = line.substring(2, line.length() - 2).trim();
                newCache.putIfAbsent(currentSection, new HashSet<>());
                continue;
            }
            if (currentSection == null) {
                continue;
            }
            try {
                if (SQL_PRINT_STATEMENT.equals(currentSection)
                    || SQL_PRINT_IGNORE_STATEMENT.equals(currentSection)) {
                    newCache.get(currentSection).add(Pattern.compile(Pattern.quote(line)));
                }
                newCache.get(currentSection).add(Pattern.compile(line));
            } catch (PatternSyntaxException e) {
                newCache.get(currentSection).add(Pattern.compile(Pattern.quote(line)));
            }
        }
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
        logger.debug("watch ignore sql config file: {}", configFile.getAbsolutePath());
    }

}
