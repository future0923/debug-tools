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
import io.github.future0923.debug.tools.base.hutool.core.convert.ConvertException;

/**
 * 强转转换器
 *
 * @author Looly
 * @param <T> 强制转换到的类型
 * @since 4.0.2
 */
public class CastConverter<T> extends AbstractConverter<T> {
	private static final long serialVersionUID = 1L;

	private Class<T> targetType;

	@Override
	protected T convertInternal(Object value) {
		// 由于在AbstractConverter中已经有类型判断并强制转换，因此当在上一步强制转换失败时直接抛出异常
		throw new ConvertException("Can not cast value to [{}]", this.targetType);
	}

	@Override
	public Class<T> getTargetType() {
		return this.targetType;
	}
}
