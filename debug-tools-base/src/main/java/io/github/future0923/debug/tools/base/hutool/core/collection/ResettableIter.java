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
package io.github.future0923.debug.tools.base.hutool.core.collection;

import java.util.Iterator;

/**
 * 支持重置的{@link Iterator} 接口<br>
 * 通过实现{@link #reset()}，重置此{@link Iterator}后可实现复用重新遍历
 *
 * @param <E> 元素类型
 * @since 5.8.0
 */
public interface ResettableIter<E> extends Iterator<E> {

	/**
	 * 重置，重置后可重新遍历
	 */
	void reset();
}
