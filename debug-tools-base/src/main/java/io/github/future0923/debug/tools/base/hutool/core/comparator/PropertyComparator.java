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

import io.github.future0923.debug.tools.base.hutool.core.bean.BeanUtil;

/**
 * Bean属性排序器<br>
 * 支持读取Bean多层次下的属性
 *
 * @param <T> 被比较的Bean
 * @author Looly
 */
public class PropertyComparator<T> extends FuncComparator<T> {
	private static final long serialVersionUID = 9157326766723846313L;

	/**
	 * 构造
	 *
	 * @param property 属性名
	 */
	public PropertyComparator(String property) {
		this(property, true);
	}

	/**
	 * 构造
	 *
	 * @param property      属性名
	 * @param isNullGreater null值是否排在后（从小到大排序）
	 */
	public PropertyComparator(String property, boolean isNullGreater) {
		this(property, true, isNullGreater);
	}

	/**
	 * 构造
	 *
	 * @param property      属性名
	 * @param compareSelf   在字段值相同情况下，是否比较对象本身。
	 *                      如果此项为{@code false}，字段值比较后为0会导致对象被认为相同，可能导致被去重。
	 * @param isNullGreater null值是否排在后（从小到大排序）
	 * @since 5.8.28
	 */
	public PropertyComparator(String property, final boolean compareSelf, boolean isNullGreater) {
		super(isNullGreater, compareSelf, (bean) -> BeanUtil.getProperty(bean, property));
	}
}
