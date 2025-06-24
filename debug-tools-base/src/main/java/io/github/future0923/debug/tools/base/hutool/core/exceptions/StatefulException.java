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
 * 带有状态码的异常
 *
 * @author xiaoleilu
 */
public class StatefulException extends RuntimeException {
	private static final long serialVersionUID = 6057602589533840889L;

	// 异常状态码
	private int status;

	public StatefulException() {
	}

	public StatefulException(String msg) {
		super(msg);
	}

	public StatefulException(String messageTemplate, Object... params) {
		super(StrUtil.format(messageTemplate, params));
	}

	public StatefulException(Throwable throwable) {
		super(throwable);
	}

	public StatefulException(String msg, Throwable throwable) {
		super(msg, throwable);
	}

	public StatefulException(String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace) {
		super(message, throwable, enableSuppression, writableStackTrace);
	}

	public StatefulException(int status, String msg) {
		super(msg);
		this.status = status;
	}

	public StatefulException(int status, Throwable throwable) {
		super(throwable);
		this.status = status;
	}

	public StatefulException(int status, String msg, Throwable throwable) {
		super(msg, throwable);
		this.status = status;
	}

	/**
	 * @return 获得异常状态码
	 */
	public int getStatus() {
		return status;
	}
}
