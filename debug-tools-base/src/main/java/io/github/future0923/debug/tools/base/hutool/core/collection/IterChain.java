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

import io.github.future0923.debug.tools.base.hutool.core.lang.Chain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * 组合{@link Iterator}，将多个{@link Iterator}组合在一起，便于集中遍历。<br>
 * 来自Jodd
 *
 * @param <T> 元素类型
 * @author looly, jodd
 */
public class IterChain<T> implements Iterator<T>, Chain<Iterator<T>, IterChain<T>> {

	protected final List<Iterator<T>> allIterators = new ArrayList<>();

	/**
	 * 构造
	 * 可以使用 {@link #addChain(Iterator)} 方法加入更多的集合。
	 */
	public IterChain() {
	}

	/**
	 * 构造
	 * @param iterators 多个{@link Iterator}
	 */
	@SafeVarargs
	public IterChain(Iterator<T>... iterators) {
		for (final Iterator<T> iterator : iterators) {
			addChain(iterator);
		}
	}

	@Override
	public IterChain<T> addChain(Iterator<T> iterator) {
		if (allIterators.contains(iterator)) {
			throw new IllegalArgumentException("Duplicate iterator");
		}
		allIterators.add(iterator);
		return this;
	}

	// ---------------------------------------------------------------- interface

	protected int currentIter = -1;

	@Override
	public boolean hasNext() {
		if (currentIter == -1) {
			currentIter = 0;
		}

		final int size = allIterators.size();
		for (int i = currentIter; i < size; i++) {
			final Iterator<T> iterator = allIterators.get(i);
			if (iterator.hasNext()) {
				currentIter = i;
				return true;
			}
		}
		return false;
	}

	@Override
	public T next() {
		if (false == hasNext()) {
			throw new NoSuchElementException();
		}

		return allIterators.get(currentIter).next();
	}

	@Override
	public void remove() {
		if (-1 == currentIter) {
			throw new IllegalStateException("next() has not yet been called");
		}

		allIterators.get(currentIter).remove();
	}

	@Override
	public Iterator<Iterator<T>> iterator() {
		return this.allIterators.iterator();
	}
}
