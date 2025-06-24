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
package io.github.future0923.debug.tools.base.hutool.core.thread.threadlocal;

/**
 * 带有Name标识的 {@link ThreadLocal}，调用toString返回name
 *
 * @param <T> 值类型
 * @author looly
 * @since 4.1.4
 */
public class NamedThreadLocal<T> extends ThreadLocal<T> {

	private final String name;

	/**
	 * 构造
	 *
	 * @param name 名字
	 */
	public NamedThreadLocal(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name;
	}

}
