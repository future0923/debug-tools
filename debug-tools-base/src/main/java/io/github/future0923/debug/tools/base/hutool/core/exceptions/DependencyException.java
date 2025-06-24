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
package io.github.future0923.debug.tools.base.hutool.core.exceptions;

import io.github.future0923.debug.tools.base.hutool.core.util.StrUtil;

/**
 * 依赖异常
 *
 * @author xiaoleilu
 * @since 4.0.10
 */
public class DependencyException extends RuntimeException {
	private static final long serialVersionUID = 8247610319171014183L;

	public DependencyException(Throwable e) {
		super(ExceptionUtil.getMessage(e), e);
	}

	public DependencyException(String message) {
		super(message);
	}

	public DependencyException(String messageTemplate, Object... params) {
		super(StrUtil.format(messageTemplate, params));
	}

	public DependencyException(String message, Throwable throwable) {
		super(message, throwable);
	}

	public DependencyException(String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace) {
		super(message, throwable, enableSuppression, writableStackTrace);
	}

	public DependencyException(Throwable throwable, String messageTemplate, Object... params) {
		super(StrUtil.format(messageTemplate, params), throwable);
	}
}
