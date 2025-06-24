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
import io.github.future0923.debug.tools.base.hutool.core.util.ReflectUtil;
import io.github.future0923.debug.tools.base.hutool.core.util.StrUtil;

import java.lang.reflect.Field;

/**
 * Bean字段排序器<br>
 * 参阅feilong-core中的PropertyComparator
 *
 * @param <T> 被比较的Bean
 * @author Looly
 */
public class FieldComparator<T> extends FuncComparator<T> {
	private static final long serialVersionUID = 9157326766723846313L;

	/**
	 * 构造
	 *
	 * @param beanClass Bean类
	 * @param fieldName 字段名
	 */
	public FieldComparator(Class<T> beanClass, String fieldName) {
		this(getNonNullField(beanClass, fieldName));
	}

	/**
	 * 构造
	 *
	 * @param field 字段
	 */
	public FieldComparator(Field field) {
		this(true, true, field);
	}

	/**
	 * 构造
	 *
	 * @param nullGreater 是否{@code null}在后
	 * @param compareSelf 在字段值相同情况下，是否比较对象本身。
	 *                    如果此项为{@code false}，字段值比较后为0会导致对象被认为相同，可能导致被去重。
	 * @param field       字段
	 */
	public FieldComparator(boolean nullGreater, boolean compareSelf, Field field) {
		super(nullGreater, compareSelf, (bean) ->
			(Comparable<?>) ReflectUtil.getFieldValue(bean,
				Assert.notNull(field, "Field must be not null!")));
	}

	/**
	 * 获取字段，附带检查字段不存在的问题。
	 *
	 * @param beanClass Bean类
	 * @param fieldName 字段名
	 * @return 非null字段
	 */
	private static Field getNonNullField(Class<?> beanClass, String fieldName) {
		final Field field = ClassUtil.getDeclaredField(beanClass, fieldName);
		if (field == null) {
			throw new IllegalArgumentException(StrUtil.format("Field [{}] not found in Class [{}]", fieldName, beanClass.getName()));
		}
		return field;
	}
}
