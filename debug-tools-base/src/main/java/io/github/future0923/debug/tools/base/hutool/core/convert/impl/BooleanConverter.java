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
package io.github.future0923.debug.tools.base.hutool.core.convert.impl;

import io.github.future0923.debug.tools.base.hutool.core.convert.AbstractConverter;
import io.github.future0923.debug.tools.base.hutool.core.util.BooleanUtil;

/**
 * 布尔转换器
 *
 * <p>
 * 对象转为boolean，规则如下：
 * </p>
 * <pre>
 *     1、数字0为false，其它数字为true
 *     2、转换为字符串，形如"true", "yes", "y", "t", "ok", "1", "on", "是", "对", "真", "對", "√"为true，其它字符串为false.
 * </pre>
 *
 * @author Looly
 */
public class BooleanConverter extends AbstractConverter<Boolean> {
	private static final long serialVersionUID = 1L;

	@Override
	protected Boolean convertInternal(Object value) {
		if (value instanceof Number) {
			// 0为false，其它数字为true
			return 0 != ((Number) value).doubleValue();
		}
		//Object不可能出现Primitive类型，故忽略
		return BooleanUtil.toBoolean(convertToStr(value));
	}

}
