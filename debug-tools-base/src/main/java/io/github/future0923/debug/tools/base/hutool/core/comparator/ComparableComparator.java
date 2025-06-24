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
 * 针对 {@link Comparable}对象的默认比较器
 *
 * @param <E> 比较对象类型
 * @author Looly
 * @since 3.0.7
 */
public class ComparableComparator<E extends Comparable<? super E>> implements Comparator<E>, Serializable {
	private static final long serialVersionUID = 3020871676147289162L;

	/** 单例 */
	@SuppressWarnings("rawtypes")
	public static final ComparableComparator INSTANCE = new ComparableComparator<>();

	/**
	 * 构造
	 */
	public ComparableComparator() {
	}

	/**
	 * 比较两个{@link Comparable}对象
	 *
	 * <pre>
	 * obj1.compareTo(obj2)
	 * </pre>
	 *
	 * @param obj1 被比较的第一个对象
	 * @param obj2 the second object to compare
	 * @return obj1小返回负数，大返回正数，否则返回0
	 * @throws NullPointerException obj1为{@code null}或者比较中抛出空指针异常
	 */
	@Override
	public int compare(final E obj1, final E obj2) {
		return obj1.compareTo(obj2);
	}

	@Override
	public int hashCode() {
		return "ComparableComparator".hashCode();
	}

	@Override
	public boolean equals(final Object object) {
		return this == object || null != object && object.getClass().equals(this.getClass());
	}

}