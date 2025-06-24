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
package io.github.future0923.debug.tools.base.hutool.core.annotation.scanner;

import io.github.future0923.debug.tools.base.hutool.core.annotation.AnnotationUtil;
import io.github.future0923.debug.tools.base.hutool.core.util.ObjectUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

/**
 * 扫描{@link Field}上的注解
 *
 * @author huangchengxing
 */
public class FieldAnnotationScanner implements AnnotationScanner {

	/**
	 * 判断是否支持扫描该注解元素，仅当注解元素是{@link Field}时返回{@code true}
	 *
	 * @param annotatedEle {@link AnnotatedElement}，可以是Class、Method、Field、Constructor、ReflectPermission
	 * @return 是否支持扫描该注解元素
	 */
	@Override
	public boolean support(AnnotatedElement annotatedEle) {
		return annotatedEle instanceof Field;
	}

	/**
	 * 扫描{@link Field}上直接声明的注解，调用前需要确保调用{@link #support(AnnotatedElement)}返回为true
	 *
	 * @param consumer     对获取到的注解和注解对应的层级索引的处理
	 * @param annotatedEle {@link AnnotatedElement}，可以是Class、Method、Field、Constructor、ReflectPermission
	 * @param filter       注解过滤器，无法通过过滤器的注解不会被处理。该参数允许为空。
	 */
	@Override
	public void scan(BiConsumer<Integer, Annotation> consumer, AnnotatedElement annotatedEle, Predicate<Annotation> filter) {
		filter = ObjectUtil.defaultIfNull(filter, a -> annotation -> true);
		for (final Annotation annotation : annotatedEle.getAnnotations()) {
			if (AnnotationUtil.isNotJdkMateAnnotation(annotation.annotationType()) && filter.test(annotation)) {
				consumer.accept(0, annotation);
			}
		}
	}

}
