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
package io.github.future0923.debug.tools.base.hutool.core.annotation;

import java.lang.annotation.Annotation;

/**
 * 表示一组被聚合在一起的注解对象
 *
 * @author huangchengxing
 */
public interface AggregateAnnotation extends Annotation {

	/**
	 * 在聚合中是否存在的指定类型注解对象
	 *
	 * @param annotationType 注解类型
	 * @return 是否
	 */
	boolean isAnnotationPresent(Class<? extends Annotation> annotationType);

	/**
	 * 获取聚合中的全部注解对象
	 *
	 * @return 注解对象
	 */
	Annotation[] getAnnotations();

}
