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
package io.github.future0923.debug.tools.base.hutool.core.io;

import io.github.future0923.debug.tools.base.hutool.core.exceptions.ExceptionUtil;
import io.github.future0923.debug.tools.base.hutool.core.util.StrUtil;

/**
 * IO运行时异常，常用于对IOException的包装
 *
 * @author xiaoleilu
 */
public class IORuntimeException extends RuntimeException {
	private static final long serialVersionUID = 8247610319171014183L;

	public IORuntimeException(Throwable e) {
		super(ExceptionUtil.getMessage(e), e);
	}

	public IORuntimeException(String message) {
		super(message);
	}

	public IORuntimeException(String messageTemplate, Object... params) {
		super(StrUtil.format(messageTemplate, params));
	}

	public IORuntimeException(String message, Throwable throwable) {
		super(message, throwable);
	}

	public IORuntimeException(Throwable throwable, String messageTemplate, Object... params) {
		super(StrUtil.format(messageTemplate, params), throwable);
	}

	/**
	 * 导致这个异常的异常是否是指定类型的异常
	 *
	 * @param clazz 异常类
	 * @return 是否为指定类型异常
	 */
	public boolean causeInstanceOf(Class<? extends Throwable> clazz) {
		final Throwable cause = this.getCause();
		return null != clazz && clazz.isInstance(cause);
	}
}
