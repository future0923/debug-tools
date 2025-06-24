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
import io.github.future0923.debug.tools.base.hutool.core.util.StrUtil;
import io.github.future0923.debug.tools.base.hutool.core.util.TypeUtil;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;

/**
 * {@link Reference}转换器
 *
 * @author Looly
 * @since 3.0.8
 */
@SuppressWarnings("rawtypes")
public class ReferenceConverter extends AbstractConverter<Reference> {
	private static final long serialVersionUID = 1L;

	private final Class<? extends Reference> targetType;

	/**
	 * 构造
	 * @param targetType {@link Reference}实现类型
	 */
	public ReferenceConverter(Class<? extends Reference> targetType) {
		this.targetType = targetType;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Reference<?> convertInternal(Object value) {

		//尝试将值转换为Reference泛型的类型
		Object targetValue = null;
		final Type paramType = TypeUtil.getTypeArgument(targetType);
		if(false == TypeUtil.isUnknown(paramType)){
			targetValue = ConverterRegistry.getInstance().convert(paramType, value);
		}
		if(null == targetValue){
			targetValue = value;
		}

		if(this.targetType == WeakReference.class){
			return new WeakReference(targetValue);
		}else if(this.targetType == SoftReference.class){
			return new SoftReference(targetValue);
		}

		throw new UnsupportedOperationException(StrUtil.format("Unsupport Reference type: {}", this.targetType.getName()));
	}

}
