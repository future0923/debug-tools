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
package io.github.future0923.debug.tools.base.hutool.core.lang.caller;

import io.github.future0923.debug.tools.base.hutool.core.exceptions.UtilException;

import java.io.Serializable;

/**
 * 通过StackTrace方式获取调用者。此方式效率最低，不推荐使用
 *
 * @author Looly
 */
public class StackTraceCaller implements Caller, Serializable {
	private static final long serialVersionUID = 1L;
	private static final int OFFSET = 2;

	@Override
	public Class<?> getCaller() {
		final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		if (OFFSET + 1 >= stackTrace.length) {
			return null;
		}
		final String className = stackTrace[OFFSET + 1].getClassName();
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new UtilException(e, "[{}] not found!", className);
		}
	}

	@Override
	public Class<?> getCallerCaller() {
		final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		if (OFFSET + 2 >= stackTrace.length) {
			return null;
		}
		final String className = stackTrace[OFFSET + 2].getClassName();
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new UtilException(e, "[{}] not found!", className);
		}
	}

	@Override
	public Class<?> getCaller(int depth) {
		final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		if (OFFSET + depth >= stackTrace.length) {
			return null;
		}
		final String className = stackTrace[OFFSET + depth].getClassName();
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new UtilException(e, "[{}] not found!", className);
		}
	}

	@Override
	public boolean isCalledBy(Class<?> clazz) {
		final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		for (final StackTraceElement element : stackTrace) {
			if (element.getClassName().equals(clazz.getName())) {
				return true;
			}
		}
		return false;
	}
}
