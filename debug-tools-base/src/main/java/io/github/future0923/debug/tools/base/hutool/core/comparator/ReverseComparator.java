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
package io.github.future0923.debug.tools.base.hutool.core.comparator;


import java.io.Serializable;
import java.util.Comparator;

/**
 * 反转比较器
 *
 * @author Looly
 *
 * @param <E> 被比较对象类型
 */
public class ReverseComparator<E> implements Comparator<E>, Serializable {
	private static final long serialVersionUID = 8083701245147495562L;

	/** 原始比较器 */
	private final Comparator<? super E> comparator;

	@SuppressWarnings("unchecked")
	public ReverseComparator(Comparator<? super E> comparator) {
		this.comparator = (null == comparator) ? ComparableComparator.INSTANCE : comparator;
	}

	//-----------------------------------------------------------------------------------------------------
	@Override
	public int compare(E o1, E o2) {
		return comparator.compare(o2, o1);
	}

	@Override
	public int hashCode() {
		return "ReverseComparator".hashCode() ^ comparator.hashCode();
	}

	@Override
	public boolean equals(final Object object) {
		if (this == object) {
			return true;
		}
		if (null == object) {
			return false;
		}
		if (object.getClass().equals(this.getClass())) {
			final ReverseComparator<?> thatrc = (ReverseComparator<?>) object;
			return comparator.equals(thatrc.comparator);
		}
		return false;
	}
}
