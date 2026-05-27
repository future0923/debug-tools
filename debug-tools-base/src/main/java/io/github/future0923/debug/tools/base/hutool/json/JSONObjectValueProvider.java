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
package io.github.future0923.debug.tools.base.hutool.json;

import io.github.future0923.debug.tools.base.hutool.core.bean.copier.ValueProvider;
import io.github.future0923.debug.tools.base.hutool.core.convert.Convert;
import io.github.future0923.debug.tools.base.hutool.core.util.StrUtil;

import java.lang.reflect.Type;

/**
 * JSONObject值提供者，用于将JSONObject中的值注入Bean<br>
 * 兼容下划线模式的JSON转换为驼峰模式
 *
 * @author Looly
 */
public class JSONObjectValueProvider implements ValueProvider<String> {

    private final JSONObject jsonObject;

    private final boolean ignoreError;

    /**
     * 构造
     *
     * @param jsonObject   JSON对象
     * @param ignoreError 是否忽略转换错误
     */
    public JSONObjectValueProvider(final JSONObject jsonObject, final boolean ignoreError) {
        this.jsonObject = jsonObject;
        this.ignoreError = ignoreError;
    }

    @Override
    public boolean containsKey(final String key) {
        return jsonObject.containsKey(key) || jsonObject.containsKey(StrUtil.toUnderlineCase(key));
    }

    @Override
    public Object value(final String key, final Type valueType) {
        Object value = jsonObject.getObj(key);
        if (null == value) {
            value = jsonObject.getObj(StrUtil.toUnderlineCase(key));
            if (null == value) {
                return null;
            }
        }
        if (value instanceof JSON) {
            return ((JSON) value).toBean(valueType, ignoreError);
        }
        return Convert.convertWithCheck(valueType, value, null, ignoreError);
    }
}
