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
package io.github.future0923.debug.tools.idea.model;

import io.github.future0923.debug.tools.common.utils.DebugToolsJsonUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author future0923
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ParamCache {

    private Map<String, String> itemHeaderMap;

    private String paramContent;

    private String xxlJobParam;

    public static final ParamCache NULL = new ParamCache();

    public String formatContent() {
        if (null == paramContent) {
            return null;
        }
        return DebugToolsJsonUtils.pretty(paramContent);
    }
}