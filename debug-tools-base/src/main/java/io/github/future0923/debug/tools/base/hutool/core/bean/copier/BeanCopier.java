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
package io.github.future0923.debug.tools.base.hutool.core.bean.copier;

import io.github.future0923.debug.tools.base.hutool.core.lang.Assert;
import io.github.future0923.debug.tools.base.hutool.core.lang.copier.Copier;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Bean拷贝，提供：
 *
 * <pre>
 *     1. Bean 转 Bean
 *     2. Bean 转 Map
 *     3. Map  转 Bean
 *     4. Map  转 Map
 * </pre>
 *
 * @author looly
 *
 * @param <T> 目标对象类型
 * @since 3.2.3
 */
public class BeanCopier<T> implements Copier<T>, Serializable {
	private static final long serialVersionUID = 1L;

	private final Copier<T> copier;

	/**
	 * 创建BeanCopier
	 *
	 * @param <T> 目标Bean类型
	 * @param source 来源对象，可以是Bean或者Map
	 * @param target 目标Bean对象
	 * @param copyOptions 拷贝属性选项
	 * @return BeanCopier
	 */
	public static <T> BeanCopier<T> create(Object source, T target, CopyOptions copyOptions) {
		return create(source, target, target.getClass(), copyOptions);
	}

	/**
	 * 创建BeanCopier
	 *
	 * @param <T> 目标Bean类型
	 * @param source 来源对象，可以是Bean或者Map
	 * @param target 目标Bean对象
	 * @param destType 目标的泛型类型，用于标注有泛型参数的Bean对象
	 * @param copyOptions 拷贝属性选项
	 * @return BeanCopier
	 */
	public static <T> BeanCopier<T> create(Object source, T target, Type destType, CopyOptions copyOptions) {
		return new BeanCopier<>(source, target, destType, copyOptions);
	}

	/**
	 * 构造
	 *
	 * @param source 来源对象，可以是Bean或者Map
	 * @param target 目标Bean对象
	 * @param targetType 目标的泛型类型，用于标注有泛型参数的Bean对象
	 * @param copyOptions 拷贝属性选项
	 */
	public BeanCopier(Object source, T target, Type targetType, CopyOptions copyOptions) {
		Assert.notNull(source, "Source bean must be not null!");
		Assert.notNull(target, "Target bean must be not null!");
		Copier<T> copier;
		if (source instanceof Map) {
			if (target instanceof Map) {
				//noinspection unchecked
				copier = (Copier<T>) new MapToMapCopier((Map<?, ?>) source, (Map<?, ?>) target, targetType, copyOptions);
			} else {
				copier = new MapToBeanCopier<>((Map<?, ?>) source, target, targetType, copyOptions);
			}
		}else if(source instanceof ValueProvider){
			//noinspection unchecked
			copier = new ValueProviderToBeanCopier<>((ValueProvider<String>) source, target, targetType, copyOptions);
		} else {
			if (target instanceof Map) {
				//noinspection unchecked
				copier = (Copier<T>) new BeanToMapCopier(source, (Map<?, ?>) target, targetType, copyOptions);
			} else {
				copier = new BeanToBeanCopier<>(source, target, targetType, copyOptions);
			}
		}
		this.copier = copier;
	}

	@Override
	public T copy() {
		return copier.copy();
	}
}
