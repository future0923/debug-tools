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
package io.github.future0923.debug.tools.base.hutool.core.compiler;

import io.github.future0923.debug.tools.base.hutool.core.exceptions.ExceptionUtil;
import io.github.future0923.debug.tools.base.hutool.core.util.StrUtil;

/**
 * 编译异常
 *
 * @author looly
 * @since 5.5.2
 */
public class CompilerException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public CompilerException(Throwable e) {
		super(ExceptionUtil.getMessage(e), e);
	}

	public CompilerException(String message) {
		super(message);
	}

	public CompilerException(String messageTemplate, Object... params) {
		super(StrUtil.format(messageTemplate, params));
	}

	public CompilerException(String message, Throwable throwable) {
		super(message, throwable);
	}

	public CompilerException(Throwable throwable, String messageTemplate, Object... params) {
		super(StrUtil.format(messageTemplate, params), throwable);
	}
}
