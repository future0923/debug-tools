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
