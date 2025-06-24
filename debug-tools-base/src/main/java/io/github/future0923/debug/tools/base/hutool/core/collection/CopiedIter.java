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
import java.util.Iterator;
import java.util.List;

/**
 * 复制 {@link Iterator}<br>
 * 为了解决并发情况下{@link Iterator}遍历导致的问题（当Iterator被修改会抛出ConcurrentModificationException）
 * ，故使用复制原Iterator的方式解决此问题。
 *
 * <p>
 * 解决方法为：在构造方法中遍历Iterator中的元素，装入新的List中然后遍历之。
 * 当然，修改这个复制后的Iterator是没有意义的，因此remove方法将会抛出异常。
 *
 * <p>
 * 需要注意的是，在构造此对象时需要保证原子性（原对象不被修改），最好加锁构造此对象，构造完毕后解锁。
 *
 * @param <E> 元素类型
 * @author Looly
 * @since 3.0.7
 */
public class CopiedIter<E> implements IterableIter<E>, Serializable {
	private static final long serialVersionUID = 1L;

	private final Iterator<E> listIterator;

	/**
	 * 根据已有{@link Iterator}，返回新的{@code CopiedIter}
	 *
	 * @param iterator {@link Iterator}
	 * @param <E>      元素类型
	 * @return {@code CopiedIter}
	 */
	public static <E> CopiedIter<E> copyOf(Iterator<E> iterator) {
		return new CopiedIter<>(iterator);
	}

	/**
	 * 构造
	 *
	 * @param iterator 被复制的Iterator
	 */
	public CopiedIter(Iterator<E> iterator) {
		final List<E> eleList = ListUtil.toList(iterator);
		this.listIterator = eleList.iterator();
	}

	@Override
	public boolean hasNext() {
		return this.listIterator.hasNext();
	}

	@Override
	public E next() {
		return this.listIterator.next();
	}

	/**
	 * 此对象不支持移除元素
	 *
	 * @throws UnsupportedOperationException 当调用此方法时始终抛出此异常
	 */
	@Override
	public void remove() throws UnsupportedOperationException {
		throw new UnsupportedOperationException("This is a read-only iterator.");
	}
}
