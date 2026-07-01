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
package io.github.future0923.debug.tools.server.http.handler;

import com.sun.net.httpserver.Headers;
import io.github.future0923.debug.tools.base.hutool.core.util.StrUtil;
import io.github.future0923.debug.tools.server.utils.DebugToolsEnvUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 读取附着应用运行时 Spring 配置。
 *
 * @author future0923
 */
public class SpringConfigHttpHandler extends BaseHttpHandler<String[], Map<String, Object>> {

    public static final SpringConfigHttpHandler INSTANCE = new SpringConfigHttpHandler();

    public static final String PATH = "/spring/config";

    private SpringConfigHttpHandler() {

    }

    @Override
    protected Map<String, Object> doHandle(String[] req, Headers responseHeaders) {
        return loadSpringConfig(req);
    }

    static Map<String, Object> loadSpringConfig(String[] keys) {
        Map<String, Object> result = new LinkedHashMap<>();
        if (keys == null) {
            return result;
        }
        for (String key : keys) {
            if (StrUtil.isBlank(key)) {
                continue;
            }
            result.put(key, getSpringConfig(key));
        }
        return result;
    }

    private static Object getSpringConfig(String key) {
        try {
            return DebugToolsEnvUtils.getSpringConfig(key);
        } catch (Exception e) {
            return null;
        }
    }
}
