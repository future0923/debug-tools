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
