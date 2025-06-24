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
import io.github.future0923.debug.tools.base.hutool.core.convert.ConverterRegistry;
import io.github.future0923.debug.tools.base.hutool.core.util.TypeUtil;

import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicReference;

/**
 * {@link AtomicReference}转换器
 *
 * @author Looly
 * @since 3.0.8
 */
@SuppressWarnings("rawtypes")
public class AtomicReferenceConverter extends AbstractConverter<AtomicReference> {
	private static final long serialVersionUID = 1L;

	@Override
	protected AtomicReference<?> convertInternal(Object value) {

		//尝试将值转换为Reference泛型的类型
		Object targetValue = null;
		final Type paramType = TypeUtil.getTypeArgument(AtomicReference.class);
		if(false == TypeUtil.isUnknown(paramType)){
			targetValue = ConverterRegistry.getInstance().convert(paramType, value);
		}
		if(null == targetValue){
			targetValue = value;
		}

		return new AtomicReference<>(targetValue);
	}

}
