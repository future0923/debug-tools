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
package io.github.future0923.debug.tools.base.hutool.core.lang;

/**
 * 片段默认实现
 *
 * @param <T> 数字类型，用于表示位置index
 * @author looly
 * @since 5.5.3
 */
public class DefaultSegment<T extends Number> implements Segment<T> {

	protected T startIndex;
	protected T endIndex;

	/**
	 * 构造
	 * @param startIndex 起始位置
	 * @param endIndex 结束位置
	 */
	public DefaultSegment(T startIndex, T endIndex) {
		this.startIndex = startIndex;
		this.endIndex = endIndex;
	}

	@Override
	public T getStartIndex() {
		return this.startIndex;
	}

	@Override
	public T getEndIndex() {
		return this.endIndex;
	}
}
