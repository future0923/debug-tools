/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
