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