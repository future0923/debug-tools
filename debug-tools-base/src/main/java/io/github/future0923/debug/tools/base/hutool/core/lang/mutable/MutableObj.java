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
package io.github.future0923.debug.tools.base.hutool.core.lang.mutable;

import io.github.future0923.debug.tools.base.hutool.core.util.ObjUtil;

import java.io.Serializable;

/**
 * 可变{@code Object}
 *
 * @param <T> 可变的类型
 * @since 3.0.1
 */
public class MutableObj<T> implements Mutable<T>, Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 构建MutableObj
	 * @param value 被包装的值
	 * @param <T> 值类型
	 * @return MutableObj
	 * @since 5.8.0
	 */
	public static <T> MutableObj<T> of(T value){
		return new MutableObj<>(value);
	}

	private T value;

	/**
	 * 构造，空值
	 */
	public MutableObj() {
	}

	/**
	 * 构造
	 *
	 * @param value 值
	 */
	public MutableObj(final T value) {
		this.value = value;
	}

	// -----------------------------------------------------------------------
	@Override
	public T get() {
		return this.value;
	}

	@Override
	public void set(final T value) {
		this.value = value;
	}

	// -----------------------------------------------------------------------
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (this.getClass() == obj.getClass()) {
			final MutableObj<?> that = (MutableObj<?>) obj;
			return ObjUtil.equals(this.value, that.value);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return value == null ? 0 : value.hashCode();
	}

	// -----------------------------------------------------------------------
	@Override
	public String toString() {
		return value == null ? "null" : value.toString();
	}

}
