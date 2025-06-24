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

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 配置下划线会转为小驼峰
 */
public class DebugToolsProperties extends Properties {

    private static final long serialVersionUID = 4467598209091707788L;

    private static final Pattern VAR_PATTERN = Pattern.compile("\\$\\{([a-zA-Z0-9._]+?)\\}");

    @Override
    public Object put(Object key, Object value) {
        return super.put(key, substitute(value));
    }

    private Object substitute(Object obj) {
        if (obj instanceof String) {
            StringBuffer result = new StringBuffer();
            Matcher m = VAR_PATTERN.matcher((String) obj);
            while (m.find()) {
                String replacement = System.getProperty(m.group(1));
                if (replacement != null) {
                    m.appendReplacement(result, replacement);
                }
            }
            m.appendTail(result);
            return result.toString();
        }

        return obj;
    }

}
