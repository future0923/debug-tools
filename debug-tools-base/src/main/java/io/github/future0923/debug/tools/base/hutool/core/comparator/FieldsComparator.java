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

import io.github.future0923.debug.tools.base.hutool.core.lang.Assert;
import io.github.future0923.debug.tools.base.hutool.core.util.ClassUtil;

import java.lang.reflect.Field;

/**
 * Bean字段排序器<br>
 * 参阅feilong-core中的PropertyComparator
 *
 * @param <T> 被比较的Bean
 * @author Looly
 */
public class FieldsComparator<T> extends NullComparator<T> {
	private static final long serialVersionUID = 8649196282886500803L;

	/**
	 * 构造
	 *
	 * @param beanClass  Bean类
	 * @param fieldNames 多个字段名
	 */
	public FieldsComparator(Class<T> beanClass, String... fieldNames) {
		this(true, beanClass, fieldNames);
	}

	/**
	 * 构造
	 *
	 * @param nullGreater 是否{@code null}在后
	 * @param beanClass   Bean类
	 * @param fieldNames  多个字段名
	 */
	public FieldsComparator(boolean nullGreater, Class<T> beanClass, String... fieldNames) {
		super(nullGreater, (a, b) -> {
			Field field;
			for (String fieldName : fieldNames) {
				field = ClassUtil.getDeclaredField(beanClass, fieldName);
				Assert.notNull(field, "Field [{}] not found in Class [{}]", fieldName, beanClass.getName());
				// issue#3259，多个字段比较时，允许字段值重复
				final int compare = new FieldComparator<>(true, false, field).compare(a, b);
				if (0 != compare) {
					return compare;
				}
			}
			return 0;
		});
	}

}
