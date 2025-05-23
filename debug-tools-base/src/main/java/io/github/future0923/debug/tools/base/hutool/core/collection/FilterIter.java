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

import io.github.future0923.debug.tools.base.hutool.core.lang.Assert;
import io.github.future0923.debug.tools.base.hutool.core.lang.Filter;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 包装 {@link Iterator}并根据{@link Filter}定义，过滤元素输出<br>
 * 类实现来自Apache Commons Collection
 *
 * @author apache commons, looly
 * @since 5.8.0
 */
public class FilterIter<E> implements Iterator<E> {

	private final Iterator<? extends E> iterator;
	private final Filter<? super E> filter;

	/**
	 * 下一个元素
	 */
	private E nextObject;
	/**
	 * 标记下一个元素是否被计算
	 */
	private boolean nextObjectSet = false;

	/**
	 * 构造
	 *
	 * @param iterator 被包装的{@link Iterator}
	 * @param filter   过滤函数，{@code null}表示不过滤
	 */
	public FilterIter(final Iterator<? extends E> iterator, final Filter<? super E> filter) {
		this.iterator = Assert.notNull(iterator);
		this.filter = filter;
	}

	@Override
	public boolean hasNext() {
		return nextObjectSet || setNextObject();
	}

	@Override
	public E next() {
		if (false == nextObjectSet && false == setNextObject()) {
			throw new NoSuchElementException();
		}
		nextObjectSet = false;
		return nextObject;
	}

	@Override
	public void remove() {
		if (nextObjectSet) {
			throw new IllegalStateException("remove() cannot be called");
		}
		iterator.remove();
	}

	/**
	 * 获取被包装的{@link Iterator}
	 *
	 * @return {@link Iterator}
	 */
	public Iterator<? extends E> getIterator() {
		return iterator;
	}

	/**
	 * 获取过滤函数
	 *
	 * @return 过滤函数，可能为{@code null}
	 */
	public Filter<? super E> getFilter() {
		return filter;
	}

	/**
	 * 设置下一个元素，如果存在返回{@code true}，否则{@code false}
	 */
	private boolean setNextObject() {
		while (iterator.hasNext()) {
			final E object = iterator.next();
			if (null == filter || filter.accept(object)) {
				nextObject = object;
				nextObjectSet = true;
				return true;
			}
		}
		return false;
	}

}
