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
import io.github.future0923.debug.tools.base.hutool.core.util.ClassLoaderUtil;

/**
 * 类转换器<br>
 * 将类名转换为类，默认初始化这个类（执行static块）
 *
 * @author Looly
 */
public class ClassConverter extends AbstractConverter<Class<?>> {
	private static final long serialVersionUID = 1L;

	private final boolean isInitialized;

	/**
	 * 构造
	 */
	public ClassConverter() {
		this(true);
	}

	/**
	 * 构造
	 *
	 * @param isInitialized 是否初始化类（调用static模块内容和初始化static属性）
	 * @since 5.5.0
	 */
	public ClassConverter(boolean isInitialized) {
		this.isInitialized = isInitialized;
	}

	@Override
	protected Class<?> convertInternal(Object value) {
		return ClassLoaderUtil.loadClass(convertToStr(value), isInitialized);
	}

}
