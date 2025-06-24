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

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Iterator;

/**
 * {@link Iterator}对象转{@link Enumeration}
 * @author Looly
 *
 * @param <E> 元素类型
 * @since 3.0.8
 */
public class IteratorEnumeration<E> implements Enumeration<E>, Serializable{
	private static final long serialVersionUID = 1L;

	private final Iterator<E> iterator;

	/**
	 * 构造
	 * @param iterator {@link Iterator}对象
	 */
	public IteratorEnumeration(Iterator<E> iterator) {
		this.iterator = iterator;
	}

	@Override
	public boolean hasMoreElements() {
		return iterator.hasNext();
	}

	@Override
	public E nextElement() {
		return iterator.next();
	}

}
