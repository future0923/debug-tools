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
