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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

/**
 * @author future0923
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServerDisplayValue {

    private static final String separator = " ";

    private String key;

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    private String value;

    @Override
    public String toString() {
        return key + separator + value;
    }

    public static String display(String key, String value) {
        return key + separator + value;
    }

    public static ServerDisplayValue of(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        String[] split = text.split(separator);
        return new ServerDisplayValue(split[0], split[1]);
    }
}
